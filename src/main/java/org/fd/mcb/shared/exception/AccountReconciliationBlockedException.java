package org.fd.mcb.shared.exception;

import org.fd.mcb.configs.exception.ModuleException;
import org.fd.mcb.shared.response.ResponseEnum;

public class AccountReconciliationBlockedException extends ModuleException {
    public AccountReconciliationBlockedException() {
        super(ResponseEnum.ACCOUNT_RECONCILIATION_BLOCKED);
    }
}
