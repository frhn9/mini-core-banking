package org.fd.mcb.modules.reconciliation.controller;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.fd.mcb.modules.reconciliation.adapter.query.ReconciliationQueryAdapter;
import org.fd.mcb.modules.reconciliation.dto.ReconciliationDiscrepancyDto;
import org.fd.mcb.modules.reconciliation.dto.ReconciliationReportDto;
import org.fd.mcb.modules.reconciliation.enums.DiscrepancySeverity;
import org.fd.mcb.modules.reconciliation.model.entity.ReconciliationDiscrepancy;
import org.fd.mcb.modules.reconciliation.model.entity.ReconciliationReport;
import org.fd.mcb.modules.reconciliation.service.ReconciliationResponseService;
import org.fd.mcb.modules.reconciliation.service.ReconciliationService;
import org.fd.mcb.shared.exception.ReconciliationNotFoundException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reconciliation")
@RequiredArgsConstructor
public class ReconciliationController {

    private final ReconciliationService reconciliationService;
    private final ReconciliationResponseService responseService;
    private final ReconciliationQueryAdapter queryAdapter;

    @PostMapping("/run")
    public ResponseEntity<ReconciliationReportDto> runReconciliation(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        ReconciliationReport report = reconciliationService.performReconciliation(date);
        return ResponseEntity.ok(toDto(report));
    }

    @GetMapping("/reports/latest")
    public ResponseEntity<ReconciliationReportDto> getLatestReport() {
        ReconciliationReport report = reconciliationService.getLatestReport();
        return ResponseEntity.ok(toDto(report));
    }

    @GetMapping("/reports/{id}")
    public ResponseEntity<ReconciliationReportDto> getReport(@PathVariable Long id) {
        ReconciliationReport report = queryAdapter.findReportById(id)
            .orElseThrow(ReconciliationNotFoundException::new);
        return ResponseEntity.ok(toDto(report));
    }

    @GetMapping("/reports/{id}/discrepancies")
    public ResponseEntity<List<ReconciliationDiscrepancyDto>> getDiscrepancies(
            @PathVariable Long id,
            @RequestParam(required = false) DiscrepancySeverity severity) {

        ReconciliationReport report = queryAdapter.findReportById(id)
            .orElseThrow(ReconciliationNotFoundException::new);

        List<ReconciliationDiscrepancy> discrepancies = severity != null ?
            queryAdapter.findDiscrepanciesByReportAndSeverity(report, severity) :
            queryAdapter.findDiscrepanciesByReport(report);

        return ResponseEntity.ok(discrepancies.stream()
            .map(this::toDto)
            .toList());
    }

    @PostMapping("/accounts/{accountId}/unblock")
    public ResponseEntity<Void> unblockAccount(
            @PathVariable Long accountId,
            @RequestParam String justification) {

        responseService.unblockAccount(accountId, justification);
        return ResponseEntity.ok().build();
    }

    private ReconciliationReportDto toDto(ReconciliationReport report) {
        ReconciliationReportDto dto = new ReconciliationReportDto();
        dto.setId(report.getId());
        dto.setReconciliationDate(report.getReconciliationDate());
        dto.setStatus(report.getStatus());
        dto.setStartedAt(report.getStartedAt());
        dto.setCompletedAt(report.getCompletedAt());
        dto.setTotalDiscrepancies(report.getTotalDiscrepancies());
        dto.setSystemBalanced(report.getSystemBalanced());
        dto.setTotalDebits(report.getTotalDebits());
        dto.setTotalCredits(report.getTotalCredits());
        return dto;
    }

    private ReconciliationDiscrepancyDto toDto(ReconciliationDiscrepancy discrepancy) {
        ReconciliationDiscrepancyDto dto = new ReconciliationDiscrepancyDto();
        dto.setId(discrepancy.getId());
        dto.setDiscrepancyType(discrepancy.getDiscrepancyType());
        dto.setEntityType(discrepancy.getEntityType());
        dto.setEntityId(discrepancy.getEntityId());
        dto.setExpectedValue(discrepancy.getExpectedValue());
        dto.setActualValue(discrepancy.getActualValue());
        dto.setSeverity(discrepancy.getSeverity());
        dto.setDescription(discrepancy.getDescription());
        dto.setAutoCorrected(discrepancy.getAutoCorrected());
        return dto;
    }
}
