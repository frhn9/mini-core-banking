package org.fd.mcb.modules.transaction.dto.response;

import lombok.Builder;
import lombok.Data;
import org.fd.mcb.modules.master.enums.TransactionStatus;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@Builder
public class TransferCaptureResponse {
    private Long transactionId;
    private String authCode;
    private TransactionStatus status;
    private BigDecimal amount;
    private String sourceAccountNumber;
    private String destinationAccountNumber;
    private ZonedDateTime capturedAt;
}
