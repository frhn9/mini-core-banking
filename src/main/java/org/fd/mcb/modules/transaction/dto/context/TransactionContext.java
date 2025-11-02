package org.fd.mcb.modules.transaction.dto.context;

import lombok.Builder;
import lombok.Data;
import org.fd.mcb.modules.master.enums.TransactionStatus;
import org.fd.mcb.modules.master.model.entity.BankAccount;
import org.fd.mcb.modules.master.model.entity.PaymentType;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@Builder
public class TransactionContext {

    private BigDecimal amount;

    private String channel;

    private BankAccount sourceAccount;

    private BankAccount destinationAccount;

    private PaymentType paymentType;

    private TransactionStatus status;

    private String authCode;

    private ZonedDateTime expiresAt;

}
