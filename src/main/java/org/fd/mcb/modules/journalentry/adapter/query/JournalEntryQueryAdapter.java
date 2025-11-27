package org.fd.mcb.modules.journalentry.adapter.query;

import java.time.ZonedDateTime;
import java.util.List;
import org.fd.mcb.modules.journalentry.enums.EntryType;
import org.fd.mcb.modules.journalentry.model.entity.JournalEntry;
import org.fd.mcb.modules.master.model.entity.BankAccount;
import org.fd.mcb.modules.master.model.entity.Transaction;

public interface JournalEntryQueryAdapter {

    JournalEntry findById(Long id);

    List<JournalEntry> findByBankAccount(BankAccount bankAccount);

    List<JournalEntry> findByBankAccountAndDateRange(
            BankAccount bankAccount,
            ZonedDateTime startDate,
            ZonedDateTime endDate);

    List<JournalEntry> findByTransaction(Transaction transaction);

    List<JournalEntry> findByBankAccountAndEntryType(
            BankAccount bankAccount,
            EntryType entryType);

    List<JournalEntry> findByDateRange(
            ZonedDateTime startDate,
            ZonedDateTime endDate);

    List<JournalEntry> findByEntryTypeAndDateRange(
            EntryType entryType,
            ZonedDateTime startDate,
            ZonedDateTime endDate);
}
