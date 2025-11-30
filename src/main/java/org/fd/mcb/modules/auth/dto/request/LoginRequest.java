package org.fd.mcb.modules.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fd.mcb.shared.enums.UserType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotBlank(message = "Identifier is required")
    private String identifier; // CIN for customers, username for staff

    @NotBlank(message = "Password is required")
    private String password;

    @NotNull(message = "User type is required")
    private UserType userType;

    private String deviceInfo;
    private String ipAddress;
    private Boolean rememberMe = false;
}
