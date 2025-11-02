package org.fd.mcb.modules.transaction.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.fd.mcb.modules.master.enums.TransactionStatus;

import java.time.ZonedDateTime;

@Getter
@Setter
@Builder
public class TransferAuthResponse {

    private String authCode;

    private Long transactionId;

    private TransactionStatus status;

    private ZonedDateTime expiresAt;

}
