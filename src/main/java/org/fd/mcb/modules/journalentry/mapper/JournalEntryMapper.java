package org.fd.mcb.modules.journalentry.mapper;

import org.fd.mcb.modules.journalentry.dto.context.JournalEntryContext;
import org.fd.mcb.modules.journalentry.enums.EntryType;
import org.fd.mcb.modules.journalentry.model.entity.JournalEntry;
import org.fd.mcb.modules.master.model.entity.BankAccount;
import org.fd.mcb.modules.master.model.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface JournalEntryMapper {

    JournalEntry toJournalEntry(JournalEntryContext journalEntryContext);

}
