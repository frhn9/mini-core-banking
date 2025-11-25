package org.fd.mcb.modules.reconciliation.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.fd.mcb.modules.reconciliation.enums.ReconciliationStatus;

@Entity
@Table(name = "reconciliation_reports")
@Getter
@Setter
public class ReconciliationReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "reconciliation_date", nullable = false)
    private LocalDate reconciliationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50, nullable = false)
    private ReconciliationStatus status;

    @Column(name = "started_at", nullable = false)
    private ZonedDateTime startedAt;

    @Column(name = "completed_at")
    private ZonedDateTime completedAt;

    @Column(name = "total_discrepancies", nullable = false)
    private Integer totalDiscrepancies = 0;

    @Column(name = "system_balanced", nullable = false)
    private Boolean systemBalanced = true;

    @Column(name = "total_debits", precision = 18, scale = 2)
    private BigDecimal totalDebits;

    @Column(name = "total_credits", precision = 18, scale = 2)
    private BigDecimal totalCredits;

    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ReconciliationDiscrepancy> discrepancies = new ArrayList<>();
}
