package org.fd.mcb.modules.master.mapper;

import org.fd.mcb.modules.master.model.entity.AccountHold;
import org.fd.mcb.modules.transaction.dto.context.AccountHoldContext;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountHoldMapper {

    AccountHold toAccountHoldFromContext(AccountHoldContext context);

}
