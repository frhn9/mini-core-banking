package org.fd.mcb.modules.master.model.entity;

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
import org.fd.mcb.modules.master.enums.AccountStatus;
import org.fd.mcb.modules.master.enums.AccountType;

@Entity
@Table(name = "bank_accounts")
@Getter
@Setter
public class BankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private Customer customer;

    @Column(name = "account_number", length = 30, unique = true)
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type")
    private AccountType accountType;

    @Column(name = "balance", precision = 18, scale = 2)
    private BigDecimal balance;

    @Column(name = "available_balance", precision = 18, scale = 2)
    private BigDecimal availableBalance;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AccountStatus status;

    @Column(name = "opened_at")
    private ZonedDateTime openedAt;

    @Column(name = "reconciled_balance", precision = 18, scale = 2)
    private BigDecimal reconciledBalance;

    @Column(name = "pending_correction", precision = 18, scale = 2)
    private BigDecimal pendingCorrection;

    @Column(name = "last_reconciled_at")
    private ZonedDateTime lastReconciledAt;

    @Column(name = "reconciliation_blocked", nullable = false)
    private Boolean reconciliationBlocked = false;

    public void updateAvailableBalance(BigDecimal totalHolds) {
        this.availableBalance = this.balance.subtract(totalHolds);
    }

    public boolean hasPendingCorrection() {
        return pendingCorrection != null && pendingCorrection.compareTo(BigDecimal.ZERO) != 0;
    }

    public BigDecimal getCustomerAvailableBalance() {
        if (pendingCorrection == null) {
            return availableBalance;
        }

        // Conservative: use lower of reconciled vs working balance
        BigDecimal conservative = reconciledBalance != null ?
            reconciledBalance.min(balance) : balance;

        // Subtract safety margin if discrepancy exists
        BigDecimal safetyMargin = pendingCorrection.abs();

        return conservative.subtract(safetyMargin);
    }
}
