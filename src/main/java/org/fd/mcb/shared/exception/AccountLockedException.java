package org.fd.mcb.shared.exception;

import lombok.Getter;
import org.fd.mcb.configs.exception.ModuleException;
import org.fd.mcb.shared.response.ResponseEnum;

@Getter
public class AccountLockedException extends ModuleException {
    public AccountLockedException() {
        super(ResponseEnum.ACCOUNT_LOCKED);
    }

}
