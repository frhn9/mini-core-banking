package org.fd.mcb.modules.transaction.service.impl;

import lombok.RequiredArgsConstructor;
import org.fd.mcb.modules.master.adapter.command.AccountHoldCommandAdapter;
import org.fd.mcb.modules.master.adapter.command.BankAccountCommandAdapter;
import org.fd.mcb.modules.master.adapter.command.TransactionCommandAdapter;
import org.fd.mcb.modules.master.adapter.query.AccountHoldQueryAdapter;
import org.fd.mcb.modules.master.adapter.query.BankAccountQueryAdapter;
import org.fd.mcb.modules.master.adapter.query.PaymentTypeQueryAdapter;
import org.fd.mcb.modules.master.enums.AccountType;
import org.fd.mcb.modules.master.enums.HoldStatus;
import org.fd.mcb.modules.master.enums.HoldType;
import org.fd.mcb.modules.master.enums.TransactionStatus;
import org.fd.mcb.modules.master.model.entity.AccountHold;
import org.fd.mcb.modules.master.model.entity.BankAccount;
import org.fd.mcb.modules.master.model.entity.PaymentType;
import org.fd.mcb.modules.master.model.entity.Transaction;
import org.fd.mcb.modules.transaction.dto.context.AccountHoldContext;
import org.fd.mcb.modules.transaction.dto.context.TransactionContext;
import org.fd.mcb.modules.transaction.dto.request.TransferAuthRequest;
import org.fd.mcb.modules.transaction.dto.response.TransferAuthResponse;
import org.fd.mcb.modules.transaction.service.TransferAuthorizationService;
import org.fd.mcb.shared.exception.InsufficientAvailableBalanceException;
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

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class TransferAuthorizationServiceImpl implements TransferAuthorizationService {

    private final BankAccountQueryAdapter bankAccountQueryAdapter;
    private final PaymentTypeQueryAdapter paymentTypeQueryAdapter;
    private final AccountHoldQueryAdapter accountHoldQueryAdapter;

    private final BankAccountCommandAdapter bankAccountCommandAdapter;
    private final TransactionCommandAdapter transactionCommandAdapter;
    private final AccountHoldCommandAdapter accountHoldCommandAdapter;

    private final TaskExecutor virtualThreadExecutor;

    @Override
    @Transactional(timeout = 5, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.REPEATABLE_READ)
    @Retryable(
            retryFor = {SQLException.class, PessimisticLockingFailureException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 100)
    )
    @Notify(
            event = NotificationEvent.TRANSFER_AUTHORIZED,
            channels = {NotificationChannel.LOG, NotificationChannel.EMAIL, NotificationChannel.SMS}
    )
    public TransferAuthResponse authorizeTransfer(TransferAuthRequest request) {
        TransactionUtil.validateInvalidAmount(request.getAmount());

        // Parallel async fetch with pessimistic locks
        CompletableFuture<BankAccount> sourceAccountFuture = CompletableFuture.supplyAsync(() -> {
            BankAccount account = bankAccountQueryAdapter
                    .findByAccountNumber(request.getSourceAccountNumber(), AccountType.SAVINGS);

            // Validate available balance (not just balance)
            if (account.getAvailableBalance() == null ||
                account.getAvailableBalance().compareTo(request.getAmount()) < 0) {
                throw new InsufficientAvailableBalanceException();
            }

            return account;
        }, virtualThreadExecutor);

        CompletableFuture<BankAccount> destAccountFuture = CompletableFuture.supplyAsync(() ->
                bankAccountQueryAdapter
                        .findByAccountNumber(request.getDestAccountNumber(), AccountType.SAVINGS),
                virtualThreadExecutor
        );

        CompletableFuture<PaymentType> paymentTypeFuture = CompletableFuture.supplyAsync(() ->
                        paymentTypeQueryAdapter.findByName("TRANSFER_AUTH"),
                virtualThreadExecutor
        );

        CompletableFuture.allOf(sourceAccountFuture, destAccountFuture, paymentTypeFuture).join();
        BankAccount sourceAccount = sourceAccountFuture.join();
        BankAccount destAccount = destAccountFuture.join();
        PaymentType paymentType = paymentTypeFuture.join();

        // Generate auth code
        String authCode = generateAuthCode();
        ZonedDateTime expiresAt = ZonedDateTime.now().plusHours(24);

        // Create AUTHORIZED transaction
        TransactionContext transactionContext = TransactionContext.builder()
                .amount(request.getAmount())
                .sourceAccount(sourceAccount)
                .destinationAccount(destAccount)
                .channel(request.getChannel() != null ? request.getChannel() : "API")
                .paymentType(paymentType)
                .status(TransactionStatus.AUTHORIZED)
                .authCode(authCode)
                .expiresAt(expiresAt)
                .build();
        Transaction transaction = transactionCommandAdapter.save(transactionContext);

        // Create AccountHold (place hold on funds)
        AccountHoldContext holdContext = AccountHoldContext.builder()
                .account(sourceAccount)
                .transaction(transaction)
                .holdType(HoldType.TRANSFER_AUTH)
                .amount(request.getAmount())
                .status(HoldStatus.ACTIVE)
                .expiresAt(expiresAt)
                .build();
        accountHoldCommandAdapter.save(holdContext);

        // Update available balance (balance stays same, available decreases)
        BigDecimal totalHolds = accountHoldQueryAdapter.calculateTotalActiveHolds(sourceAccount.getId());
        sourceAccount.updateAvailableBalance(totalHolds);
        bankAccountCommandAdapter.save(sourceAccount);

        // NO journal entry yet - funds not moved

        return TransferAuthResponse.builder()
                .authCode(authCode)
                .transactionId(transaction.getId())
                .status(TransactionStatus.AUTHORIZED)
                .expiresAt(expiresAt)
                .build();
    }

    @Override
    @Transactional(timeout = 5, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.REPEATABLE_READ)
    @Retryable(
            retryFor = {SQLException.class, PessimisticLockingFailureException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 100)
    )
    public void releaseHold(String authCode) {
        // Find transaction by auth code
        // Find hold by transaction
        // Release hold
        // Update available balance
        // Update transaction status to CANCELLED

        // Implementation for future capture/cancel feature
        throw new UnsupportedOperationException("Release hold not yet implemented");
    }

    @Override
    @Transactional(timeout = 5, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.REPEATABLE_READ)
    public void expireHolds() {
        List<AccountHold> expiredHolds = accountHoldQueryAdapter.findExpiredHolds();

        for (AccountHold hold : expiredHolds) {
            // Release the hold
            accountHoldCommandAdapter.releaseHold(hold.getId());

            // Update transaction status to CANCELLED
            Transaction transaction = hold.getTransaction();
            transaction.setStatus(TransactionStatus.CANCELLED);

            // Update available balance
            BankAccount account = hold.getAccount();
            BigDecimal totalHolds = accountHoldQueryAdapter.calculateTotalActiveHolds(account.getId());
            account.updateAvailableBalance(totalHolds);
            bankAccountCommandAdapter.save(account);
        }
    }

    private String generateAuthCode() {
        return "AUTH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
