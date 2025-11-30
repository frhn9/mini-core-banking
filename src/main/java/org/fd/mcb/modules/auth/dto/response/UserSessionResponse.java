package org.fd.mcb.modules.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSessionResponse {
    private String sessionId;
    private String deviceInfo;
    private String ipAddress;
    private String userAgent;
    private ZonedDateTime createdAt;
    private ZonedDateTime lastActivityAt;
    private ZonedDateTime expiresAt;
    private Boolean active;
    private Boolean currentSession;
}
