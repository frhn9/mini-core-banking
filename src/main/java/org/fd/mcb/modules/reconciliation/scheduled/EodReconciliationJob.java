package org.fd.mcb.modules.reconciliation.scheduled;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fd.mcb.modules.reconciliation.model.entity.ReconciliationReport;
import org.fd.mcb.modules.reconciliation.service.ReconciliationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EodReconciliationJob {

    private final ReconciliationService reconciliationService;

    /**
     * Runs End of Day reconciliation at midnight Asia/Jakarta timezone
     * Reconciles the previous day's transactions
     */
    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Jakarta")
    public void performEodReconciliation() {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        log.info("====================================");
        log.info("Starting End of Day Reconciliation");
        log.info("Date: {}", yesterday);
        log.info("====================================");

        try {
            ReconciliationReport report = reconciliationService.performReconciliation(yesterday);

            log.info("====================================");
            log.info("EoD Reconciliation Completed");
            log.info("Report ID: {}", report.getId());
            log.info("Status: {}", report.getStatus());
            log.info("Total Discrepancies: {}", report.getTotalDiscrepancies());
            log.info("System Balanced: {}", report.getSystemBalanced());
            if (report.getTotalDebits() != null && report.getTotalCredits() != null) {
                log.info("Total Debits: {}", report.getTotalDebits());
                log.info("Total Credits: {}", report.getTotalCredits());
            }
            log.info("====================================");

        } catch (Exception e) {
            log.error("====================================");
            log.error("EoD Reconciliation Failed for date: {}", yesterday);
            log.error("Error: {}", e.getMessage(), e);
            log.error("====================================");
        }
    }
}
