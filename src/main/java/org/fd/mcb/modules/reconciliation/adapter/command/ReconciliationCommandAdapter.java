package org.fd.mcb.modules.reconciliation.adapter.command;

import org.fd.mcb.modules.reconciliation.model.entity.ReconciliationDiscrepancy;
import org.fd.mcb.modules.reconciliation.model.entity.ReconciliationReport;

public interface ReconciliationCommandAdapter {

    ReconciliationReport saveReport(ReconciliationReport report);

    ReconciliationDiscrepancy saveDiscrepancy(ReconciliationDiscrepancy discrepancy);

    void deleteDiscrepancy(Long discrepancyId);
}
