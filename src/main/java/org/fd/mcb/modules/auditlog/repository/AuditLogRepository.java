
package org.fd.mcb.modules.auditlog.repository;

import java.util.UUID;
import org.fd.mcb.modules.auditlog.model.entity.AuditLog;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends CrudRepository<AuditLog, UUID> {
}
