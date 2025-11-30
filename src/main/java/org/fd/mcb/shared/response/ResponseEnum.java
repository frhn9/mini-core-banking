package org.fd.mcb.shared.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ResponseEnum {
  SUCCESS("success", "Success", HttpStatus.OK),
  BALANCE_INSUFFICIENT("balance_insufficient", "Balance Insufficient", HttpStatus.BAD_REQUEST),
  AVAILABLE_BALANCE_INSUFFICIENT("available_balance_insufficient", "Available Balance Insufficient", HttpStatus.BAD_REQUEST),
  SAVINGS_ACCOUNT_NOT_FOUND("savings_account_not_found", "Savings Account Not Found", HttpStatus.NOT_FOUND),
  PAYMENT_TYPE_NOT_FOUND("payment_type_not_found", "Payment Type Not Found", HttpStatus.NOT_FOUND),
  USER_BLOCKED_BY_BANK("user_blocked_by_bank", "User Blocked by Bank", HttpStatus.FORBIDDEN),
  BANK_ACCOUNT_NOT_FOUND("bank_account_not_found", "Bank Account Not Found", HttpStatus.NOT_FOUND),
  BANK_ACCOUNT_NOT_ACTIVE("bank_account_not_active", "Bank Account Not Active", HttpStatus.BAD_REQUEST),
  INVALID_AMOUNT("invalid_amount", "Invalid Amount", HttpStatus.BAD_REQUEST),
  AUTHORIZATION_NOT_FOUND("authorization_not_found", "Authorization Not Found", HttpStatus.NOT_FOUND),
  AUTHORIZATION_EXPIRED("authorization_expired", "Authorization Expired", HttpStatus.BAD_REQUEST),
  INVALID_TRANSACTION_STATUS("invalid_transaction_status", "Invalid Transaction Status", HttpStatus.BAD_REQUEST),
  TRANSFER_ALREADY_CAPTURED("transfer_already_captured", "Transfer Already Captured", HttpStatus.BAD_REQUEST),
  TRANSFER_ALREADY_SETTLED("transfer_already_settled", "Transfer Already Settled", HttpStatus.BAD_REQUEST),
  UNBALANCED_JOURNAL_ENTRY("unbalanced_journal_entry", "Unbalanced Journal Entry", HttpStatus.BAD_REQUEST),
  NEGATIVE_BALANCE_NOT_ALLOWED("negative_balance_not_allowed", "Negative Balance Not Allowed", HttpStatus.BAD_REQUEST),
  INVALID_JOURNAL_ENTRY("invalid_journal_entry", "Invalid Journal Entry", HttpStatus.BAD_REQUEST),
  JOURNAL_ENTRY_NOT_FOUND("journal_entry_not_found", "Journal Entry Not Found", HttpStatus.NOT_FOUND),

  // Reconciliation-related errors
  ACCOUNT_RECONCILIATION_BLOCKED("account_reconciliation_blocked", "Account Reconciliation Blocked", HttpStatus.FORBIDDEN),
  TRANSACTION_LIMIT_EXCEEDED("transaction_limit_exceeded", "Transaction Limit Exceeded", HttpStatus.BAD_REQUEST),
  RECONCILIATION_POOL_INSUFFICIENT_FUNDS("reconciliation_pool_insufficient_funds", "Reconciliation Pool Insufficient Funds", HttpStatus.INTERNAL_SERVER_ERROR),
  RECONCILIATION_NOT_FOUND("reconciliation_not_found", "Reconciliation Not Found", HttpStatus.NOT_FOUND),
  RECONCILIATION_IN_PROGRESS("reconciliation_in_progress", "Reconciliation In Progress", HttpStatus.CONFLICT),

  // Customer-related errors
  CUSTOMER_CIN_ALREADY_EXISTS("customer_cin_already_exists", "Customer CIN Already Exists", HttpStatus.CONFLICT),
  CUSTOMER_EMAIL_ALREADY_EXISTS("customer_email_already_exists", "Customer Email Already Exists", HttpStatus.CONFLICT),
  CUSTOMER_NATIONAL_ID_ALREADY_EXISTS("customer_national_id_already_exists", "Customer National ID Already Exists", HttpStatus.CONFLICT),

  // Authentication-related responses
  LOGIN_SUCCESS("login_success", "Login Successful", HttpStatus.OK),
  LOGOUT_SUCCESS("logout_success", "Logout Successful", HttpStatus.OK),
  TOKEN_REFRESHED("token_refreshed", "Token Refreshed Successfully", HttpStatus.OK),
  INVALID_CREDENTIALS("invalid_credentials", "Invalid Credentials", HttpStatus.UNAUTHORIZED),
  ACCOUNT_LOCKED("account_locked", "Account Locked", HttpStatus.FORBIDDEN),
  TOKEN_EXPIRED("token_expired", "Token Expired", HttpStatus.UNAUTHORIZED),
  INVALID_TOKEN("invalid_token", "Invalid Token", HttpStatus.UNAUTHORIZED),
  REFRESH_TOKEN_NOT_FOUND("refresh_token_not_found", "Refresh Token Not Found", HttpStatus.NOT_FOUND),
  REFRESH_TOKEN_EXPIRED("refresh_token_expired", "Refresh Token Expired", HttpStatus.UNAUTHORIZED),
  REFRESH_TOKEN_REVOKED("refresh_token_revoked", "Refresh Token Revoked", HttpStatus.UNAUTHORIZED),
  SESSION_NOT_FOUND("session_not_found", "Session Not Found", HttpStatus.NOT_FOUND),
  USER_NOT_FOUND("user_not_found", "User Not Found", HttpStatus.NOT_FOUND),

  INVALID_PARAM("invalid_param", "Invalid Parameter", HttpStatus.BAD_REQUEST),
  INTERNAL_SERVER_ERROR("internal_server_error", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);

  private final String responseCode;
  private final String responseMessage;
  private final HttpStatus httpStatus;

  ResponseEnum(String responseCode, String responseMessage, HttpStatus httpStatus) {
    this.responseCode = responseCode;
    this.responseMessage = responseMessage;
    this.httpStatus = httpStatus;
  }

}
