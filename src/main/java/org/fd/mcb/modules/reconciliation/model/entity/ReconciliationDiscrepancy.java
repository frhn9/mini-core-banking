package org.fd.mcb.modules.reconciliation.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import lombok.Getter;
import lombok.Setter;
import org.fd.mcb.modules.reconciliation.enums.DiscrepancySeverity;
import org.fd.mcb.modules.reconciliation.enums.DiscrepancyType;

@Entity
@Table(name = "reconciliation_discrepancies")
@Getter
@Setter
public class ReconciliationDiscrepancy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private ReconciliationReport report;

    @Enumerated(EnumType.STRING)
    @Column(name = "discrepancy_type", length = 50, nullable = false)
    private DiscrepancyType discrepancyType;

    @Column(name = "entity_type", length = 50, nullable = false)
    private String entityType;

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Column(name = "expected_value")
    private String expectedValue;

    @Column(name = "actual_value")
    private String actualValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", length = 50, nullable = false)
    private DiscrepancySeverity severity;

    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "auto_corrected", nullable = false)
    private Boolean autoCorrected = false;

    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;
}
