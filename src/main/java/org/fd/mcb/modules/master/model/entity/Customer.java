package org.fd.mcb.modules.master.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import lombok.Getter;
import lombok.Setter;
import org.fd.mcb.modules.master.enums.BlockedByBankReason;
import org.fd.mcb.modules.master.enums.CustomerStatus;

@Entity
@Table(name = "customers")
@Getter
@Setter
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "cin", length = 50, unique = true, nullable = false)
    private String cin;

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(name = "national_id", length = 20)
    private String nationalId;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "address")
    private String address;

    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    @Column(name = "deleted_at")
    private ZonedDateTime deletedAt;

    @Column(name = "is_blocked_by_bank")
    private Boolean isBlockedByBank;

    @Enumerated(EnumType.STRING)
    @Column(name = "blocked_by_bank_reason")
    private BlockedByBankReason blockedByBankReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private CustomerStatus status;

    @Column(name = "account_balance_derived", precision = 18, scale = 2)
    private BigDecimal accountBalanceDerived;

}
