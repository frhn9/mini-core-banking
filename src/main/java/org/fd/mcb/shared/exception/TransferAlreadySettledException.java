package org.fd.mcb.shared.exception;

import org.fd.mcb.configs.exception.ModuleException;
import org.fd.mcb.shared.response.ResponseEnum;

public class TransferAlreadySettledException extends ModuleException {
    public TransferAlreadySettledException() {
        super(ResponseEnum.TRANSFER_ALREADY_SETTLED);
    }
}
