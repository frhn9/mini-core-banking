package org.fd.mcb.modules.transaction.mapper;

import org.fd.mcb.modules.master.model.entity.BankAccount;
import org.fd.mcb.modules.master.model.entity.Transaction;
import org.fd.mcb.modules.transaction.dto.response.DepositResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DepositMapper {

    @Mapping(target = "transactionId", source = "transaction.id")
    @Mapping(target = "currentBalance", source = "bankAccount.balance")
    DepositResponse toDepositResponse(Transaction transaction, BankAccount bankAccount);
}


