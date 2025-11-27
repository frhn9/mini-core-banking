package org.fd.mcb.modules.journalentry.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ManualJournalEntryRequest {

    @NotEmpty(message = "Journal entries list cannot be empty")
    @Size(min = 2, message = "At least 2 journal entries are required for double-entry bookkeeping")
    @Valid
    private List<JournalEntryItemRequest> entries;

    @NotBlank(message = "Reason is required")
    private String reason;

    private String staffUsername;
}
