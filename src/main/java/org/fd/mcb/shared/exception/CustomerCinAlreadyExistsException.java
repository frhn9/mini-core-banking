package org.fd.mcb.shared.exception;

import org.fd.mcb.configs.exception.ModuleException;
import org.fd.mcb.shared.response.ResponseEnum;

public class CustomerCinAlreadyExistsException extends ModuleException {
  public CustomerCinAlreadyExistsException() {
    super(ResponseEnum.CUSTOMER_CIN_ALREADY_EXISTS);
  }
}
