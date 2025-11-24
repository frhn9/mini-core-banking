package org.fd.mcb.shared.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ResponseEnum {
  SUCCESS("success", "success", HttpStatus.OK),
  BALANCE_INSUFFICIENT("balance_insufficient", "balance.insufficient", HttpStatus.BAD_REQUEST),
  AVAILABLE_BALANCE_INSUFFICIENT("available_balance_insufficient", "available.balance.insufficient", HttpStatus.BAD_REQUEST),
  SAVINGS_ACCOUNT_NOT_FOUND("savings_account_not_found", "savings.account.not.found", HttpStatus.NOT_FOUND),
  PAYMENT_TYPE_NOT_FOUND("payment_type_not_found", "payment.type.not.found", HttpStatus.NOT_FOUND),
  USER_BLOCKED_BY_BANK("user_blocked_by_bank", "user.blocked.by.bank", HttpStatus.FORBIDDEN),
  BANK_ACCOUNT_NOT_FOUND("bank_account_not_found", "bank.account.not.found", HttpStatus.NOT_FOUND),
  BANK_ACCOUNT_NOT_ACTIVE("bank_account_not_active", "bank.account.not.active", HttpStatus.BAD_REQUEST),
  INVALID_AMOUNT("invalid_amount", "invalid.amount", HttpStatus.BAD_REQUEST),
  AUTHORIZATION_NOT_FOUND("authorization_not_found", "authorization.not.found", HttpStatus.NOT_FOUND),
  AUTHORIZATION_EXPIRED("authorization_expired", "authorization.expired", HttpStatus.BAD_REQUEST),
  INVALID_TRANSACTION_STATUS("invalid_transaction_status", "invalid.transaction.status", HttpStatus.BAD_REQUEST),
  TRANSFER_ALREADY_CAPTURED("transfer_already_captured", "transfer.already.captured", HttpStatus.BAD_REQUEST),
  TRANSFER_ALREADY_SETTLED("transfer_already_settled", "transfer.already.settled", HttpStatus.BAD_REQUEST),
  UNBALANCED_JOURNAL_ENTRY("unbalanced_journal_entry", "journal.entry.unbalanced", HttpStatus.BAD_REQUEST),
  NEGATIVE_BALANCE_NOT_ALLOWED("negative_balance_not_allowed", "negative.balance.not.allowed", HttpStatus.BAD_REQUEST),
  INVALID_JOURNAL_ENTRY("invalid_journal_entry", "invalid.journal.entry", HttpStatus.BAD_REQUEST),
  JOURNAL_ENTRY_NOT_FOUND("journal_entry_not_found", "journal.entry.not.found", HttpStatus.NOT_FOUND),

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
