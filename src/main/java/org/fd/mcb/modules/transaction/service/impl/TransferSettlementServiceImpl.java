package org.fd.mcb.modules.transaction.service.impl;

import lombok.RequiredArgsConstructor;
import org.fd.mcb.modules.journalentry.adapter.command.JournalEntryCommandAdapter;
import org.fd.mcb.modules.journalentry.dto.context.JournalEntryContext;
import org.fd.mcb.modules.journalentry.enums.EntryType;
import org.fd.mcb.modules.master.adapter.command.BankAccountCommandAdapter;
import org.fd.mcb.modules.master.enums.TransactionStatus;
import org.fd.mcb.modules.master.model.entity.BankAccount;
import org.fd.mcb.modules.master.model.entity.Transaction;
import org.fd.mcb.modules.master.model.repository.TransactionRepository;
import org.fd.mcb.modules.transaction.dto.request.TransferSettlementRequest;
import org.fd.mcb.modules.transaction.dto.response.TransferSettlementResponse;
import org.fd.mcb.modules.transaction.service.TransferSettlementService;
import org.fd.mcb.shared.exception.AuthorizationNotFoundException;
import org.fd.mcb.shared.exception.InvalidTransactionStatusException;
import org.fd.mcb.shared.exception.TransferAlreadySettledException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class TransferSettlementServiceImpl implements TransferSettlementService {

    private final TransactionRepository transactionRepository;
    private final BankAccountCommandAdapter bankAccountCommandAdapter;
    private final JournalEntryCommandAdapter journalEntryCommandAdapter;

    @Override
    @Transactional(timeout = 5, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.REPEATABLE_READ)
    @Retryable(
            retryFor = {SQLException.class, PessimisticLockingFailureException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 100)
    )
    public TransferSettlementResponse settleTransfer(TransferSettlementRequest request) {
        // 1. Find transaction by auth code (with pessimistic lock)
        Transaction transaction = transactionRepository.findByAuthCode(request.getAuthCode())
                .orElseThrow(AuthorizationNotFoundException::new);

        // 2. Validate transaction status
        if (transaction.getStatus() == TransactionStatus.SETTLED) {
            throw new TransferAlreadySettledException();
        }

        if (transaction.getStatus() != TransactionStatus.CAPTURED) {
            throw new InvalidTransactionStatusException();
        }

        // 3. Fetch destination account (with pessimistic lock)
        BankAccount destinationAccount = transaction.getDestinationAccount();

        // 4. Credit destination account
        destinationAccount.setBalance(destinationAccount.getBalance().add(transaction.getAmount()));

        // Initialize or update available balance
        if (destinationAccount.getAvailableBalance() == null) {
            destinationAccount.setAvailableBalance(destinationAccount.getBalance());
        } else {
            destinationAccount.setAvailableBalance(
                destinationAccount.getAvailableBalance().add(transaction.getAmount())
            );
        }

        bankAccountCommandAdapter.save(destinationAccount);

        // 5. Create CREDIT journal entry on destination account
        JournalEntryContext journalEntryContext = JournalEntryContext.builder()
                .transaction(transaction)
                .bankAccount(destinationAccount)
                .amount(transaction.getAmount())
                .entryType(EntryType.CREDIT)
                .build();
        journalEntryCommandAdapter.save(journalEntryContext);

        // 6. Update transaction
        transaction.setStatus(TransactionStatus.SETTLED);
        transaction.setSettledAt(ZonedDateTime.now());
        transactionRepository.save(transaction);

        // 7. Return settlement response
        return TransferSettlementResponse.builder()
                .transactionId(transaction.getId())
                .authCode(transaction.getAuthCode())
                .status(TransactionStatus.SETTLED)
                .amount(transaction.getAmount())
                .sourceAccountNumber(transaction.getSourceAccount().getAccountNumber())
                .destinationAccountNumber(destinationAccount.getAccountNumber())
                .settledAt(transaction.getSettledAt())
                .build();
    }
}
