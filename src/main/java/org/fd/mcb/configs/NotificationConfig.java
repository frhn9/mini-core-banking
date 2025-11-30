package org.fd.mcb.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Configuration for the notification system.
 * Enables AOP for @Notify annotation processing.
 */
@Configuration
@EnableAspectJAutoProxy
public class NotificationConfig {
    // Notification components are auto-configured via @Component annotations
    // This class primarily enables AspectJ auto-proxying for the @Notify annotation
}
