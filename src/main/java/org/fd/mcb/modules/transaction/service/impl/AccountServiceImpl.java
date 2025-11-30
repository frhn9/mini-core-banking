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
import org.fd.mcb.modules.transaction.dto.request.DepositWithdrawReq;
import org.fd.mcb.modules.transaction.dto.response.AccountResponse;
import org.fd.mcb.modules.transaction.mapper.AccountMapper;
import org.fd.mcb.modules.transaction.service.AccountService;
import org.fd.mcb.shared.notification.annotation.Notify;
import org.fd.mcb.shared.notification.enums.NotificationChannel;
import org.fd.mcb.shared.notification.enums.NotificationEvent;
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
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final BankAccountQueryAdapter bankAccountQueryAdapter;
    private final PaymentTypeQueryAdapter paymentTypeQueryAdapter;

    private final BankAccountCommandAdapter bankAccountCommandAdapter;
    private final TransactionCommandAdapter transactionCommandAdapter;
    private final JournalEntryCommandAdapter journalEntryCommandAdapter;

    private final AccountMapper accountMapper;

    private final TaskExecutor virtualThreadExecutor;

    @Override
    @Transactional(timeout = 5, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.REPEATABLE_READ)
    @Retryable(
            retryFor = {SQLException.class, PessimisticLockingFailureException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 100)
    )
    @Notify(
            event = NotificationEvent.DEPOSIT_SUCCESS,
            channels = {NotificationChannel.LOG, NotificationChannel.EMAIL, NotificationChannel.SMS}
    )
    public AccountResponse deposit(DepositWithdrawReq request) {
        TransactionUtil.validateInvalidAmount(request.getAmount());

        CompletableFuture<BankAccount> bankAccountFuture = CompletableFuture.supplyAsync(() ->
                        bankAccountQueryAdapter.findByAccountNumber(request.getAccountNumber(), AccountType.SAVINGS),
                virtualThreadExecutor
        );
        CompletableFuture<PaymentType> paymentTypeFuture = CompletableFuture.supplyAsync(() ->
                        paymentTypeQueryAdapter.findByName("DEPOSIT"),
                virtualThreadExecutor
        );
        CompletableFuture.allOf(bankAccountFuture, paymentTypeFuture).join();
        BankAccount bankAccount = bankAccountFuture.join();
        PaymentType paymentType = paymentTypeFuture.join();

        TransactionContext transactionContext = TransactionContext.builder()
                .amount(request.getAmount())
                .destinationAccount(bankAccount)
                .channel("ATM")
                .paymentType(paymentType)
                .build();
        Transaction transaction = transactionCommandAdapter.save(transactionContext);

        bankAccount.setBalance(bankAccount.getBalance().add(request.getAmount()));
        // Update available balance (if null, initialize it)
        if (bankAccount.getAvailableBalance() == null) {
            bankAccount.setAvailableBalance(bankAccount.getBalance());
        } else {
            bankAccount.setAvailableBalance(bankAccount.getAvailableBalance().add(request.getAmount()));
        }
        bankAccountCommandAdapter.save(bankAccount);

        JournalEntryContext journalEntryContext = JournalEntryContext.builder()
                .transaction(transaction)
                .bankAccount(bankAccount)
                .amount(request.getAmount())
                .entryType(EntryType.CREDIT)
                .build();
        journalEntryCommandAdapter.save(journalEntryContext);

        return accountMapper.toAccountResponse(transaction, bankAccount);
    }

    @Override
    @Transactional(timeout = 5, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.REPEATABLE_READ)
    @Retryable(
            retryFor = {SQLException.class, PessimisticLockingFailureException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 100)
    )
    @Notify(
            event = NotificationEvent.WITHDRAWAL_SUCCESS,
            channels = {NotificationChannel.LOG, NotificationChannel.EMAIL, NotificationChannel.SMS}
    )
    public AccountResponse withdrawal(DepositWithdrawReq request) {
        TransactionUtil.validateInvalidAmount(request.getAmount());

        CompletableFuture<BankAccount> bankAccountFuture = CompletableFuture.supplyAsync(() -> {
            BankAccount ba = bankAccountQueryAdapter.findByAccountNumber(request.getAccountNumber(), AccountType.SAVINGS);
            // Validate available balance (respects holds)
            TransactionUtil.validateBalance(ba.getAvailableBalance() != null ? ba.getAvailableBalance() : ba.getBalance(), request.getAmount());
            return ba;
        }, virtualThreadExecutor);
        CompletableFuture<PaymentType> paymentTypeFuture = CompletableFuture.supplyAsync(() ->
                paymentTypeQueryAdapter.findByName("WITHDRAWAL"),
                virtualThreadExecutor
        );
        CompletableFuture.allOf(bankAccountFuture, paymentTypeFuture).join();
        BankAccount bankAccount = bankAccountFuture.join();
        PaymentType paymentType = paymentTypeFuture.join();

        TransactionContext transactionContext = TransactionContext.builder()
                .amount(request.getAmount())
                .sourceAccount(bankAccount)
                .channel("ATM")
                .paymentType(paymentType)
                .build();
        Transaction transaction = transactionCommandAdapter.save(transactionContext);

        bankAccount.setBalance(bankAccount.getBalance().subtract(request.getAmount()));
        // Update available balance (if null, initialize it first)
        if (bankAccount.getAvailableBalance() == null) {
            bankAccount.setAvailableBalance(bankAccount.getBalance());
        } else {
            bankAccount.setAvailableBalance(bankAccount.getAvailableBalance().subtract(request.getAmount()));
        }
        bankAccountCommandAdapter.save(bankAccount);

        JournalEntryContext journalEntryContext = JournalEntryContext.builder()
                .transaction(transaction)
                .bankAccount(bankAccount)
                .amount(request.getAmount())
                .entryType(EntryType.DEBIT)
                .build();
        journalEntryCommandAdapter.save(journalEntryContext);

        return accountMapper.toAccountResponse(transaction, bankAccount);
    }

}
