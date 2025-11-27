package org.fd.mcb.modules.reconciliation.adapter.command.impl;

import lombok.RequiredArgsConstructor;
import org.fd.mcb.modules.master.model.entity.BankAccount;
import org.fd.mcb.modules.reconciliation.adapter.command.AccountLimitsCommandAdapter;
import org.fd.mcb.modules.reconciliation.model.entity.AccountLimits;
import org.fd.mcb.modules.reconciliation.model.repository.AccountLimitsRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountLimitsCommandAdapterImpl implements AccountLimitsCommandAdapter {

    private final AccountLimitsRepository accountLimitsRepository;

    @Override
    public AccountLimits save(AccountLimits accountLimits) {
        return accountLimitsRepository.save(accountLimits);
    }

    @Override
    public void deleteByAccount(BankAccount account) {
        accountLimitsRepository.deleteByAccount(account);
    }

    @Override
    public void deleteById(Long id) {
        accountLimitsRepository.deleteById(id);
    }
}
