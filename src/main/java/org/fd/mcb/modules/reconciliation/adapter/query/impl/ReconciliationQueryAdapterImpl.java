package org.fd.mcb.modules.reconciliation.adapter.query.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.fd.mcb.modules.reconciliation.adapter.query.ReconciliationQueryAdapter;
import org.fd.mcb.modules.reconciliation.enums.DiscrepancySeverity;
import org.fd.mcb.modules.reconciliation.enums.ReconciliationStatus;
import org.fd.mcb.modules.reconciliation.model.entity.ReconciliationDiscrepancy;
import org.fd.mcb.modules.reconciliation.model.entity.ReconciliationReport;
import org.fd.mcb.modules.reconciliation.model.repository.ReconciliationDiscrepancyRepository;
import org.fd.mcb.modules.reconciliation.model.repository.ReconciliationReportRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReconciliationQueryAdapterImpl implements ReconciliationQueryAdapter {

    private final ReconciliationReportRepository reportRepository;
    private final ReconciliationDiscrepancyRepository discrepancyRepository;

    @Override
    public Optional<ReconciliationReport> findReportById(Long id) {
        return reportRepository.findById(id);
    }

    @Override
    public Optional<ReconciliationReport> findReportByDate(LocalDate date) {
        return reportRepository.findByReconciliationDate(date);
    }

    @Override
    public Optional<ReconciliationReport> findLatestReport() {
        return reportRepository.findLatest();
    }

    @Override
    public Page<ReconciliationReport> findAllReports(Pageable pageable) {
        return reportRepository.findAllOrderByCreatedAtDesc(pageable);
    }

    @Override
    public Page<ReconciliationReport> findReportsByStatus(ReconciliationStatus status, Pageable pageable) {
        return reportRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
    }

    @Override
    public List<ReconciliationDiscrepancy> findDiscrepanciesByReport(ReconciliationReport report) {
        return discrepancyRepository.findByReport(report);
    }

    @Override
    public List<ReconciliationDiscrepancy> findDiscrepanciesByReportAndSeverity(
            ReconciliationReport report,
            DiscrepancySeverity severity) {
        return discrepancyRepository.findByReportAndSeverity(report, severity);
    }

    @Override
    public List<ReconciliationDiscrepancy> findDiscrepanciesByAccountId(Long accountId) {
        return discrepancyRepository.findByAccountId(accountId);
    }
}
