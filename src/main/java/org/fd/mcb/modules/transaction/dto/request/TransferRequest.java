package org.fd.mcb.modules.transaction.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransferRequest {

    private String fromAccountNumber;

    private String toAccountNumber;

    private BigDecimal amount;

    private String description;

}
