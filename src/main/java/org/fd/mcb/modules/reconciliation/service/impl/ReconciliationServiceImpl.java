package org.fd.mcb.modules.reconciliation.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fd.mcb.configs.ReconciliationConfigProperties;
import org.fd.mcb.modules.journalentry.enums.EntryType;
import org.fd.mcb.modules.journalentry.model.entity.JournalEntry;
import org.fd.mcb.modules.journalentry.model.repository.JournalEntryRepository;
import org.fd.mcb.modules.master.enums.TransactionStatus;
import org.fd.mcb.modules.master.model.entity.BankAccount;
import org.fd.mcb.modules.master.model.entity.Transaction;
import org.fd.mcb.modules.master.model.repository.BankAccountRepository;
import org.fd.mcb.modules.master.model.repository.TransactionRepository;
import org.fd.mcb.modules.reconciliation.adapter.command.ReconciliationCommandAdapter;
import org.fd.mcb.modules.reconciliation.adapter.query.ReconciliationQueryAdapter;
import org.fd.mcb.modules.reconciliation.enums.DiscrepancySeverity;
import org.fd.mcb.modules.reconciliation.enums.DiscrepancyType;
import org.fd.mcb.modules.reconciliation.enums.ReconciliationStatus;
import org.fd.mcb.modules.reconciliation.model.entity.ReconciliationDiscrepancy;
import org.fd.mcb.modules.reconciliation.model.entity.ReconciliationReport;
import org.fd.mcb.modules.reconciliation.service.ReconciliationService;
import org.fd.mcb.modules.reconciliation.service.ReconciliationResponseService;
import org.fd.mcb.shared.exception.ReconciliationNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReconciliationServiceImpl implements ReconciliationService {

    private final ReconciliationCommandAdapter reconciliationCommandAdapter;
    private final ReconciliationQueryAdapter reconciliationQueryAdapter;
    private final ReconciliationResponseService responseService;
    private final JournalEntryRepository journalEntryRepository;
    private final TransactionRepository transactionRepository;
    private final BankAccountRepository bankAccountRepository;
    private final ReconciliationConfigProperties config;

    @Override
    @Transactional
    public ReconciliationReport performReconciliation(LocalDate date) {
        log.info("Starting reconciliation for date: {}", date);

        // Create report
        ReconciliationReport report = new ReconciliationReport();
        report.setReconciliationDate(date);
        report.setStatus(ReconciliationStatus.IN_PROGRESS);
        report.setStartedAt(ZonedDateTime.now());
        report.setCreatedAt(ZonedDateTime.now());
        report = reconciliationCommandAdapter.saveReport(report);

        List<ReconciliationDiscrepancy> allDiscrepancies = new ArrayList<>();

        try {
            // Get date range for the reconciliation date
            ZonedDateTime startOfDay = date.atStartOfDay(java.time.ZoneId.of("Asia/Jakarta"));
            ZonedDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);

            // Perform all reconciliation checks
            allDiscrepancies.addAll(checkDoubleEntryBalance(report, startOfDay, endOfDay));
            allDiscrepancies.addAll(checkAccountBalances(report, startOfDay, endOfDay));
            allDiscrepancies.addAll(checkTransactionCompleteness(report, startOfDay, endOfDay));
            allDiscrepancies.addAll(checkAvailableBalances(report, startOfDay, endOfDay));

            // Save all discrepancies
            for (ReconciliationDiscrepancy discrepancy : allDiscrepancies) {
                reconciliationCommandAdapter.saveDiscrepancy(discrepancy);
            }

            // Process discrepancies with hybrid response strategy
            for (ReconciliationDiscrepancy discrepancy : allDiscrepancies) {
                responseService.handleDiscrepancy(discrepancy);
            }

            // Update report status
            report.setTotalDiscrepancies(allDiscrepancies.size());
            report.setStatus(allDiscrepancies.isEmpty() ?
                ReconciliationStatus.COMPLETED :
                ReconciliationStatus.COMPLETED_WITH_DISCREPANCIES);
            report.setCompletedAt(ZonedDateTime.now());
            report = reconciliationCommandAdapter.saveReport(report);

            log.info("Reconciliation completed for date: {}. Total discrepancies: {}",
                date, allDiscrepancies.size());

            return report;

        } catch (Exception e) {
            log.error("Reconciliation failed for date: {}", date, e);
            report.setStatus(ReconciliationStatus.FAILED);
            report.setCompletedAt(ZonedDateTime.now());
            reconciliationCommandAdapter.saveReport(report);
            throw e;
        }
    }

    private List<ReconciliationDiscrepancy> checkDoubleEntryBalance(
            ReconciliationReport report,
            ZonedDateTime startOfDay,
            ZonedDateTime endOfDay) {

        log.debug("Checking double-entry balance for period: {} to {}", startOfDay, endOfDay);
        List<ReconciliationDiscrepancy> discrepancies = new ArrayList<>();

        // Get all journal entries for the day
        List<JournalEntry> entries = journalEntryRepository.findByDateRangeOrderByCreatedAtDesc(
            startOfDay, endOfDay);

        BigDecimal totalDebits = BigDecimal.ZERO;
        BigDecimal totalCredits = BigDecimal.ZERO;

        for (JournalEntry entry : entries) {
            if (entry.getEntryType() == EntryType.DEBIT) {
                totalDebits = totalDebits.add(entry.getAmount());
            } else if (entry.getEntryType() == EntryType.CREDIT) {
                totalCredits = totalCredits.add(entry.getAmount());
            }
        }

        // Update report with totals
        report.setTotalDebits(totalDebits);
        report.setTotalCredits(totalCredits);

        // Check if balanced
        if (totalDebits.compareTo(totalCredits) != 0) {
            log.warn("System-wide unbalanced entries detected. Debits: {}, Credits: {}",
                totalDebits, totalCredits);

            report.setSystemBalanced(false);

            ReconciliationDiscrepancy discrepancy = new ReconciliationDiscrepancy();
            discrepancy.setReport(report);
            discrepancy.setDiscrepancyType(DiscrepancyType.UNBALANCED_ENTRIES);
            discrepancy.setEntityType("SYSTEM");
            discrepancy.setEntityId(0L);
            discrepancy.setExpectedValue(totalDebits.toString());
            discrepancy.setActualValue(totalCredits.toString());
            discrepancy.setSeverity(DiscrepancySeverity.CRITICAL);
            discrepancy.setDescription(String.format(
                "System-wide unbalanced entries. Total debits (%s) != Total credits (%s). Difference: %s",
                totalDebits, totalCredits, totalDebits.subtract(totalCredits).abs()));
            discrepancy.setCreatedAt(ZonedDateTime.now());

            discrepancies.add(discrepancy);
        }

        return discrepancies;
    }

    private List<ReconciliationDiscrepancy> checkAccountBalances(
            ReconciliationReport report,
            ZonedDateTime startOfDay,
            ZonedDateTime endOfDay) {

        log.debug("Checking account balances for period: {} to {}", startOfDay, endOfDay);
        List<ReconciliationDiscrepancy> discrepancies = new ArrayList<>();

        // Get all journal entries for the day
        List<JournalEntry> entries = journalEntryRepository.findByDateRangeOrderByCreatedAtDesc(
            startOfDay, endOfDay);

        // Group by account
        var accountEntries = entries.stream()
            .collect(java.util.stream.Collectors.groupingBy(JournalEntry::getBankAccount));

        for (var entry : accountEntries.entrySet()) {
            BankAccount account = entry.getKey();
            List<JournalEntry> accountJournalEntries = entry.getValue();

            // Calculate expected balance from journal entries
            BigDecimal calculatedBalance = calculateExpectedBalance(account, accountJournalEntries);
            BigDecimal actualBalance = account.getBalance();

            if (calculatedBalance.compareTo(actualBalance) != 0) {
                BigDecimal difference = actualBalance.subtract(calculatedBalance);

                log.warn("Account balance mismatch for account {}: Expected: {}, Actual: {}, Difference: {}",
                    account.getAccountNumber(), calculatedBalance, actualBalance, difference);

                ReconciliationDiscrepancy discrepancy = new ReconciliationDiscrepancy();
                discrepancy.setReport(report);
                discrepancy.setDiscrepancyType(DiscrepancyType.ACCOUNT_BALANCE_MISMATCH);
                discrepancy.setEntityType("BANK_ACCOUNT");
                discrepancy.setEntityId(account.getId());
                discrepancy.setExpectedValue(calculatedBalance.toString());
                discrepancy.setActualValue(actualBalance.toString());
                discrepancy.setSeverity(determineSeverity(difference.abs()));
                discrepancy.setDescription(String.format(
                    "Account %s balance mismatch. Calculated from journal entries: %s, Actual balance: %s, Difference: %s",
                    account.getAccountNumber(), calculatedBalance, actualBalance, difference));
                discrepancy.setCreatedAt(ZonedDateTime.now());

                discrepancies.add(discrepancy);
            }
        }

        return discrepancies;
    }

    private BigDecimal calculateExpectedBalance(BankAccount account, List<JournalEntry> todayEntries) {
        // Start with reconciled balance if available, otherwise use current balance
        BigDecimal expectedBalance = account.getReconciledBalance() != null ?
            account.getReconciledBalance() : account.getBalance();

        // Apply today's journal entries
        for (JournalEntry entry : todayEntries) {
            if (entry.getEntryType() == EntryType.CREDIT) {
                expectedBalance = expectedBalance.add(entry.getAmount());
            } else if (entry.getEntryType() == EntryType.DEBIT) {
                expectedBalance = expectedBalance.subtract(entry.getAmount());
            }
        }

        return expectedBalance;
    }

    private List<ReconciliationDiscrepancy> checkTransactionCompleteness(
            ReconciliationReport report,
            ZonedDateTime startOfDay,
            ZonedDateTime endOfDay) {

        log.debug("Checking transaction completeness for period: {} to {}", startOfDay, endOfDay);
        List<ReconciliationDiscrepancy> discrepancies = new ArrayList<>();

        // Get all completed/settled transactions for the day
        Iterable<Transaction> allTransactions = transactionRepository.findAll();
        List<Transaction> dayTransactions = new ArrayList<>();

        for (Transaction txn : allTransactions) {
            if (txn.getCreatedAt().isAfter(startOfDay) && txn.getCreatedAt().isBefore(endOfDay)) {
                if (txn.getStatus() == TransactionStatus.COMPLETED ||
                    txn.getStatus() == TransactionStatus.SETTLED) {
                    dayTransactions.add(txn);
                }
            }
        }

        for (Transaction txn : dayTransactions) {
            List<JournalEntry> entries = journalEntryRepository.findByTransactionOrderByCreatedAtDesc(txn);

            if (entries.isEmpty()) {
                log.warn("Transaction {} has no journal entries", txn.getId());

                ReconciliationDiscrepancy discrepancy = new ReconciliationDiscrepancy();
                discrepancy.setReport(report);
                discrepancy.setDiscrepancyType(DiscrepancyType.MISSING_JOURNAL_ENTRIES);
                discrepancy.setEntityType("TRANSACTION");
                discrepancy.setEntityId(txn.getId());
                discrepancy.setExpectedValue("1 or more");
                discrepancy.setActualValue("0");
                discrepancy.setSeverity(determineSeverity(txn.getAmount()));
                discrepancy.setDescription(String.format(
                    "Transaction %d (%s) has no journal entries. Amount: %s",
                    txn.getId(), txn.getStatus(), txn.getAmount()));
                discrepancy.setCreatedAt(ZonedDateTime.now());

                discrepancies.add(discrepancy);
            }
        }

        return discrepancies;
    }

    private List<ReconciliationDiscrepancy> checkAvailableBalances(
            ReconciliationReport report,
            ZonedDateTime startOfDay,
            ZonedDateTime endOfDay) {

        log.debug("Checking available balances for period: {} to {}", startOfDay, endOfDay);
        List<ReconciliationDiscrepancy> discrepancies = new ArrayList<>();

        // This is a simplified check - in a real implementation, you would query active holds
        // and verify available_balance = balance - sum(holds)

        Iterable<BankAccount> allAccounts = bankAccountRepository.findAll();

        for (BankAccount account : allAccounts) {
            // For now, just check if available balance exists
            if (account.getAvailableBalance() == null) {
                ReconciliationDiscrepancy discrepancy = new ReconciliationDiscrepancy();
                discrepancy.setReport(report);
                discrepancy.setDiscrepancyType(DiscrepancyType.AVAILABLE_BALANCE_MISMATCH);
                discrepancy.setEntityType("BANK_ACCOUNT");
                discrepancy.setEntityId(account.getId());
                discrepancy.setExpectedValue(account.getBalance().toString());
                discrepancy.setActualValue("NULL");
                discrepancy.setSeverity(DiscrepancySeverity.LOW);
                discrepancy.setDescription(String.format(
                    "Account %s has null available balance", account.getAccountNumber()));
                discrepancy.setCreatedAt(ZonedDateTime.now());

                discrepancies.add(discrepancy);
            }
        }

        return discrepancies;
    }

    private DiscrepancySeverity determineSeverity(BigDecimal amount) {
        BigDecimal absAmount = amount.abs();

        if (absAmount.compareTo(config.getSeverityThresholds().getHigh()) > 0) {
            return DiscrepancySeverity.CRITICAL;
        } else if (absAmount.compareTo(config.getSeverityThresholds().getMedium()) > 0) {
            return DiscrepancySeverity.HIGH;
        } else if (absAmount.compareTo(config.getSeverityThresholds().getLow()) > 0) {
            return DiscrepancySeverity.MEDIUM;
        } else {
            return DiscrepancySeverity.LOW;
        }
    }

    @Override
    public ReconciliationReport getLatestReport() {
        return reconciliationQueryAdapter.findLatestReport()
            .orElseThrow(ReconciliationNotFoundException::new);
    }
}
