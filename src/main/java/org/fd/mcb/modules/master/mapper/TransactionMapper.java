package org.fd.mcb.modules.master.mapper;

import org.fd.mcb.modules.master.model.entity.Transaction;
import org.fd.mcb.modules.transaction.dto.context.TransactionContext;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    Transaction toTransactionFromContext(TransactionContext context);

}
