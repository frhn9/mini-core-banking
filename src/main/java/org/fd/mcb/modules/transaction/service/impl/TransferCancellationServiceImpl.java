package org.fd.mcb.modules.transaction.service.impl;

import lombok.RequiredArgsConstructor;
import org.fd.mcb.modules.master.adapter.command.AccountHoldCommandAdapter;
import org.fd.mcb.modules.master.adapter.command.BankAccountCommandAdapter;
import org.fd.mcb.modules.master.adapter.query.AccountHoldQueryAdapter;
import org.fd.mcb.modules.master.adapter.query.BankAccountQueryAdapter;
import org.fd.mcb.modules.master.enums.HoldStatus;
import org.fd.mcb.modules.master.enums.TransactionStatus;
import org.fd.mcb.modules.master.model.entity.AccountHold;
import org.fd.mcb.modules.master.model.entity.BankAccount;
import org.fd.mcb.modules.master.model.entity.Transaction;
import org.fd.mcb.modules.master.model.repository.TransactionRepository;
import org.fd.mcb.modules.transaction.dto.request.TransferCancellationRequest;
import org.fd.mcb.modules.transaction.dto.response.TransferCancellationResponse;
import org.fd.mcb.modules.transaction.service.TransferCancellationService;
import org.fd.mcb.shared.exception.AuthorizationNotFoundException;
import org.fd.mcb.shared.exception.InvalidTransactionStatusException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.SQLException;

@Service
@RequiredArgsConstructor
public class TransferCancellationServiceImpl implements TransferCancellationService {

    private final TransactionRepository transactionRepository;
    private final AccountHoldQueryAdapter accountHoldQueryAdapter;
    private final AccountHoldCommandAdapter accountHoldCommandAdapter;
    private final BankAccountQueryAdapter bankAccountQueryAdapter;
    private final BankAccountCommandAdapter bankAccountCommandAdapter;

    @Override
    @Transactional(timeout = 5, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.REPEATABLE_READ)
    @Retryable(
            retryFor = {SQLException.class, PessimisticLockingFailureException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 100)
    )
    public TransferCancellationResponse cancelTransfer(TransferCancellationRequest request) {
        // 1. Find transaction by auth code (with pessimistic lock)
        Transaction transaction = transactionRepository.findByAuthCode(request.getAuthCode())
                .orElseThrow(AuthorizationNotFoundException::new);

        // 2. Validate transaction status is AUTHORIZED
        if (transaction.getStatus() != TransactionStatus.AUTHORIZED) {
            throw new InvalidTransactionStatusException();
        }

        // 3. Find associated hold
        AccountHold hold = accountHoldQueryAdapter.findByTransactionId(transaction.getId());
        if (hold.getStatus() != HoldStatus.ACTIVE) {
            throw new InvalidTransactionStatusException();
        }

        // 4. Fetch source account (with pessimistic lock)
        BankAccount sourceAccount = transaction.getSourceAccount();

        // 5. Release hold
        accountHoldCommandAdapter.releaseHold(hold.getId());

        // 6. Recalculate available balance (restore funds)
        BigDecimal totalHolds = accountHoldQueryAdapter.calculateTotalActiveHolds(sourceAccount.getId());
        sourceAccount.updateAvailableBalance(totalHolds);
        bankAccountCommandAdapter.save(sourceAccount);

        // 7. Update transaction status
        transaction.setStatus(TransactionStatus.CANCELLED);
        transactionRepository.save(transaction);

        // 8. Return response
        return TransferCancellationResponse.builder()
                .transactionId(transaction.getId())
                .authCode(transaction.getAuthCode())
                .status(TransactionStatus.CANCELLED)
                .amount(transaction.getAmount())
                .message("Transfer authorization cancelled successfully")
                .build();
    }
}
