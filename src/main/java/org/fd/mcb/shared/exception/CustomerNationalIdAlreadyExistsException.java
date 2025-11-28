package org.fd.mcb.shared.exception;

import org.fd.mcb.configs.exception.ModuleException;
import org.fd.mcb.shared.response.ResponseEnum;

public class CustomerNationalIdAlreadyExistsException extends ModuleException {
  public CustomerNationalIdAlreadyExistsException() {
    super(ResponseEnum.CUSTOMER_NATIONAL_ID_ALREADY_EXISTS);
  }
}
