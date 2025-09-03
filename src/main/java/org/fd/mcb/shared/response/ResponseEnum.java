package org.fd.mcb.shared.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ResponseEnum {
  SUCCESS("success", "success", HttpStatus.OK),
  BALANCE_INSUFFICIENT("balance_insufficient", "balance.insufficient", HttpStatus.BAD_REQUEST),
  SAVINGS_ACCOUNT_NOT_FOUND("savings_account_not_found", "savings.account.not.found", HttpStatus.NOT_FOUND),
  PAYMENT_TYPE_NOT_FOUND("payment_type_not_found", "payment.type.not.found", HttpStatus.NOT_FOUND),
  USER_BLOCKED_BY_BANK("user_blocked_by_bank", "user.blocked.by.bank", HttpStatus.FORBIDDEN),

  INVALID_PARAM("invalid_param", "invalid.param", HttpStatus.BAD_REQUEST),
  INTERNAL_SERVER_ERROR("internal_server_error", "internal.server.error", HttpStatus.INTERNAL_SERVER_ERROR);

  private final String responseCode;
  private final String responseMessage;
  private final HttpStatus httpStatus;

  ResponseEnum(String responseCode, String responseMessage, HttpStatus httpStatus) {
    this.responseCode = responseCode;
    this.responseMessage = responseMessage;
    this.httpStatus = httpStatus;
  }

}
