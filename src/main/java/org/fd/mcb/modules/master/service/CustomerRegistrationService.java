package org.fd.mcb.modules.master.service;

import org.fd.mcb.modules.master.dto.request.CustomerRegistrationRequest;
import org.fd.mcb.modules.master.dto.response.CustomerRegistrationResponse;

public interface CustomerRegistrationService {
  CustomerRegistrationResponse registerCustomer(CustomerRegistrationRequest request);
}
