package org.fd.mcb.modules.master.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.fd.mcb.modules.master.enums.CustomerStatus;

import java.time.ZonedDateTime;

@Getter
@Setter
@Builder
public class CustomerRegistrationResponse {
  private Long customerId;
  private String cin;
  private String fullName;
  private String email;
  private String phoneNumber;
  private CustomerStatus status;
  private ZonedDateTime createdAt;
}
