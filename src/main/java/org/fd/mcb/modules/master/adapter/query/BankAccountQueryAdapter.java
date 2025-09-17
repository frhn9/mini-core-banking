package org.fd.mcb.modules.master.adapter.query;

import org.fd.mcb.modules.master.enums.AccountType;
import org.fd.mcb.modules.master.model.entity.BankAccount;

public interface BankAccountQueryAdapter {

    BankAccount findByAccountNumber(String accountNumber, AccountType accountType);

}
