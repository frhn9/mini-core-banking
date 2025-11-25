package org.fd.mcb.modules.reconciliation.adapter.query.impl;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.fd.mcb.modules.master.model.entity.BankAccount;
import org.fd.mcb.modules.reconciliation.adapter.query.AccountLimitsQueryAdapter;
import org.fd.mcb.modules.reconciliation.model.entity.AccountLimits;
import org.fd.mcb.modules.reconciliation.model.repository.AccountLimitsRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountLimitsQueryAdapterImpl implements AccountLimitsQueryAdapter {

    private final AccountLimitsRepository accountLimitsRepository;

    @Override
    public Optional<AccountLimits> findByAccount(BankAccount account) {
        return accountLimitsRepository.findByAccount(account);
    }

    @Override
    public Optional<AccountLimits> findActiveByAccountId(Long accountId, ZonedDateTime now) {
        return accountLimitsRepository.findActiveByAccountId(accountId, now);
    }

    @Override
    public List<AccountLimits> findExpired(ZonedDateTime now) {
        return accountLimitsRepository.findExpired(now);
    }
}
