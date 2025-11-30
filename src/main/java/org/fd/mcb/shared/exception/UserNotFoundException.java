package org.fd.mcb.shared.exception;

import org.fd.mcb.configs.exception.ModuleException;
import org.fd.mcb.shared.response.ResponseEnum;

public class UserNotFoundException extends ModuleException {
    public UserNotFoundException() {
        super(ResponseEnum.USER_NOT_FOUND);
    }
}
