package org.fd.mcb.modules.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fd.mcb.shared.enums.UserType;

import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private Long expiresIn; // in seconds
    private String tokenType = "Bearer";

    // User information
    private Long userId;
    private String username; // CIN for customers, username for staff
    private String fullName;
    private UserType userType;
    private String role;

    // Session information
    private String sessionId;
    private ZonedDateTime loginAt;
}
