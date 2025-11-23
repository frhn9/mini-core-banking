package org.fd.mcb.shared.exception;

import org.fd.mcb.configs.exception.ModuleException;
import org.fd.mcb.shared.response.ResponseEnum;

public class InvalidTransactionStatusException extends ModuleException {
    public InvalidTransactionStatusException() {
        super(ResponseEnum.INVALID_TRANSACTION_STATUS);
    }
}
