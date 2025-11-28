package org.fd.mcb.modules.master.mapper;

import org.fd.mcb.modules.master.dto.request.CustomerRegistrationRequest;
import org.fd.mcb.modules.master.dto.response.CustomerRegistrationResponse;
import org.fd.mcb.modules.master.model.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "deletedAt", ignore = true)
  @Mapping(target = "isBlockedByBank", ignore = true)
  @Mapping(target = "blockedByBankReason", ignore = true)
  @Mapping(target = "accountBalanceDerived", ignore = true)
  Customer toEntity(CustomerRegistrationRequest request);

  @Mapping(target = "customerId", source = "id")
  CustomerRegistrationResponse toRegistrationResponse(Customer customer);
}
