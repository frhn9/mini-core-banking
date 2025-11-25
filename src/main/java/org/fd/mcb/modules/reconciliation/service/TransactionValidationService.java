package org.fd.mcb.modules.reconciliation.service;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fd.mcb.modules.master.model.entity.BankAccount;
import org.fd.mcb.modules.reconciliation.adapter.query.AccountLimitsQueryAdapter;
import org.fd.mcb.modules.reconciliation.model.entity.AccountLimits;
import org.fd.mcb.shared.exception.AccountReconciliationBlockedException;
import org.fd.mcb.shared.exception.TransactionLimitExceededException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionValidationService {

    private final AccountLimitsQueryAdapter accountLimitsQueryAdapter;

    /**
     * Validate if a transaction can proceed for a given account
     * @param account The account to validate
     * @param amount The transaction amount
     * @param transactionType The type of transaction (WITHDRAWAL, TRANSFER, etc.)
     * @throws AccountReconciliationBlockedException if account is hard blocked
     * @throws TransactionLimitExceededException if amount exceeds soft limits
     */
    public void validateTransaction(BankAccount account, BigDecimal amount, String transactionType) {
        // Check for hard block
        if (Boolean.TRUE.equals(account.getReconciliationBlocked())) {
            log.warn("Transaction blocked: Account {} is reconciliation blocked", account.getAccountNumber());
            throw new AccountReconciliationBlockedException();
        }

        // Check for soft limits
        Optional<AccountLimits> limitsOpt = accountLimitsQueryAdapter
            .findActiveByAccountId(account.getId(), ZonedDateTime.now());

        if (limitsOpt.isPresent()) {
            AccountLimits limits = limitsOpt.get();
            log.info("Account {} has active limits: Tier={}", account.getAccountNumber(), limits.getTier());

            // Validate based on transaction type
            if ("WITHDRAWAL".equalsIgnoreCase(transactionType) || "DEBIT".equalsIgnoreCase(transactionType)) {
                if (limits.getMaxWithdrawal() != null && amount.compareTo(limits.getMaxWithdrawal()) > 0) {
                    log.warn("Transaction amount {} exceeds max withdrawal limit {}",
                        amount, limits.getMaxWithdrawal());
                    throw new TransactionLimitExceededException();
                }
            } else if ("TRANSFER".equalsIgnoreCase(transactionType)) {
                if (limits.getMaxTransferOut() != null && amount.compareTo(limits.getMaxTransferOut()) > 0) {
                    log.warn("Transfer amount {} exceeds max transfer limit {}",
                        amount, limits.getMaxTransferOut());
                    throw new TransactionLimitExceededException();
                }
            }

            log.info("Transaction validation passed with soft limits");
        } else {
            log.debug("No active limits for account {}", account.getAccountNumber());
        }
    }

    /**
     * Check if an account has any reconciliation issues
     * @param account The account to check
     * @return true if account has pending corrections or limits
     */
    public boolean hasReconciliationIssues(BankAccount account) {
        if (Boolean.TRUE.equals(account.getReconciliationBlocked())) {
            return true;
        }

        if (account.hasPendingCorrection()) {
            return true;
        }

        Optional<AccountLimits> limitsOpt = accountLimitsQueryAdapter
            .findActiveByAccountId(account.getId(), ZonedDateTime.now());

        return limitsOpt.isPresent();
    }
}
