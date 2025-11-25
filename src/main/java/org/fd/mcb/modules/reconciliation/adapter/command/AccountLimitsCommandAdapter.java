package org.fd.mcb.modules.reconciliation.adapter.command;

import org.fd.mcb.modules.master.model.entity.BankAccount;
import org.fd.mcb.modules.reconciliation.model.entity.AccountLimits;

public interface AccountLimitsCommandAdapter {

    AccountLimits save(AccountLimits accountLimits);

    void deleteByAccount(BankAccount account);

    void deleteById(Long id);
}
