package org.fd.mcb.modules.journalentry.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReversalRequest {

    @NotNull(message = "Transaction ID is required")
    private Long transactionId;

    @NotBlank(message = "Reversal reason is required")
    private String reason;

    private String staffUsername;
}
