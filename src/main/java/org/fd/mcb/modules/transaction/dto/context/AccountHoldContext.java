package org.fd.mcb.modules.transaction.dto.context;

import lombok.Builder;
import lombok.Data;
import org.fd.mcb.modules.master.enums.HoldStatus;
import org.fd.mcb.modules.master.enums.HoldType;
import org.fd.mcb.modules.master.model.entity.BankAccount;
import org.fd.mcb.modules.master.model.entity.Transaction;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@Builder
public class AccountHoldContext {

    private BankAccount account;

    private Transaction transaction;

    private HoldType holdType;

    private BigDecimal amount;

    private HoldStatus status;

    private ZonedDateTime expiresAt;
}
