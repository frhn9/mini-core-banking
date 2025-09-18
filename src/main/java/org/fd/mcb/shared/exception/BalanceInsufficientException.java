package org.fd.mcb.shared.exception;

import org.fd.mcb.configs.exception.ModuleException;
import org.fd.mcb.shared.response.ResponseEnum;

public class BalanceInsufficientException extends ModuleException {
    public BalanceInsufficientException() {
        super(ResponseEnum.BALANCE_INSUFFICIENT);
    }
}
