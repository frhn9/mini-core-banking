package org.fd.mcb.shared.exception;

import org.fd.mcb.configs.exception.ModuleException;
import org.fd.mcb.shared.response.ResponseEnum;

public class BankAccountNotActiveException extends ModuleException {
    public BankAccountNotActiveException() {
        super(ResponseEnum.BANK_ACCOUNT_NOT_ACTIVE);
    }
}