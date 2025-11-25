package org.fd.mcb.shared.exception;

import org.fd.mcb.configs.exception.ModuleException;
import org.fd.mcb.shared.response.ResponseEnum;

public class ReconciliationNotFoundException extends ModuleException {
    public ReconciliationNotFoundException() {
        super(ResponseEnum.RECONCILIATION_NOT_FOUND);
    }
}
