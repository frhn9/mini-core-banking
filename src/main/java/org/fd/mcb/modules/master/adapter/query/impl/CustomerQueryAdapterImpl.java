package org.fd.mcb.modules.master.adapter.query.impl;

import lombok.RequiredArgsConstructor;
import org.fd.mcb.modules.master.adapter.query.CustomerQueryAdapter;
import org.fd.mcb.modules.master.model.repository.CustomerRepository;
import org.fd.mcb.shared.exception.CustomerCinAlreadyExistsException;
import org.fd.mcb.shared.exception.CustomerEmailAlreadyExistsException;
import org.fd.mcb.shared.exception.CustomerNationalIdAlreadyExistsException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerQueryAdapterImpl implements CustomerQueryAdapter {
  private final CustomerRepository customerRepository;

  @Override
  public void validateCustomerUniqueness(String cin, String email, String nationalId) {
    if (customerRepository.existsByCin(cin)) {
      throw new CustomerCinAlreadyExistsException();
    }
    if (customerRepository.existsByEmail(email)) {
      throw new CustomerEmailAlreadyExistsException();
    }
    if (customerRepository.existsByNationalId(nationalId)) {
      throw new CustomerNationalIdAlreadyExistsException();
    }
  }
}
