package org.fd.mcb.modules.transaction.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TransferResponse {
    private Long transactionId;
    private String referenceNumber;
    private String message;
}
