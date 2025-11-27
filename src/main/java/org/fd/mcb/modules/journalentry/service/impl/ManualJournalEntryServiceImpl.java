package org.fd.mcb.modules.journalentry.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fd.mcb.modules.auditlog.adapter.command.AuditLogCommandAdapter;
import org.fd.mcb.modules.auditlog.dto.context.AuditLogContext;
import org.fd.mcb.modules.journalentry.adapter.command.JournalEntryCommandAdapter;
import org.fd.mcb.modules.journalentry.dto.context.JournalEntryContext;
import org.fd.mcb.modules.journalentry.dto.request.JournalEntryItemRequest;
import org.fd.mcb.modules.journalentry.dto.request.ManualJournalEntryRequest;
import org.fd.mcb.modules.journalentry.dto.request.ReversalRequest;
import org.fd.mcb.modules.journalentry.dto.response.JournalEntryDTO;
import org.fd.mcb.modules.journalentry.dto.response.ManualJournalEntryResponse;
import org.fd.mcb.modules.journalentry.enums.EntryType;
import org.fd.mcb.modules.journalentry.mapper.JournalEntryMapper;
import org.fd.mcb.modules.journalentry.model.entity.JournalEntry;
import org.fd.mcb.modules.journalentry.service.ManualJournalEntryService;
import org.fd.mcb.modules.master.adapter.command.BankAccountCommandAdapter;
import org.fd.mcb.modules.master.adapter.command.TransactionCommandAdapter;
import org.fd.mcb.modules.master.adapter.query.BankAccountQueryAdapter;
import org.fd.mcb.modules.master.adapter.query.PaymentTypeQueryAdapter;
import org.fd.mcb.modules.master.enums.AccountType;
import org.fd.mcb.modules.master.enums.TransactionStatus;
import org.fd.mcb.modules.master.model.entity.BankAccount;
import org.fd.mcb.modules.master.model.entity.PaymentType;
import org.fd.mcb.modules.master.model.entity.Transaction;
import org.fd.mcb.modules.master.model.repository.TransactionRepository;
import org.fd.mcb.modules.transaction.dto.context.TransactionContext;
import org.fd.mcb.shared.exception.InvalidAmountException;
import org.fd.mcb.shared.exception.InvalidJournalEntryException;
import org.fd.mcb.shared.exception.NegativeBalanceNotAllowedException;
import org.fd.mcb.shared.exception.UnbalancedJournalEntryException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ManualJournalEntryServiceImpl implements ManualJournalEntryService {

    private final BankAccountQueryAdapter bankAccountQueryAdapter;
    private final BankAccountCommandAdapter bankAccountCommandAdapter;
    private final TransactionCommandAdapter transactionCommandAdapter;
    private final JournalEntryCommandAdapter journalEntryCommandAdapter;
    private final PaymentTypeQueryAdapter paymentTypeQueryAdapter;
    private final AuditLogCommandAdapter auditLogCommandAdapter;
    private final TransactionRepository transactionRepository;
    private final JournalEntryMapper journalEntryMapper;

    @Override
    @Transactional(timeout = 10, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.REPEATABLE_READ)
    @Retryable(
            retryFor = {java.sql.SQLException.class, PessimisticLockingFailureException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 100)
    )
    public ManualJournalEntryResponse createManualEntry(ManualJournalEntryRequest request) {
        log.info("Creating manual journal entry with reason: {}", request.getReason());

        // Validate request
        validateManualJournalEntryRequest(request);

        // Validate double-entry bookkeeping (debits = credits)
        validateBalancedEntry(request);

        // Get payment type for manual adjustment
        PaymentType paymentType = paymentTypeQueryAdapter.findByName("MANUAL_ADJUSTMENT");

        // Create transaction
        TransactionContext transactionContext = TransactionContext.builder()
                .amount(calculateTotalAmount(request))
                .channel("MANUAL")
                .paymentType(paymentType)
                .additionalInfo(request.getReason())
                .status(TransactionStatus.COMPLETED)
                .build();
        Transaction transaction = transactionCommandAdapter.save(transactionContext);

        // Process each journal entry item
        Map<String, BankAccount> accountCache = new HashMap<>();
        List<JournalEntry> createdEntries = new ArrayList<>();

        for (JournalEntryItemRequest item : request.getEntries()) {
            // Get or fetch bank account
            BankAccount bankAccount = accountCache.computeIfAbsent(
                    item.getAccountNumber(),
                    accNum -> bankAccountQueryAdapter.findByAccountNumber(accNum, AccountType.SAVINGS)
            );

            // Validate balance will not go negative
            validateBalanceNotNegative(bankAccount, item);

            // Update account balance
            updateAccountBalance(bankAccount, item);
            bankAccountCommandAdapter.save(bankAccount);

            // Create journal entry
            JournalEntryContext journalEntryContext = JournalEntryContext.builder()
                    .transaction(transaction)
                    .bankAccount(bankAccount)
                    .amount(item.getAmount())
                    .entryType(item.getEntryType())
                    .build();
            JournalEntry journalEntry = journalEntryCommandAdapter.save(journalEntryContext);
            createdEntries.add(journalEntry);
        }

        // Create audit log
        createAuditLog(request, transaction);

        // Build response
        return buildResponse(transaction, createdEntries, request.getReason());
    }

    @Override
    @Transactional(timeout = 10, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.REPEATABLE_READ)
    @Retryable(
            retryFor = {java.sql.SQLException.class, PessimisticLockingFailureException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 100)
    )
    public ManualJournalEntryResponse reverseManualEntry(ReversalRequest request) {
        log.info("Reversing journal entry for transaction ID: {}", request.getTransactionId());

        // Find original transaction
        Transaction originalTransaction = transactionRepository.findById(request.getTransactionId())
                .orElseThrow(() -> new InvalidJournalEntryException());

        // Find all journal entries for this transaction
        List<JournalEntry> originalEntries = originalTransaction.getJournalEntries();
        if (originalEntries.isEmpty()) {
            throw new InvalidJournalEntryException();
        }

        // Get payment type for manual reversal
        PaymentType paymentType = paymentTypeQueryAdapter.findByName("MANUAL_REVERSAL");

        // Create reversal transaction
        TransactionContext transactionContext = TransactionContext.builder()
                .amount(originalTransaction.getAmount())
                .channel("MANUAL_REVERSAL")
                .paymentType(paymentType)
                .additionalInfo("Reversal of transaction " + request.getTransactionId() + ": " + request.getReason())
                .status(TransactionStatus.COMPLETED)
                .build();
        Transaction reversalTransaction = transactionCommandAdapter.save(transactionContext);

        // Create offsetting journal entries
        List<JournalEntry> reversalEntries = new ArrayList<>();
        for (JournalEntry originalEntry : originalEntries) {
            BankAccount bankAccount = originalEntry.getBankAccount();

            // Reverse the entry type
            EntryType reversalEntryType = originalEntry.getEntryType() == EntryType.DEBIT
                    ? EntryType.CREDIT
                    : EntryType.DEBIT;

            // Validate balance will not go negative
            validateBalanceNotNegativeForReversal(bankAccount, originalEntry.getAmount(), reversalEntryType);

            // Update account balance (opposite operation)
            if (reversalEntryType == EntryType.CREDIT) {
                bankAccount.setBalance(bankAccount.getBalance().add(originalEntry.getAmount()));
                bankAccount.setAvailableBalance(bankAccount.getAvailableBalance().add(originalEntry.getAmount()));
            } else {
                bankAccount.setBalance(bankAccount.getBalance().subtract(originalEntry.getAmount()));
                bankAccount.setAvailableBalance(bankAccount.getAvailableBalance().subtract(originalEntry.getAmount()));
            }
            bankAccountCommandAdapter.save(bankAccount);

            // Create reversal journal entry
            JournalEntryContext journalEntryContext = JournalEntryContext.builder()
                    .transaction(reversalTransaction)
                    .bankAccount(bankAccount)
                    .amount(originalEntry.getAmount())
                    .entryType(reversalEntryType)
                    .build();
            JournalEntry reversalEntry = journalEntryCommandAdapter.save(journalEntryContext);
            reversalEntries.add(reversalEntry);
        }

        // Create audit log for reversal
        AuditLogContext auditLogContext = AuditLogContext.builder()
                .action("MANUAL_JOURNAL_ENTRY_REVERSAL")
                .details("Reversed transaction " + request.getTransactionId() + ". Reason: " + request.getReason()
                        + ". Staff: " + (request.getStaffUsername() != null ? request.getStaffUsername() : "UNKNOWN"))
                .build();
        auditLogCommandAdapter.save(auditLogContext);

        // Build response
        return buildResponse(reversalTransaction, reversalEntries, request.getReason());
    }

    private void validateManualJournalEntryRequest(ManualJournalEntryRequest request) {
        if (request.getEntries() == null || request.getEntries().isEmpty()) {
            throw new InvalidJournalEntryException();
        }

        for (JournalEntryItemRequest item : request.getEntries()) {
            if (item.getAmount() == null || item.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new InvalidAmountException();
            }
            if (item.getEntryType() == null) {
                throw new InvalidJournalEntryException();
            }
        }
    }

    private void validateBalancedEntry(ManualJournalEntryRequest request) {
        BigDecimal totalDebits = BigDecimal.ZERO;
        BigDecimal totalCredits = BigDecimal.ZERO;

        for (JournalEntryItemRequest item : request.getEntries()) {
            if (item.getEntryType() == EntryType.DEBIT) {
                totalDebits = totalDebits.add(item.getAmount());
            } else if (item.getEntryType() == EntryType.CREDIT) {
                totalCredits = totalCredits.add(item.getAmount());
            }
        }

        if (totalDebits.compareTo(totalCredits) != 0) {
            log.error("Unbalanced journal entry: debits={}, credits={}", totalDebits, totalCredits);
            throw new UnbalancedJournalEntryException();
        }
    }

    private void validateBalanceNotNegative(BankAccount bankAccount, JournalEntryItemRequest item) {
        if (item.getEntryType() == EntryType.DEBIT) {
            BigDecimal newBalance = bankAccount.getBalance().subtract(item.getAmount());
            if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                log.error("Operation would result in negative balance for account: {}",
                        bankAccount.getAccountNumber());
                throw new NegativeBalanceNotAllowedException();
            }
        }
    }

    private void validateBalanceNotNegativeForReversal(BankAccount bankAccount, BigDecimal amount,
            EntryType reversalEntryType) {
        if (reversalEntryType == EntryType.DEBIT) {
            BigDecimal newBalance = bankAccount.getBalance().subtract(amount);
            if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                log.error("Reversal would result in negative balance for account: {}",
                        bankAccount.getAccountNumber());
                throw new NegativeBalanceNotAllowedException();
            }
        }
    }

    private void updateAccountBalance(BankAccount bankAccount, JournalEntryItemRequest item) {
        if (item.getEntryType() == EntryType.CREDIT) {
            bankAccount.setBalance(bankAccount.getBalance().add(item.getAmount()));
            if (bankAccount.getAvailableBalance() == null) {
                bankAccount.setAvailableBalance(bankAccount.getBalance());
            } else {
                bankAccount.setAvailableBalance(bankAccount.getAvailableBalance().add(item.getAmount()));
            }
        } else if (item.getEntryType() == EntryType.DEBIT) {
            bankAccount.setBalance(bankAccount.getBalance().subtract(item.getAmount()));
            if (bankAccount.getAvailableBalance() == null) {
                bankAccount.setAvailableBalance(bankAccount.getBalance());
            } else {
                bankAccount.setAvailableBalance(bankAccount.getAvailableBalance().subtract(item.getAmount()));
            }
        }
    }

    private BigDecimal calculateTotalAmount(ManualJournalEntryRequest request) {
        return request.getEntries().stream()
                .filter(item -> item.getEntryType() == EntryType.DEBIT)
                .map(JournalEntryItemRequest::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void createAuditLog(ManualJournalEntryRequest request, Transaction transaction) {
        String details = String.format("Created manual journal entry. Transaction ID: %d, Reason: %s, Staff: %s",
                transaction.getId(),
                request.getReason(),
                request.getStaffUsername() != null ? request.getStaffUsername() : "UNKNOWN");

        AuditLogContext auditLogContext = AuditLogContext.builder()
                .action("MANUAL_JOURNAL_ENTRY_CREATED")
                .details(details)
                .build();
        auditLogCommandAdapter.save(auditLogContext);
    }

    private ManualJournalEntryResponse buildResponse(Transaction transaction,
            List<JournalEntry> journalEntries, String reason) {
        ManualJournalEntryResponse response = new ManualJournalEntryResponse();
        response.setTransactionId(transaction.getId());
        response.setReason(reason);
        response.setCreatedAt(transaction.getCreatedAt());
        response.setStatus(transaction.getStatus().toString());

        List<JournalEntryDTO> entryDTOs = journalEntryMapper.toDTOList(journalEntries);
        response.setJournalEntries(entryDTOs);

        return response;
    }
}
