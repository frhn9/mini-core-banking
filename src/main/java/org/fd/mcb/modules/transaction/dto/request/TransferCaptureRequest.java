package org.fd.mcb.modules.transaction.dto.request;

import lombok.Data;

@Data
public class TransferCaptureRequest {
    private String authCode;
}
