package org.fd.mcb.shared.exception;

import org.fd.mcb.configs.exception.ModuleException;
import org.fd.mcb.shared.response.ResponseEnum;

public class TokenExpiredException extends ModuleException {
    public TokenExpiredException() {
        super(ResponseEnum.TOKEN_EXPIRED);
    }
}
