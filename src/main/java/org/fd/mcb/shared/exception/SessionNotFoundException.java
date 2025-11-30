package org.fd.mcb.shared.exception;

import org.fd.mcb.configs.exception.ModuleException;
import org.fd.mcb.shared.response.ResponseEnum;

public class SessionNotFoundException extends ModuleException {
    public SessionNotFoundException() {
        super(ResponseEnum.SESSION_NOT_FOUND);
    }
}
