package org.fd.mcb.shared.exception;

import lombok.Getter;
import org.fd.mcb.configs.exception.ModuleException;
import org.fd.mcb.shared.response.ResponseEnum;

@Getter
public class RefreshTokenRevokedException extends ModuleException {
    public RefreshTokenRevokedException() {
        super(ResponseEnum.REFRESH_TOKEN_REVOKED);
    }
}
