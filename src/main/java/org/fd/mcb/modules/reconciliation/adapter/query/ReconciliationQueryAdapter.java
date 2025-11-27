package org.fd.mcb.modules.reconciliation.adapter.query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.fd.mcb.modules.reconciliation.enums.DiscrepancySeverity;
import org.fd.mcb.modules.reconciliation.enums.ReconciliationStatus;
import org.fd.mcb.modules.reconciliation.model.entity.ReconciliationDiscrepancy;
import org.fd.mcb.modules.reconciliation.model.entity.ReconciliationReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReconciliationQueryAdapter {

    Optional<ReconciliationReport> findReportById(Long id);

    Optional<ReconciliationReport> findReportByDate(LocalDate date);

    Optional<ReconciliationReport> findLatestReport();

    Page<ReconciliationReport> findAllReports(Pageable pageable);

    Page<ReconciliationReport> findReportsByStatus(ReconciliationStatus status, Pageable pageable);

    List<ReconciliationDiscrepancy> findDiscrepanciesByReport(ReconciliationReport report);

    List<ReconciliationDiscrepancy> findDiscrepanciesByReportAndSeverity(ReconciliationReport report, DiscrepancySeverity severity);

    List<ReconciliationDiscrepancy> findDiscrepanciesByAccountId(Long accountId);
}
