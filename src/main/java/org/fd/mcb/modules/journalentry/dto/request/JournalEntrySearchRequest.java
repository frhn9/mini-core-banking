package org.fd.mcb.modules.journalentry.dto.request;

import java.time.ZonedDateTime;
import lombok.Getter;
import lombok.Setter;
import org.fd.mcb.modules.journalentry.enums.EntryType;

@Getter
@Setter
public class JournalEntrySearchRequest {

    private String accountNumber;
    private EntryType entryType;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
}
