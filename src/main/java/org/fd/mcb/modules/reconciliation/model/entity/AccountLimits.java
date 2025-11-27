package org.fd.mcb.modules.reconciliation.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import lombok.Getter;
import lombok.Setter;
import org.fd.mcb.modules.master.model.entity.BankAccount;
import org.fd.mcb.modules.reconciliation.enums.AccountTier;

@Entity
@Table(name = "account_limits")
@Getter
@Setter
public class AccountLimits {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private BankAccount account;

    @Enumerated(EnumType.STRING)
    @Column(name = "tier", length = 50, nullable = false)
    private AccountTier tier;

    @Column(name = "max_withdrawal", precision = 18, scale = 2)
    private BigDecimal maxWithdrawal;

    @Column(name = "max_transfer_out", precision = 18, scale = 2)
    private BigDecimal maxTransferOut;

    @Column(name = "daily_limit", precision = 18, scale = 2)
    private BigDecimal dailyLimit;

    @Column(name = "reason", length = 500)
    private String reason;

    @Column(name = "applied_at", nullable = false)
    private ZonedDateTime appliedAt;

    @Column(name = "expires_at")
    private ZonedDateTime expiresAt;
}
