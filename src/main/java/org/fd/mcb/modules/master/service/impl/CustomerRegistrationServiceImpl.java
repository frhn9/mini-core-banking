package org.fd.mcb.modules.master.service.impl;

import lombok.RequiredArgsConstructor;
import org.fd.mcb.modules.master.adapter.command.CustomerCommandAdapter;
import org.fd.mcb.modules.master.adapter.query.CustomerQueryAdapter;
import org.fd.mcb.modules.master.dto.request.CustomerRegistrationRequest;
import org.fd.mcb.modules.master.dto.response.CustomerRegistrationResponse;
import org.fd.mcb.modules.master.enums.CustomerStatus;
import org.fd.mcb.modules.master.mapper.CustomerMapper;
import org.fd.mcb.modules.master.model.entity.Customer;
import org.fd.mcb.modules.master.service.CustomerRegistrationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class CustomerRegistrationServiceImpl implements CustomerRegistrationService {
  private final CustomerQueryAdapter customerQueryAdapter;
  private final CustomerCommandAdapter customerCommandAdapter;
  private final CustomerMapper customerMapper;

  @Override
  @Transactional
  public CustomerRegistrationResponse registerCustomer(CustomerRegistrationRequest request) {
    // 1. Validate uniqueness
    customerQueryAdapter.validateCustomerUniqueness(
            request.getCin(), request.getEmail(), request.getNationalId()
    );

    // 2. Map to entity
    Customer customer = customerMapper.toEntity(request);

    // 3. Set defaults
    customer.setStatus(CustomerStatus.ACTIVE);
    customer.setCreatedAt(ZonedDateTime.now());
    customer.setIsBlockedByBank(false);

    // 4. Save
    Customer savedCustomer = customerCommandAdapter.save(customer);

    // 5. Return response
    return customerMapper.toRegistrationResponse(savedCustomer);
  }
}
