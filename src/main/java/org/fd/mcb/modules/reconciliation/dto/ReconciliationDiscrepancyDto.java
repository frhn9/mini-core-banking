package org.fd.mcb.modules.reconciliation.dto;

import lombok.Data;
import org.fd.mcb.modules.reconciliation.enums.DiscrepancySeverity;
import org.fd.mcb.modules.reconciliation.enums.DiscrepancyType;

@Data
public class ReconciliationDiscrepancyDto {
    private Long id;
    private DiscrepancyType discrepancyType;
    private String entityType;
    private Long entityId;
    private String expectedValue;
    private String actualValue;
    private DiscrepancySeverity severity;
    private String description;
    private Boolean autoCorrected;
}
