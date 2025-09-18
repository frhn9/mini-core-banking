package org.fd.mcb.modules.transaction.mapper;

import org.fd.mcb.modules.master.model.entity.BankAccount;
import org.fd.mcb.modules.master.model.entity.Transaction;
import org.fd.mcb.modules.transaction.dto.response.AccountResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(target = "transactionId", source = "transaction.id")
    @Mapping(target = "currentBalance", source = "bankAccount.balance")
    AccountResponse toAccountResponse(Transaction transaction, BankAccount bankAccount);

}


