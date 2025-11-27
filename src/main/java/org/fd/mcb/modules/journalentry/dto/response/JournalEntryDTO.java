package org.fd.mcb.modules.journalentry.dto.response;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import lombok.Getter;
import lombok.Setter;
import org.fd.mcb.modules.journalentry.enums.EntryType;

@Getter
@Setter
public class JournalEntryDTO {

    private Long id;
    private Long transactionId;
    private String accountNumber;
    private String accountType;
    private EntryType entryType;
    private BigDecimal amount;
    private ZonedDateTime createdAt;
}
