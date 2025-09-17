package org.fd.mcb.modules.transaction.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DepositRequest {

    private BigDecimal amount;

}
