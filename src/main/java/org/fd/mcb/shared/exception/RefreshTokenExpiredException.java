package org.fd.mcb.shared.exception;

import org.fd.mcb.configs.exception.ModuleException;
import org.fd.mcb.shared.response.ResponseEnum;

public class RefreshTokenExpiredException extends ModuleException {
    public RefreshTokenExpiredException() {
        super(ResponseEnum.REFRESH_TOKEN_EXPIRED);
    }
}
