package org.fd.mcb.shared.exception;

import org.fd.mcb.configs.exception.ModuleException;
import org.fd.mcb.shared.response.ResponseEnum;

public class TransactionLimitExceededException extends ModuleException {
    public TransactionLimitExceededException() {
        super(ResponseEnum.TRANSACTION_LIMIT_EXCEEDED);
    }
}
