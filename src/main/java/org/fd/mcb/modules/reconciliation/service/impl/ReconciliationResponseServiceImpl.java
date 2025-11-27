package org.fd.mcb.modules.reconciliation.service.impl;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fd.mcb.configs.ReconciliationConfigProperties;
import org.fd.mcb.modules.auditlog.adapter.command.AuditLogCommandAdapter;
import org.fd.mcb.modules.master.adapter.command.BankAccountCommandAdapter;
import org.fd.mcb.modules.master.model.entity.BankAccount;
import org.fd.mcb.modules.master.model.repository.BankAccountRepository;
import org.fd.mcb.modules.reconciliation.adapter.command.AccountLimitsCommandAdapter;
import org.fd.mcb.modules.reconciliation.adapter.command.ReconciliationCommandAdapter;
import org.fd.mcb.modules.reconciliation.enums.AccountTier;
import org.fd.mcb.modules.reconciliation.enums.DiscrepancySeverity;
import org.fd.mcb.modules.reconciliation.model.entity.AccountLimits;
import org.fd.mcb.modules.reconciliation.model.entity.ReconciliationDiscrepancy;
import org.fd.mcb.modules.reconciliation.service.ReconciliationResponseService;
import org.fd.mcb.shared.exception.BankAccountNotFoundException;
import org.fd.mcb.shared.exception.ReconciliationPoolInsufficientFundsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReconciliationResponseServiceImpl implements ReconciliationResponseService {

    private final ReconciliationCommandAdapter reconciliationCommandAdapter;
    private final AccountLimitsCommandAdapter accountLimitsCommandAdapter;
    private final BankAccountCommandAdapter bankAccountCommandAdapter;
    private final BankAccountRepository bankAccountRepository;
    private final AuditLogCommandAdapter auditLogCommandAdapter;
    private final ReconciliationConfigProperties config;

    @Override
    @Transactional
    public void handleDiscrepancy(ReconciliationDiscrepancy discrepancy) {
        log.info("Handling discrepancy: Type={}, Severity={}, EntityType={}, EntityId={}",
            discrepancy.getDiscrepancyType(),
            discrepancy.getSeverity(),
            discrepancy.getEntityType(),
            discrepancy.getEntityId());

        // Always log to audit
        auditLog("DISCREPANCY_DETECTED", discrepancy);

        // Hybrid tiered response based on severity
        switch (discrepancy.getSeverity()) {
            case LOW -> handleLowSeverity(discrepancy);
            case MEDIUM -> handleMediumSeverity(discrepancy);
            case HIGH -> handleHighSeverity(discrepancy);
            case CRITICAL -> handleCriticalSeverity(discrepancy);
        }
    }

    private void handleLowSeverity(ReconciliationDiscrepancy discrepancy) {
        log.info("Low severity discrepancy - attempting auto-correction");

        if (!config.isAutoCorrectEnabled()) {
            log.info("Auto-correction disabled. Logging only.");
            return;
        }

        if ("BANK_ACCOUNT".equals(discrepancy.getEntityType())) {
            try {
                autoCorrectFromPool(discrepancy);
                discrepancy.setAutoCorrected(true);
                reconciliationCommandAdapter.saveDiscrepancy(discrepancy);
                auditLog("DISCREPANCY_AUTO_CORRECTED", discrepancy);
            } catch (Exception e) {
                log.error("Auto-correction failed for discrepancy {}", discrepancy.getId(), e);
                // Fall back to shadow balance
                enableShadowBalance(discrepancy);
            }
        }
    }

    private void handleMediumSeverity(ReconciliationDiscrepancy discrepancy) {
        log.info("Medium severity discrepancy - applying soft limits");

        if ("BANK_ACCOUNT".equals(discrepancy.getEntityType())) {
            applySoftLimits(discrepancy, AccountTier.TIER_3_LIMITED);
            enableShadowBalance(discrepancy);
            auditLog("SOFT_LIMITS_APPLIED", discrepancy);
        }
    }

    private void handleHighSeverity(ReconciliationDiscrepancy discrepancy) {
        log.info("High severity discrepancy - applying restrictive soft limits");

        if ("BANK_ACCOUNT".equals(discrepancy.getEntityType())) {
            applySoftLimits(discrepancy, AccountTier.TIER_4_MINIMAL);
            enableShadowBalance(discrepancy);
            auditLog("SOFT_LIMITS_APPLIED", discrepancy);
        }
    }

    private void handleCriticalSeverity(ReconciliationDiscrepancy discrepancy) {
        log.warn("Critical severity discrepancy - hard blocking account");

        if ("BANK_ACCOUNT".equals(discrepancy.getEntityType())) {
            blockAccount(discrepancy);
            auditLog("ACCOUNT_HARD_BLOCKED", discrepancy);
        } else if ("SYSTEM".equals(discrepancy.getEntityType())) {
            log.error("CRITICAL: System-wide discrepancy detected. Manual intervention required!");
            auditLog("SYSTEM_WIDE_DISCREPANCY", discrepancy);
            // TODO: Send alert to compliance team
        }
    }

    private void autoCorrectFromPool(ReconciliationDiscrepancy discrepancy) {
        log.info("Auto-correcting discrepancy {} from reconciliation pool", discrepancy.getId());

        BankAccount customerAccount = bankAccountRepository.findById(discrepancy.getEntityId())
            .orElseThrow(BankAccountNotFoundException::new);

        BankAccount poolAccount = bankAccountRepository
            .findByAccountNumber(config.getPool().getAccountNumber())
            .orElseThrow(() -> new RuntimeException("Reconciliation pool account not found"));

        BigDecimal difference = new BigDecimal(discrepancy.getExpectedValue())
            .subtract(new BigDecimal(discrepancy.getActualValue()));

        // Check pool has sufficient funds
        if (poolAccount.getBalance().compareTo(difference.abs()) < 0) {
            log.error("Reconciliation pool has insufficient funds. Pool balance: {}, Required: {}",
                poolAccount.getBalance(), difference.abs());
            throw new ReconciliationPoolInsufficientFundsException();
        }

        // Apply correction
        if (difference.compareTo(BigDecimal.ZERO) > 0) {
            // Customer account is short - transfer from pool
            poolAccount.setBalance(poolAccount.getBalance().subtract(difference));
            customerAccount.setBalance(customerAccount.getBalance().add(difference));
            log.info("Transferred {} from pool to account {}", difference, customerAccount.getAccountNumber());
        } else {
            // Customer account is over - transfer to pool
            BigDecimal absAmount = difference.abs();
            customerAccount.setBalance(customerAccount.getBalance().subtract(absAmount));
            poolAccount.setBalance(poolAccount.getBalance().add(absAmount));
            log.info("Transferred {} from account {} to pool", absAmount, customerAccount.getAccountNumber());
        }

        bankAccountCommandAdapter.save(poolAccount);
        bankAccountCommandAdapter.save(customerAccount);

        log.info("Auto-correction completed successfully");
    }

    private void applySoftLimits(ReconciliationDiscrepancy discrepancy, AccountTier tier) {
        log.info("Applying soft limits (tier: {}) to account {}", tier, discrepancy.getEntityId());

        BankAccount account = bankAccountRepository.findById(discrepancy.getEntityId())
            .orElseThrow(BankAccountNotFoundException::new);

        AccountLimits limits = new AccountLimits();
        limits.setAccount(account);
        limits.setTier(tier);
        limits.setAppliedAt(ZonedDateTime.now());
        limits.setReason("Reconciliation discrepancy: " + discrepancy.getDiscrepancyType());

        // Set limits based on tier configuration
        switch (tier) {
            case TIER_2_RESTRICTED -> {
                limits.setMaxWithdrawal(config.getTierLimits().getTier2().getMaxWithdrawal());
                limits.setDailyLimit(config.getTierLimits().getTier2().getDailyLimit());
            }
            case TIER_3_LIMITED -> {
                limits.setMaxWithdrawal(config.getTierLimits().getTier3().getMaxWithdrawal());
                limits.setMaxTransferOut(config.getTierLimits().getTier3().getMaxTransferOut());
                limits.setDailyLimit(config.getTierLimits().getTier3().getDailyLimit());
            }
            case TIER_4_MINIMAL -> {
                limits.setMaxWithdrawal(config.getTierLimits().getTier4().getMaxWithdrawal());
                limits.setDailyLimit(config.getTierLimits().getTier4().getDailyLimit());
            }
        }

        accountLimitsCommandAdapter.save(limits);
        log.info("Soft limits applied successfully");
    }

    private void enableShadowBalance(ReconciliationDiscrepancy discrepancy) {
        log.info("Enabling shadow balance tracking for account {}", discrepancy.getEntityId());

        BankAccount account = bankAccountRepository.findById(discrepancy.getEntityId())
            .orElseThrow(BankAccountNotFoundException::new);

        BigDecimal difference = new BigDecimal(discrepancy.getExpectedValue())
            .subtract(new BigDecimal(discrepancy.getActualValue()));

        account.setPendingCorrection(difference);
        account.setReconciledBalance(account.getBalance());
        bankAccountCommandAdapter.save(account);

        log.info("Shadow balance enabled with pending correction: {}", difference);
    }

    private void blockAccount(ReconciliationDiscrepancy discrepancy) {
        log.warn("Hard blocking account {} due to critical discrepancy", discrepancy.getEntityId());

        BankAccount account = bankAccountRepository.findById(discrepancy.getEntityId())
            .orElseThrow(BankAccountNotFoundException::new);

        account.setReconciliationBlocked(true);
        bankAccountCommandAdapter.save(account);

        log.warn("Account {} has been hard blocked", account.getAccountNumber());
    }

    @Override
    @Transactional
    public void unblockAccount(Long accountId, String justification) {
        log.info("Unblocking account {} with justification: {}", accountId, justification);

        BankAccount account = bankAccountRepository.findById(accountId)
            .orElseThrow(BankAccountNotFoundException::new);

        account.setReconciliationBlocked(false);
        account.setPendingCorrection(null);
        account.setReconciledBalance(account.getBalance());
        account.setLastReconciledAt(ZonedDateTime.now());
        bankAccountCommandAdapter.save(account);

        // Remove any soft limits
        accountLimitsCommandAdapter.deleteByAccount(account);

        auditLog("ACCOUNT_UNBLOCKED", String.format(
            "Account %s unblocked. Justification: %s", account.getAccountNumber(), justification));

        log.info("Account {} unblocked successfully", account.getAccountNumber());
    }

    private void auditLog(String action, ReconciliationDiscrepancy discrepancy) {
        auditLog(action, String.format(
            "Discrepancy: %s, Type: %s, Severity: %s, Entity: %s/%d, Description: %s",
            discrepancy.getId(),
            discrepancy.getDiscrepancyType(),
            discrepancy.getSeverity(),
            discrepancy.getEntityType(),
            discrepancy.getEntityId(),
            discrepancy.getDescription()));
    }

    private void auditLog(String action, String details) {
        try {
            // TODO: Implement proper audit logging
            log.info("AUDIT: {} - {}", action, details);
        } catch (Exception e) {
            log.error("Failed to write audit log", e);
        }
    }
}
