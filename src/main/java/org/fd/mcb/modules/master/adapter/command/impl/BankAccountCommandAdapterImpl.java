package org.fd.mcb.modules.master.adapter.command.impl;

import lombok.RequiredArgsConstructor;
import org.fd.mcb.modules.master.adapter.command.BankAccountCommandAdapter;
import org.fd.mcb.modules.master.model.entity.BankAccount;
import org.fd.mcb.modules.master.model.repository.BankAccountRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BankAccountCommandAdapterImpl implements BankAccountCommandAdapter {

    private final BankAccountRepository bankAccountRepository;

    @Override
    public BankAccount save(BankAccount bankAccount) {
        return bankAccountRepository.save(bankAccount);
    }

}
