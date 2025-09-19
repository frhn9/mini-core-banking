package org.fd.mcb.modules.transaction.service.impl;

import lombok.RequiredArgsConstructor;
import org.fd.mcb.modules.journalentry.adapter.command.JournalEntryCommandAdapter;
import org.fd.mcb.modules.journalentry.dto.context.JournalEntryContext;
import org.fd.mcb.modules.journalentry.enums.EntryType;
import org.fd.mcb.modules.master.adapter.command.BankAccountCommandAdapter;
import org.fd.mcb.modules.master.adapter.command.TransactionCommandAdapter;
import org.fd.mcb.modules.master.adapter.query.BankAccountQueryAdapter;
import org.fd.mcb.modules.master.adapter.query.PaymentTypeQueryAdapter;
import org.fd.mcb.modules.master.enums.AccountType;
import org.fd.mcb.modules.master.model.entity.BankAccount;
import org.fd.mcb.modules.master.model.entity.PaymentType;
import org.fd.mcb.modules.master.model.entity.Transaction;
import org.fd.mcb.modules.transaction.dto.context.TransactionContext;
import org.fd.mcb.modules.transaction.dto.request.TransferRequest;
import org.fd.mcb.modules.transaction.dto.response.TransferResponse;
import org.fd.mcb.modules.transaction.service.TransferService;
import org.fd.mcb.shared.util.TransactionUtil;
import org.springframework.core.task.TaskExecutor;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final BankAccountQueryAdapter bankAccountQueryAdapter;
    private final PaymentTypeQueryAdapter paymentTypeQueryAdapter;

    private final BankAccountCommandAdapter bankAccountCommandAdapter;
    private final TransactionCommandAdapter transactionCommandAdapter;
    private final JournalEntryCommandAdapter journalEntryCommandAdapter;

    private final TaskExecutor virtualThreadExecutor;

    @Override
    @Transactional(timeout = 5, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.REPEATABLE_READ)
    @Retryable(
            retryFor = {SQLException.class, PessimisticLockingFailureException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 100)
    )
    public TransferResponse authPayment(TransferRequest request) {
        TransactionUtil.validateInvalidAmount(request.getAmount());

        CompletableFuture<BankAccount> fromAccountFuture = CompletableFuture.supplyAsync(() ->
                        bankAccountQueryAdapter.findByAccountNumber(request.getFromAccountNumber(), AccountType.SAVINGS),
                virtualThreadExecutor
        );
        CompletableFuture<BankAccount> toAccountFuture = CompletableFuture.supplyAsync(() ->
                        bankAccountQueryAdapter.findByAccountNumber(request.getToAccountNumber(), AccountType.SAVINGS),
                virtualThreadExecutor
        );
        CompletableFuture<PaymentType> paymentTypeFuture = CompletableFuture.supplyAsync(() ->
                        paymentTypeQueryAdapter.findByName("TRANSFER"),
                virtualThreadExecutor
        );
        CompletableFuture.allOf(fromAccountFuture, toAccountFuture, paymentTypeFuture).join();
        BankAccount fromAccount = fromAccountFuture.join();
        BankAccount toAccount = toAccountFuture.join();
        PaymentType paymentType = paymentTypeFuture.join();

        TransactionUtil.validateBalance(fromAccount.getBalance(), request.getAmount());

        String referenceNumber = UUID.randomUUID().toString();

        TransactionContext transactionContext = TransactionContext.builder()
                .amount(request.getAmount())
                .sourceAccount(fromAccount)
                .destinationAccount(toAccount)
                .channel("MOBILE_BANKING")
                .paymentType(paymentType)
                .referenceNumber(referenceNumber)
                .status("AUTHORIZED")
                .build();
        Transaction transaction = transactionCommandAdapter.save(transactionContext);

        fromAccount.setBalance(fromAccount.getBalance().subtract(request.getAmount()));
        bankAccountCommandAdapter.save(fromAccount);

        JournalEntryContext journalEntryContext = JournalEntryContext.builder()
                .transaction(transaction)
                .bankAccount(fromAccount)
                .amount(request.getAmount())
                .entryType(EntryType.DEBIT)
                .build();
        journalEntryCommandAdapter.save(journalEntryContext);

        return TransferResponse.builder()
                .transactionId(transaction.getId())
                .referenceNumber(referenceNumber)
                .message("Transfer has been authorized.")
                .build();
    }
}
