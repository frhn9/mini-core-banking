package org.fd.mcb.modules.journalentry.mapper;

import java.math.BigDecimal;
import java.util.List;
import org.fd.mcb.modules.journalentry.dto.context.JournalEntryContext;
import org.fd.mcb.modules.journalentry.dto.response.JournalEntryDTO;
import org.fd.mcb.modules.journalentry.enums.EntryType;
import org.fd.mcb.modules.journalentry.model.entity.JournalEntry;
import org.fd.mcb.modules.master.model.entity.BankAccount;
import org.fd.mcb.modules.master.model.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring")
public interface JournalEntryMapper {

    JournalEntry toJournalEntry(JournalEntryContext journalEntryContext);

    @Mapping(source = "transaction.id", target = "transactionId")
    @Mapping(source = "bankAccount.accountNumber", target = "accountNumber")
    @Mapping(source = "bankAccount.accountType", target = "accountType")
    JournalEntryDTO toDTO(JournalEntry journalEntry);

    List<JournalEntryDTO> toDTOList(List<JournalEntry> journalEntries);
}
