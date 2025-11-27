package org.fd.mcb.shared.exception;

import org.fd.mcb.configs.exception.ModuleException;
import org.fd.mcb.shared.response.ResponseEnum;

public class NegativeBalanceNotAllowedException extends ModuleException {
    public NegativeBalanceNotAllowedException() {
        super(ResponseEnum.NEGATIVE_BALANCE_NOT_ALLOWED);
    }
}
