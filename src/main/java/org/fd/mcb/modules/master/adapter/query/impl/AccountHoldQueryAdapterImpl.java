package org.fd.mcb.modules.master.adapter.query.impl;

import lombok.RequiredArgsConstructor;
import org.fd.mcb.modules.master.adapter.query.AccountHoldQueryAdapter;
import org.fd.mcb.modules.master.enums.HoldStatus;
import org.fd.mcb.modules.master.model.entity.AccountHold;
import org.fd.mcb.modules.master.model.repository.AccountHoldRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountHoldQueryAdapterImpl implements AccountHoldQueryAdapter {

    private final AccountHoldRepository accountHoldRepository;

    @Override
    public List<AccountHold> findActiveHoldsByAccountId(Long accountId) {
        return accountHoldRepository.findByAccountIdAndStatus(accountId, HoldStatus.ACTIVE);
    }

    @Override
    public AccountHold findByTransactionId(Long transactionId) {
        return accountHoldRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Account hold not found for transaction"));
    }

    @Override
    public BigDecimal calculateTotalActiveHolds(Long accountId) {
        return accountHoldRepository.sumAmountByAccountIdAndStatus(accountId, HoldStatus.ACTIVE);
    }

    @Override
    public List<AccountHold> findExpiredHolds() {
        return accountHoldRepository.findByStatusAndExpiresAtBefore(HoldStatus.ACTIVE, ZonedDateTime.now());
    }

}
