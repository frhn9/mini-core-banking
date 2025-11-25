package org.fd.mcb.shared.exception;

import org.fd.mcb.configs.exception.ModuleException;
import org.fd.mcb.shared.response.ResponseEnum;

public class ReconciliationInProgressException extends ModuleException {
    public ReconciliationInProgressException() {
        super(ResponseEnum.RECONCILIATION_IN_PROGRESS);
    }
}
