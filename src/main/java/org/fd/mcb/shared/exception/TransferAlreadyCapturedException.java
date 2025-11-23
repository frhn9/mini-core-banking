package org.fd.mcb.shared.exception;

import org.fd.mcb.configs.exception.ModuleException;
import org.fd.mcb.shared.response.ResponseEnum;

public class TransferAlreadyCapturedException extends ModuleException {
    public TransferAlreadyCapturedException() {
        super(ResponseEnum.TRANSFER_ALREADY_CAPTURED);
    }
}
