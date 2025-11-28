package org.fd.mcb.modules.master.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerRegistrationRequest {
  @NotBlank(message = "CIN is required")
  private String cin;

  @NotBlank(message = "Full name is required")
  private String fullName;

  @NotBlank(message = "National ID is required")
  private String nationalId;

  @NotBlank(message = "Email is required")
  @Email(message = "Email must be valid")
  private String email;

  @NotBlank(message = "Phone number is required")
  @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number must be valid (10-15 digits)")
  private String phoneNumber;

  private String address;
}
