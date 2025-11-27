package org.fd.mcb.modules.auditlog.adapter.command;

import org.fd.mcb.modules.auditlog.dto.context.AuditLogContext;
import org.fd.mcb.modules.auditlog.model.entity.AuditLog;

public interface AuditLogCommandAdapter {

    AuditLog save(AuditLogContext context);
}
