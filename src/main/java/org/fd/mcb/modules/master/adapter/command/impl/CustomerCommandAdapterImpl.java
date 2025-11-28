package org.fd.mcb.modules.master.adapter.command.impl;

import lombok.RequiredArgsConstructor;
import org.fd.mcb.modules.master.adapter.command.CustomerCommandAdapter;
import org.fd.mcb.modules.master.model.entity.Customer;
import org.fd.mcb.modules.master.model.repository.CustomerRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerCommandAdapterImpl implements CustomerCommandAdapter {
  private final CustomerRepository customerRepository;

  @Override
  public Customer save(Customer customer) {
    return customerRepository.save(customer);
  }
}
