package org.fd.mcb.modules.transaction.scheduled;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fd.mcb.modules.transaction.service.TransferAuthorizationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class HoldExpirationJob {

    private final TransferAuthorizationService transferAuthService;

    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void expireOldHolds() {
        log.info("Running hold expiration job");
        try {
            transferAuthService.expireHolds();
            log.info("Hold expiration job completed successfully");
        } catch (Exception e) {
            log.error("Error during hold expiration job", e);
        }
    }
}
