package org.fd.mcb.modules.reconciliation.service;

import java.time.LocalDate;
import org.fd.mcb.modules.reconciliation.model.entity.ReconciliationReport;

public interface ReconciliationService {

    /**
     * Perform complete reconciliation for a specific date
     * @param date The date to reconcile
     * @return The reconciliation report
     */
    ReconciliationReport performReconciliation(LocalDate date);

    /**
     * Get the latest reconciliation report
     * @return The most recent reconciliation report
     */
    ReconciliationReport getLatestReport();
}
