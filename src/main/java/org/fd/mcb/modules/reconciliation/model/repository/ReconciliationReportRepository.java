package org.fd.mcb.modules.reconciliation.model.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.fd.mcb.modules.reconciliation.enums.ReconciliationStatus;
import org.fd.mcb.modules.reconciliation.model.entity.ReconciliationReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReconciliationReportRepository extends CrudRepository<ReconciliationReport, Long> {

    Optional<ReconciliationReport> findByReconciliationDate(LocalDate reconciliationDate);

    List<ReconciliationReport> findByStatus(ReconciliationStatus status);

    @Query("SELECT r FROM ReconciliationReport r WHERE r.reconciliationDate BETWEEN :startDate AND :endDate ORDER BY r.reconciliationDate DESC")
    List<ReconciliationReport> findByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT r FROM ReconciliationReport r ORDER BY r.createdAt DESC")
    Page<ReconciliationReport> findAllOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT r FROM ReconciliationReport r WHERE r.status = :status ORDER BY r.createdAt DESC")
    Page<ReconciliationReport> findByStatusOrderByCreatedAtDesc(
            @Param("status") ReconciliationStatus status,
            Pageable pageable);

    @Query("SELECT r FROM ReconciliationReport r ORDER BY r.reconciliationDate DESC LIMIT 1")
    Optional<ReconciliationReport> findLatest();
}
