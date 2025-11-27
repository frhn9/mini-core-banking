package org.fd.mcb.configs;

import java.math.BigDecimal;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "reconciliation")
@Data
public class ReconciliationConfigProperties {

    private Pool pool = new Pool();
    private SeverityThresholds severityThresholds = new SeverityThresholds();
    private TierLimits tierLimits = new TierLimits();
    private boolean autoCorrectEnabled = true;
    private Notifications notifications = new Notifications();

    @Data
    public static class Pool {
        private String accountNumber = "RECONCILIATION-POOL";
        private BigDecimal lowBalanceThreshold = new BigDecimal("10000.00");
    }

    @Data
    public static class SeverityThresholds {
        private BigDecimal low = new BigDecimal("10.00");
        private BigDecimal medium = new BigDecimal("100.00");
        private BigDecimal high = new BigDecimal("1000.00");
    }

    @Data
    public static class TierLimits {
        private TierConfig tier2 = new TierConfig();
        private TierConfig tier3 = new TierConfig();
        private TierConfig tier4 = new TierConfig();
    }

    @Data
    public static class TierConfig {
        private BigDecimal maxWithdrawal;
        private BigDecimal maxTransferOut;
        private BigDecimal dailyLimit;
    }

    @Data
    public static class Notifications {
        private String operationsEmail = "ops@bank.com";
    }
}
