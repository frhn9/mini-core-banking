package org.fd.mcb.modules.journalentry.dto.response;

import java.time.ZonedDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ManualJournalEntryResponse {

    private Long transactionId;
    private String reason;
    private List<JournalEntryDTO> journalEntries;
    private ZonedDateTime createdAt;
    private String status;
}
