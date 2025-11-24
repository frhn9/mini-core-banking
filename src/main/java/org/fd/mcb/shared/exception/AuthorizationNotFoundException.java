package org.fd.mcb.shared.exception;

import org.fd.mcb.configs.exception.ModuleException;
import org.fd.mcb.shared.response.ResponseEnum;

public class AuthorizationNotFoundException extends ModuleException {
    public AuthorizationNotFoundException() {
        super(ResponseEnum.AUTHORIZATION_NOT_FOUND);
    }
}
