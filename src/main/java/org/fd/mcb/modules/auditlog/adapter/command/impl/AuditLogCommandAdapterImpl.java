package org.fd.mcb.modules.auditlog.adapter.command.impl;

import lombok.RequiredArgsConstructor;
import org.fd.mcb.modules.auditlog.adapter.command.AuditLogCommandAdapter;
import org.fd.mcb.modules.auditlog.dto.context.AuditLogContext;
import org.fd.mcb.modules.auditlog.model.entity.AuditLog;
import org.fd.mcb.modules.auditlog.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditLogCommandAdapterImpl implements AuditLogCommandAdapter {

    private final AuditLogRepository auditLogRepository;

    @Override
    public AuditLog save(AuditLogContext context) {
        AuditLog auditLog = new AuditLog();
        auditLog.setUser(context.getUser());
        auditLog.setAction(context.getAction());
        auditLog.setDetails(context.getDetails());
        return auditLogRepository.save(auditLog);
    }
}
