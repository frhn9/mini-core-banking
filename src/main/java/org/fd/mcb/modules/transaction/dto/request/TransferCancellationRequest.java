package org.fd.mcb.modules.transaction.dto.request;

import lombok.Data;

@Data
public class TransferCancellationRequest {
    private String authCode;
}
