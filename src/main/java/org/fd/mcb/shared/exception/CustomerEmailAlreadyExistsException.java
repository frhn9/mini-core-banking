package org.fd.mcb.shared.exception;

import org.fd.mcb.configs.exception.ModuleException;
import org.fd.mcb.shared.response.ResponseEnum;

public class CustomerEmailAlreadyExistsException extends ModuleException {
  public CustomerEmailAlreadyExistsException() {
    super(ResponseEnum.CUSTOMER_EMAIL_ALREADY_EXISTS);
  }
}
