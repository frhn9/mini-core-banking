package org.fd.mcb.modules.master.adapter.command;

import org.fd.mcb.modules.master.model.entity.Transaction;
import org.fd.mcb.modules.transaction.dto.context.TransactionContext;

public interface TransactionCommandAdapter {

    Transaction save(TransactionContext transactionContext);

}
