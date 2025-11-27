package org.fd.mcb.modules.reconciliation.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import lombok.Data;
import org.fd.mcb.modules.reconciliation.enums.ReconciliationStatus;

@Data
public class ReconciliationReportDto {
    private Long id;
    private LocalDate reconciliationDate;
    private ReconciliationStatus status;
    private ZonedDateTime startedAt;
    private ZonedDateTime completedAt;
    private Integer totalDiscrepancies;
    private Boolean systemBalanced;
    private BigDecimal totalDebits;
    private BigDecimal totalCredits;
}
