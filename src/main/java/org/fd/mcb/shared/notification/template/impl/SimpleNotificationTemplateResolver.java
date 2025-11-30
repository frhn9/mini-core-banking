package org.fd.mcb.shared.notification.template.impl;

import lombok.extern.slf4j.Slf4j;
import org.fd.mcb.shared.notification.enums.NotificationEvent;
import org.fd.mcb.shared.notification.template.NotificationTemplateResolver;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Simple template resolver that uses inline HTML and text templates.
 * Templates use {variableName} syntax for variable substitution.
 *
 * For production use, consider:
 * - Loading templates from external files or database
 * - Using a proper template engine like Thymeleaf or Freemarker
 * - Supporting multiple languages/locales
 */
@Slf4j
@Component
public class SimpleNotificationTemplateResolver implements NotificationTemplateResolver {

    @Override
    public String resolveEmailTemplate(NotificationEvent event, Map<String, Object> variables) {
        String template = getEmailTemplate(event);
        return populateTemplate(template, variables);
    }

    @Override
    public String resolveSmsTemplate(NotificationEvent event, Map<String, Object> variables) {
        String template = getSmsTemplate(event);
        return populateTemplate(template, variables);
    }

    @Override
    public String resolvePushTemplate(NotificationEvent event, Map<String, Object> variables) {
        String template = getPushTemplate(event);
        return populateTemplate(template, variables);
    }

    private String getEmailTemplate(NotificationEvent event) {
        return switch (event) {
            case DEPOSIT_SUCCESS -> """
                    <!DOCTYPE html>
                    <html>
                    <body style="font-family: Arial, sans-serif; padding: 20px;">
                        <h2 style="color: #28a745;">Deposit Successful</h2>
                        <p>Dear {customerName},</p>
                        <p>Your deposit has been processed successfully.</p>
                        <table style="border-collapse: collapse; margin: 20px 0;">
                            <tr><td style="padding: 8px;"><strong>Account Number:</strong></td><td style="padding: 8px;">{accountNumber}</td></tr>
                            <tr><td style="padding: 8px;"><strong>Amount:</strong></td><td style="padding: 8px;">Rp {amount}</td></tr>
                            <tr><td style="padding: 8px;"><strong>Transaction ID:</strong></td><td style="padding: 8px;">{transactionId}</td></tr>
                            <tr><td style="padding: 8px;"><strong>New Balance:</strong></td><td style="padding: 8px;">Rp {balance}</td></tr>
                            <tr><td style="padding: 8px;"><strong>Date:</strong></td><td style="padding: 8px;">{timestamp}</td></tr>
                        </table>
                        <p>Thank you for banking with us.</p>
                        <hr style="margin: 20px 0;">
                        <p style="font-size: 12px; color: #666;">This is an automated notification. Please do not reply to this email.</p>
                    </body>
                    </html>
                    """;

            case WITHDRAWAL_SUCCESS -> """
                    <!DOCTYPE html>
                    <html>
                    <body style="font-family: Arial, sans-serif; padding: 20px;">
                        <h2 style="color: #dc3545;">Withdrawal Successful</h2>
                        <p>Dear {customerName},</p>
                        <p>Your withdrawal has been processed successfully.</p>
                        <table style="border-collapse: collapse; margin: 20px 0;">
                            <tr><td style="padding: 8px;"><strong>Account Number:</strong></td><td style="padding: 8px;">{accountNumber}</td></tr>
                            <tr><td style="padding: 8px;"><strong>Amount:</strong></td><td style="padding: 8px;">Rp {amount}</td></tr>
                            <tr><td style="padding: 8px;"><strong>Transaction ID:</strong></td><td style="padding: 8px;">{transactionId}</td></tr>
                            <tr><td style="padding: 8px;"><strong>New Balance:</strong></td><td style="padding: 8px;">Rp {balance}</td></tr>
                            <tr><td style="padding: 8px;"><strong>Date:</strong></td><td style="padding: 8px;">{timestamp}</td></tr>
                        </table>
                        <p>Thank you for banking with us.</p>
                        <hr style="margin: 20px 0;">
                        <p style="font-size: 12px; color: #666;">This is an automated notification. Please do not reply to this email.</p>
                    </body>
                    </html>
                    """;

            case TRANSFER_AUTHORIZED -> """
                    <!DOCTYPE html>
                    <html>
                    <body style="font-family: Arial, sans-serif; padding: 20px;">
                        <h2 style="color: #007bff;">Transfer Authorized</h2>
                        <p>Dear {customerName},</p>
                        <p>Your transfer has been authorized. The funds are on hold pending completion.</p>
                        <table style="border-collapse: collapse; margin: 20px 0;">
                            <tr><td style="padding: 8px;"><strong>From Account:</strong></td><td style="padding: 8px;">{sourceAccountNumber}</td></tr>
                            <tr><td style="padding: 8px;"><strong>To Account:</strong></td><td style="padding: 8px;">{destinationAccountNumber}</td></tr>
                            <tr><td style="padding: 8px;"><strong>Amount:</strong></td><td style="padding: 8px;">Rp {amount}</td></tr>
                            <tr><td style="padding: 8px;"><strong>Authorization Code:</strong></td><td style="padding: 8px;">{authCode}</td></tr>
                            <tr><td style="padding: 8px;"><strong>Expires At:</strong></td><td style="padding: 8px;">{expiresAt}</td></tr>
                            <tr><td style="padding: 8px;"><strong>Available Balance:</strong></td><td style="padding: 8px;">Rp {availableBalance}</td></tr>
                        </table>
                        <p>Use the authorization code to complete the transfer.</p>
                        <hr style="margin: 20px 0;">
                        <p style="font-size: 12px; color: #666;">This is an automated notification. Please do not reply to this email.</p>
                    </body>
                    </html>
                    """;

            case TRANSFER_CAPTURED -> """
                    <!DOCTYPE html>
                    <html>
                    <body style="font-family: Arial, sans-serif; padding: 20px;">
                        <h2 style="color: #ffc107;">Transfer Captured</h2>
                        <p>Dear {customerName},</p>
                        <p>The transfer has been captured and funds have been debited from your account.</p>
                        <table style="border-collapse: collapse; margin: 20px 0;">
                            <tr><td style="padding: 8px;"><strong>From Account:</strong></td><td style="padding: 8px;">{sourceAccountNumber}</td></tr>
                            <tr><td style="padding: 8px;"><strong>To Account:</strong></td><td style="padding: 8px;">{destinationAccountNumber}</td></tr>
                            <tr><td style="padding: 8px;"><strong>Amount:</strong></td><td style="padding: 8px;">Rp {amount}</td></tr>
                            <tr><td style="padding: 8px;"><strong>Transaction ID:</strong></td><td style="padding: 8px;">{transactionId}</td></tr>
                            <tr><td style="padding: 8px;"><strong>New Balance:</strong></td><td style="padding: 8px;">Rp {balance}</td></tr>
                        </table>
                        <p>The transfer will be completed shortly when funds are credited to the destination account.</p>
                        <hr style="margin: 20px 0;">
                        <p style="font-size: 12px; color: #666;">This is an automated notification. Please do not reply to this email.</p>
                    </body>
                    </html>
                    """;

            case TRANSFER_SETTLED -> """
                    <!DOCTYPE html>
                    <html>
                    <body style="font-family: Arial, sans-serif; padding: 20px;">
                        <h2 style="color: #28a745;">Transfer Completed</h2>
                        <p>Dear {customerName},</p>
                        <p>Your transfer has been completed successfully.</p>
                        <table style="border-collapse: collapse; margin: 20px 0;">
                            <tr><td style="padding: 8px;"><strong>From Account:</strong></td><td style="padding: 8px;">{sourceAccountNumber}</td></tr>
                            <tr><td style="padding: 8px;"><strong>To Account:</strong></td><td style="padding: 8px;">{destinationAccountNumber}</td></tr>
                            <tr><td style="padding: 8px;"><strong>Amount:</strong></td><td style="padding: 8px;">Rp {amount}</td></tr>
                            <tr><td style="padding: 8px;"><strong>Transaction ID:</strong></td><td style="padding: 8px;">{transactionId}</td></tr>
                            <tr><td style="padding: 8px;"><strong>Date:</strong></td><td style="padding: 8px;">{timestamp}</td></tr>
                        </table>
                        <p>Thank you for banking with us.</p>
                        <hr style="margin: 20px 0;">
                        <p style="font-size: 12px; color: #666;">This is an automated notification. Please do not reply to this email.</p>
                    </body>
                    </html>
                    """;

            case TRANSFER_CANCELLED -> """
                    <!DOCTYPE html>
                    <html>
                    <body style="font-family: Arial, sans-serif; padding: 20px;">
                        <h2 style="color: #6c757d;">Transfer Cancelled</h2>
                        <p>Dear {customerName},</p>
                        <p>Your transfer has been cancelled and any holds have been released.</p>
                        <table style="border-collapse: collapse; margin: 20px 0;">
                            <tr><td style="padding: 8px;"><strong>Authorization Code:</strong></td><td style="padding: 8px;">{authCode}</td></tr>
                            <tr><td style="padding: 8px;"><strong>Amount:</strong></td><td style="padding: 8px;">Rp {amount}</td></tr>
                            <tr><td style="padding: 8px;"><strong>Available Balance:</strong></td><td style="padding: 8px;">Rp {availableBalance}</td></tr>
                        </table>
                        <hr style="margin: 20px 0;">
                        <p style="font-size: 12px; color: #666;">This is an automated notification. Please do not reply to this email.</p>
                    </body>
                    </html>
                    """;

            default -> "<p>Event: " + event.getSubject() + "</p>";
        };
    }

    private String getSmsTemplate(NotificationEvent event) {
        return switch (event) {
            case DEPOSIT_SUCCESS ->
                    "Deposit successful. Account: {accountNumber}, Amount: Rp{amount}, New Balance: Rp{balance}. Ref: {transactionId}";
            case WITHDRAWAL_SUCCESS ->
                    "Withdrawal successful. Account: {accountNumber}, Amount: Rp{amount}, New Balance: Rp{balance}. Ref: {transactionId}";
            case TRANSFER_AUTHORIZED ->
                    "Transfer authorized. From: {sourceAccountNumber} to {destinationAccountNumber}, Amount: Rp{amount}. Auth Code: {authCode}. Expires: {expiresAt}";
            case TRANSFER_CAPTURED ->
                    "Transfer captured. Amount: Rp{amount} debited from {sourceAccountNumber}. Ref: {transactionId}";
            case TRANSFER_SETTLED ->
                    "Transfer completed. Amount: Rp{amount} from {sourceAccountNumber} to {destinationAccountNumber}. Ref: {transactionId}";
            case TRANSFER_CANCELLED ->
                    "Transfer cancelled. Auth Code: {authCode}. Funds released.";
            default -> event.getSubject();
        };
    }

    private String getPushTemplate(NotificationEvent event) {
        return switch (event) {
            case DEPOSIT_SUCCESS -> "Deposit of Rp{amount} successful. New balance: Rp{balance}";
            case WITHDRAWAL_SUCCESS -> "Withdrawal of Rp{amount} successful. New balance: Rp{balance}";
            case TRANSFER_AUTHORIZED -> "Transfer of Rp{amount} authorized. Auth code: {authCode}";
            case TRANSFER_CAPTURED -> "Transfer of Rp{amount} captured from your account";
            case TRANSFER_SETTLED -> "Transfer of Rp{amount} completed successfully";
            case TRANSFER_CANCELLED -> "Transfer cancelled. Funds released.";
            default -> event.getSubject();
        };
    }

    private String populateTemplate(String template, Map<String, Object> variables) {
        String result = template;
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            String placeholder = "{" + entry.getKey() + "}";
            String value = entry.getValue() != null ? entry.getValue().toString() : "";
            result = result.replace(placeholder, value);
        }
        return result;
    }
}
