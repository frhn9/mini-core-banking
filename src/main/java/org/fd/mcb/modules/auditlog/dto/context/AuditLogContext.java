package org.fd.mcb.modules.auditlog.dto.context;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.fd.mcb.modules.staff.model.entity.Staff;

@Getter
@Setter
@Builder
public class AuditLogContext {

    private Staff user;
    private String action;
    private String details;
}
