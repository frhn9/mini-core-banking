package org.fd.mcb.shared.exception;

import org.fd.mcb.configs.exception.ModuleException;
import org.fd.mcb.shared.response.ResponseEnum;

public class PaymentTypeNotFoundException extends ModuleException {
    public PaymentTypeNotFoundException() {
        super(ResponseEnum.PAYMENT_TYPE_NOT_FOUND);
    }
}
