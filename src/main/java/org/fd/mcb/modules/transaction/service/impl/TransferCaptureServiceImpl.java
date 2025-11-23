package org.fd.mcb.modules.transaction.service.impl;

import lombok.RequiredArgsConstructor;
import org.fd.mcb.modules.journalentry.adapter.command.JournalEntryCommandAdapter;
import org.fd.mcb.modules.journalentry.dto.context.JournalEntryContext;
import org.fd.mcb.modules.journalentry.enums.EntryType;
import org.fd.mcb.modules.master.adapter.command.AccountHoldCommandAdapter;
import org.fd.mcb.modules.master.adapter.command.BankAccountCommandAdapter;
import org.fd.mcb.modules.master.adapter.query.AccountHoldQueryAdapter;
import org.fd.mcb.modules.master.enums.HoldStatus;
import org.fd.mcb.modules.master.enums.TransactionStatus;
import org.fd.mcb.modules.master.model.entity.AccountHold;
import org.fd.mcb.modules.master.model.entity.BankAccount;
import org.fd.mcb.modules.master.model.entity.Transaction;
import org.fd.mcb.modules.master.model.repository.TransactionRepository;
import org.fd.mcb.modules.transaction.dto.request.TransferCaptureRequest;
import org.fd.mcb.modules.transaction.dto.response.TransferCaptureResponse;
import org.fd.mcb.modules.transaction.service.TransferCaptureService;
import org.fd.mcb.shared.exception.AuthorizationExpiredException;
import org.fd.mcb.shared.exception.AuthorizationNotFoundException;
import org.fd.mcb.shared.exception.TransferAlreadyCapturedException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class TransferCaptureServiceImpl implements TransferCaptureService {

    private final TransactionRepository transactionRepository;
    private final AccountHoldQueryAdapter accountHoldQueryAdapter;
    private final AccountHoldCommandAdapter accountHoldCommandAdapter;
    private final BankAccountCommandAdapter bankAccountCommandAdapter;
    private final JournalEntryCommandAdapter journalEntryCommandAdapter;

    @Override
    @Transactional(timeout = 5, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.REPEATABLE_READ)
    @Retryable(
            retryFor = {SQLException.class, PessimisticLockingFailureException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 100)
    )
    public TransferCaptureResponse captureTransfer(TransferCaptureRequest request) {
        // 1. Find transaction by auth code (with pessimistic lock)
        Transaction transaction = transactionRepository.findByAuthCode(request.getAuthCode())
                .orElseThrow(AuthorizationNotFoundException::new);

        // 2. Validate transaction status
        if (transaction.getStatus() == TransactionStatus.CAPTURED ||
            transaction.getStatus() == TransactionStatus.SETTLED) {
            throw new TransferAlreadyCapturedException();
        }

        if (transaction.getStatus() != TransactionStatus.AUTHORIZED) {
            throw new AuthorizationNotFoundException();
        }

        // 3. Validate not expired
        if (transaction.getExpiresAt() != null &&
            transaction.getExpiresAt().isBefore(ZonedDateTime.now())) {
            throw new AuthorizationExpiredException();
        }

        // 4. Find associated hold
        AccountHold hold = accountHoldQueryAdapter.findByTransactionId(transaction.getId());
        if (hold.getStatus() != HoldStatus.ACTIVE) {
            throw new AuthorizationNotFoundException();
        }

        // 5. Fetch source account (with pessimistic lock)
        BankAccount sourceAccount = transaction.getSourceAccount();

        // 6. Debit source account actual balance (now actually moving money)
        sourceAccount.setBalance(sourceAccount.getBalance().subtract(transaction.getAmount()));

        // 7. Update transaction
        transaction.setStatus(TransactionStatus.CAPTURED);
        transaction.setCapturedAt(ZonedDateTime.now());
        transactionRepository.save(transaction);

        // 8. Release hold (funds now debited from actual balance)
        accountHoldCommandAdapter.releaseHold(hold.getId());

        // 9. Recalculate available balance (hold released, so available increases)
        BigDecimal totalHolds = accountHoldQueryAdapter.calculateTotalActiveHolds(sourceAccount.getId());
        sourceAccount.updateAvailableBalance(totalHolds);
        bankAccountCommandAdapter.save(sourceAccount);

        // 10. Create DEBIT journal entry on source account
        JournalEntryContext journalEntryContext = JournalEntryContext.builder()
                .transaction(transaction)
                .bankAccount(sourceAccount)
                .amount(transaction.getAmount())
                .entryType(EntryType.DEBIT)
                .build();
        journalEntryCommandAdapter.save(journalEntryContext);

        // 11. Return capture response
        return TransferCaptureResponse.builder()
                .transactionId(transaction.getId())
                .authCode(transaction.getAuthCode())
                .status(TransactionStatus.CAPTURED)
                .amount(transaction.getAmount())
                .sourceAccountNumber(sourceAccount.getAccountNumber())
                .destinationAccountNumber(transaction.getDestinationAccount().getAccountNumber())
                .capturedAt(transaction.getCapturedAt())
                .build();
    }
}
