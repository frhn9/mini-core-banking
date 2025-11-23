package org.fd.mcb.shared.exception;

import org.fd.mcb.configs.exception.ModuleException;
import org.fd.mcb.shared.response.ResponseEnum;

public class AuthorizationExpiredException extends ModuleException {
    public AuthorizationExpiredException() {
        super(ResponseEnum.AUTHORIZATION_EXPIRED);
    }
}
