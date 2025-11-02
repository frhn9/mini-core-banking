package org.fd.mcb.modules.transaction.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransferAuthRequest {

    private String sourceAccountNumber;

    private String destAccountNumber;

    private BigDecimal amount;

    private String channel;

}
