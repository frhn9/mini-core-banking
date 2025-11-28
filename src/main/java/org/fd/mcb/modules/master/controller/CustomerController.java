package org.fd.mcb.modules.master.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.fd.mcb.modules.master.dto.request.CustomerRegistrationRequest;
import org.fd.mcb.modules.master.dto.response.CustomerRegistrationResponse;
import org.fd.mcb.modules.master.service.CustomerRegistrationService;
import org.fd.mcb.shared.response.ResponseEnum;
import org.fd.mcb.shared.response.ResponseHelper;
import org.fd.mcb.shared.response.template.ResponseData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {
  private final ResponseHelper responseHelper;
  private final CustomerRegistrationService customerRegistrationService;

  @PostMapping("/register")
  public ResponseEntity<ResponseData<CustomerRegistrationResponse>> registerCustomer(
          @Valid @RequestBody CustomerRegistrationRequest request
  ) {
    return responseHelper.createResponseData(
            ResponseEnum.SUCCESS,
            customerRegistrationService.registerCustomer(request)
    );
  }
}
