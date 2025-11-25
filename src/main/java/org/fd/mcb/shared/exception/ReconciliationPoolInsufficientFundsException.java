package org.fd.mcb.shared.exception;

import org.fd.mcb.configs.exception.ModuleException;
import org.fd.mcb.shared.response.ResponseEnum;

public class ReconciliationPoolInsufficientFundsException extends ModuleException {
    public ReconciliationPoolInsufficientFundsException() {
        super(ResponseEnum.RECONCILIATION_POOL_INSUFFICIENT_FUNDS);
    }
}
