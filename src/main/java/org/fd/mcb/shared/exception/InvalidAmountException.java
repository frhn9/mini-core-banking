package org.fd.mcb.shared.exception;

import org.fd.mcb.configs.exception.ModuleException;
import org.fd.mcb.shared.response.ResponseEnum;

public class InvalidAmountException extends ModuleException {
    public InvalidAmountException() {
        super(ResponseEnum.INVALID_AMOUNT);
    }
}
