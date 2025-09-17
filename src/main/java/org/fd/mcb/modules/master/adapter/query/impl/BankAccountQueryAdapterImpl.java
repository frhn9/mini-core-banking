package org.fd.mcb.modules.master.adapter.query.impl;

import lombok.RequiredArgsConstructor;
import org.fd.mcb.modules.master.adapter.query.BankAccountQueryAdapter;
import org.fd.mcb.modules.master.enums.AccountStatus;
import org.fd.mcb.modules.master.enums.AccountType;
import org.fd.mcb.modules.master.model.entity.BankAccount;
import org.fd.mcb.modules.master.model.repository.BankAccountRepository;
import org.fd.mcb.shared.exception.BankAccountNotActiveException;
import org.fd.mcb.shared.exception.BankAccountNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BankAccountQueryAdapterImpl implements BankAccountQueryAdapter {

    private final BankAccountRepository bankAccountRepository;

    @Override
    public BankAccount findByAccountNumber(String accountNumber, AccountType accountType) {
        BankAccount bankAccount = bankAccountRepository
                .findByAccountNumberAndAccountType(accountNumber, accountType)
                .orElseThrow(BankAccountNotFoundException::new);

        if (bankAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new BankAccountNotActiveException();
        }

        return bankAccount;
    }

}
