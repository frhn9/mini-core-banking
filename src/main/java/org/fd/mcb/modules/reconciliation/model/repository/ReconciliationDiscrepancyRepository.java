package org.fd.mcb.modules.reconciliation.model.repository;

import java.util.List;
import org.fd.mcb.modules.reconciliation.enums.DiscrepancySeverity;
import org.fd.mcb.modules.reconciliation.enums.DiscrepancyType;
import org.fd.mcb.modules.reconciliation.model.entity.ReconciliationDiscrepancy;
import org.fd.mcb.modules.reconciliation.model.entity.ReconciliationReport;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReconciliationDiscrepancyRepository extends CrudRepository<ReconciliationDiscrepancy, Long> {

    List<ReconciliationDiscrepancy> findByReport(ReconciliationReport report);

    List<ReconciliationDiscrepancy> findByReportAndSeverity(ReconciliationReport report, DiscrepancySeverity severity);

    List<ReconciliationDiscrepancy> findByEntityTypeAndEntityId(String entityType, Long entityId);

    @Query("SELECT d FROM ReconciliationDiscrepancy d WHERE d.entityType = 'BANK_ACCOUNT' AND d.entityId = :accountId ORDER BY d.createdAt DESC")
    List<ReconciliationDiscrepancy> findByAccountId(@Param("accountId") Long accountId);

    @Query("SELECT d FROM ReconciliationDiscrepancy d WHERE d.report = :report AND d.discrepancyType = :type")
    List<ReconciliationDiscrepancy> findByReportAndType(
            @Param("report") ReconciliationReport report,
            @Param("type") DiscrepancyType type);

    @Query("SELECT d FROM ReconciliationDiscrepancy d WHERE d.severity = :severity AND d.autoCorrected = false ORDER BY d.createdAt DESC")
    List<ReconciliationDiscrepancy> findUncorrectedBySeverity(@Param("severity") DiscrepancySeverity severity);
}
