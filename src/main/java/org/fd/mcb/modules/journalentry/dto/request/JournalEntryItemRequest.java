package org.fd.mcb.modules.journalentry.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import org.fd.mcb.modules.journalentry.enums.EntryType;

@Getter
@Setter
public class JournalEntryItemRequest {

    @NotBlank(message = "Account number is required")
    private String accountNumber;

    @NotNull(message = "Entry type is required")
    private EntryType entryType;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
}
