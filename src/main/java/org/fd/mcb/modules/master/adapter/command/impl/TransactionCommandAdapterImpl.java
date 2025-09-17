package org.fd.mcb.modules.master.adapter.command.impl;

import lombok.RequiredArgsConstructor;
import org.fd.mcb.modules.master.adapter.command.TransactionCommandAdapter;
import org.fd.mcb.modules.master.model.entity.Transaction;
import org.fd.mcb.modules.master.model.repository.TransactionRepository;
import org.fd.mcb.modules.transaction.dto.context.TransactionContext;
import org.fd.mcb.modules.master.mapper.TransactionMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionCommandAdapterImpl implements TransactionCommandAdapter {

    private final TransactionRepository transactionRepository;

    private final TransactionMapper transactionMapper;

    @Override
    public Transaction save(TransactionContext transactionContext) {
        return transactionRepository.save(transactionMapper.toTransactionFromContext(transactionContext));
    }
}
