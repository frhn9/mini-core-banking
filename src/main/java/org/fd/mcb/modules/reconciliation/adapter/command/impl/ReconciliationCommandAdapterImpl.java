package org.fd.mcb.modules.reconciliation.adapter.command.impl;

import lombok.RequiredArgsConstructor;
import org.fd.mcb.modules.reconciliation.adapter.command.ReconciliationCommandAdapter;
import org.fd.mcb.modules.reconciliation.model.entity.ReconciliationDiscrepancy;
import org.fd.mcb.modules.reconciliation.model.entity.ReconciliationReport;
import org.fd.mcb.modules.reconciliation.model.repository.ReconciliationDiscrepancyRepository;
import org.fd.mcb.modules.reconciliation.model.repository.ReconciliationReportRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReconciliationCommandAdapterImpl implements ReconciliationCommandAdapter {

    private final ReconciliationReportRepository reportRepository;
    private final ReconciliationDiscrepancyRepository discrepancyRepository;

    @Override
    public ReconciliationReport saveReport(ReconciliationReport report) {
        return reportRepository.save(report);
    }

    @Override
    public ReconciliationDiscrepancy saveDiscrepancy(ReconciliationDiscrepancy discrepancy) {
        return discrepancyRepository.save(discrepancy);
    }

    @Override
    public void deleteDiscrepancy(Long discrepancyId) {
        discrepancyRepository.deleteById(discrepancyId);
    }
}
