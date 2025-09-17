package org.fd.mcb.modules.transaction.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class DepositResponse {
    private Long transactionId;
    private BigDecimal currentBalance;
}
