package org.fd.mcb.shared.exception;

import org.fd.mcb.configs.exception.ModuleException;
import org.fd.mcb.shared.response.ResponseEnum;

public class InsufficientAvailableBalanceException extends ModuleException {
    public InsufficientAvailableBalanceException() {
        super(ResponseEnum.AVAILABLE_BALANCE_INSUFFICIENT);
    }
}
