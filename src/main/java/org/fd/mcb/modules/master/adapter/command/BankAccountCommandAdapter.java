package org.fd.mcb.modules.master.adapter.command;

import org.fd.mcb.modules.master.model.entity.BankAccount;

public interface BankAccountCommandAdapter {

    BankAccount save(BankAccount bankAccount);

}
