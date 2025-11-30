package org.fd.mcb.shared.exception;

import lombok.RequiredArgsConstructor;
import org.fd.mcb.configs.exception.ModuleException;
import org.fd.mcb.shared.response.ResponseEnum;

public class RefreshTokenNotFoundException extends ModuleException {
    public RefreshTokenNotFoundException() {
        super(ResponseEnum.REFRESH_TOKEN_NOT_FOUND);
    }
}
