package org.fd.mcb.shared.notification.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.fd.mcb.modules.master.model.entity.BankAccount;
import org.fd.mcb.modules.master.model.entity.Customer;
import org.fd.mcb.modules.master.model.entity.Transaction;
import org.fd.mcb.modules.master.model.repository.TransactionRepository;
import org.fd.mcb.shared.notification.annotation.Notify;
import org.fd.mcb.shared.notification.dto.NotificationData;
import org.fd.mcb.shared.notification.dto.NotificationRecipient;
import org.fd.mcb.shared.notification.enums.NotificationChannel;
import org.fd.mcb.shared.notification.service.NotificationService;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * AOP Aspect that intercepts methods annotated with @Notify
 * and sends notifications after successful execution.
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class NotificationAspect {

    private final NotificationService notificationService;
    private final TransactionRepository transactionRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");

    /**
     * Intercepts methods annotated with @Notify after successful execution.
     * Extracts notification data from the response and sends notifications
     * through configured channels.
     */
    @AfterReturning(
            pointcut = "@annotation(notify)",
            returning = "result"
    )
    public void sendNotification(JoinPoint joinPoint, Notify notify, Object result) {
        try {
            log.debug("Processing notification for event: {}", notify.event());

            // Extract transaction ID from response
            Long transactionId = extractTransactionId(result);
            if (transactionId == null) {
                log.warn("Could not extract transaction ID from result: {}", result.getClass().getSimpleName());
                return;
            }

            // Fetch transaction details
            Transaction transaction = transactionRepository.findById(transactionId).orElse(null);
            if (transaction == null) {
                log.warn("Transaction not found with ID: {}", transactionId);
                return;
            }

            // Determine which account's customer should receive the notification
            BankAccount notificationAccount = determineNotificationAccount(transaction, notify.event());
            if (notificationAccount == null || notificationAccount.getCustomer() == null) {
                log.warn("No customer found for notification");
                return;
            }

            // Build notification data
            NotificationData notificationData = buildNotificationData(
                    notify,
                    transaction,
                    notificationAccount,
                    result
            );

            // Send notification
            Set<NotificationChannel> channels = Arrays.stream(notify.channels())
                    .collect(Collectors.toSet());

            if (notify.async()) {
                notificationService.sendAsync(notificationData, channels);
            } else {
                notificationService.send(notificationData, channels);
            }

            log.info("Notification queued for event: {} via channels: {}", notify.event(), channels);

        } catch (Exception e) {
            // Don't throw exceptions - notification failures should not break business logic
            log.error("Failed to send notification: {}", e.getMessage(), e);
        }
    }

    /**
     * Extracts transaction ID from various response types using reflection.
     */
    private Long extractTransactionId(Object result) {
        if (result == null) {
            return null;
        }

        try {
            // Try to get transactionId field using reflection
            var method = result.getClass().getMethod("getTransactionId");
            Object transactionId = method.invoke(result);
            return transactionId instanceof Long ? (Long) transactionId : null;
        } catch (Exception e) {
            log.debug("Could not extract transactionId from {}: {}", result.getClass().getSimpleName(), e.getMessage());
            return null;
        }
    }

    /**
     * Determines which account's customer should receive the notification.
     * For deposits/withdrawals: source account customer
     * For transfers: depends on the event type
     */
    private BankAccount determineNotificationAccount(Transaction transaction, org.fd.mcb.shared.notification.enums.NotificationEvent event) {
        return switch (event) {
            case DEPOSIT_SUCCESS, WITHDRAWAL_SUCCESS, TRANSFER_AUTHORIZED, TRANSFER_CAPTURED, TRANSFER_CANCELLED ->
                    transaction.getSourceAccount();
            case TRANSFER_SETTLED ->
                    transaction.getDestinationAccount();  // Notify recipient
            default -> transaction.getSourceAccount();
        };
    }

    /**
     * Builds NotificationData from transaction details.
     */
    private NotificationData buildNotificationData(
            Notify notify,
            Transaction transaction,
            BankAccount account,
            Object result
    ) {
        Customer customer = account.getCustomer();

        // Build recipient
        NotificationRecipient recipient = NotificationRecipient.builder()
                .recipientId(customer.getId())
                .name(customer.getFullName())
                .email(customer.getEmail())
                .phoneNumber(customer.getPhoneNumber())
                .locale("en")  // TODO: Get from customer preferences
                .build();

        // Build template variables
        Map<String, Object> variables = new HashMap<>();
        variables.put("customerName", customer.getFullName());
        variables.put("accountNumber", account.getAccountNumber());
        variables.put("transactionId", transaction.getId());
        variables.put("amount", transaction.getAmount());
        variables.put("balance", account.getBalance());
        variables.put("availableBalance", account.getAvailableBalance());
        variables.put("timestamp", formatDateTime(transaction.getCreatedAt()));

        // Add transfer-specific variables
        if (transaction.getAuthCode() != null) {
            variables.put("authCode", transaction.getAuthCode());
        }
        if (transaction.getExpiresAt() != null) {
            variables.put("expiresAt", formatDateTime(transaction.getExpiresAt()));
        }
        if (transaction.getDestinationAccount() != null) {
            variables.put("destinationAccountNumber", transaction.getDestinationAccount().getAccountNumber());
        }
        if (transaction.getSourceAccount() != null) {
            variables.put("sourceAccountNumber", transaction.getSourceAccount().getAccountNumber());
        }

        // Build notification data
        return NotificationData.builder()
                .event(notify.event())
                .recipient(recipient)
                .subject(notify.event().getSubject())
                .variables(variables)
                .build();
    }

    private String formatDateTime(ZonedDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_FORMATTER) : "";
    }
}
