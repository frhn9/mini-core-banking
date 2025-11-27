package org.fd.mcb.modules.journalentry.adapter.query.impl;

import java.time.ZonedDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.fd.mcb.modules.journalentry.adapter.query.JournalEntryQueryAdapter;
import org.fd.mcb.modules.journalentry.enums.EntryType;
import org.fd.mcb.modules.journalentry.model.entity.JournalEntry;
import org.fd.mcb.modules.journalentry.model.repository.JournalEntryRepository;
import org.fd.mcb.modules.master.model.entity.BankAccount;
import org.fd.mcb.modules.master.model.entity.Transaction;
import org.fd.mcb.shared.exception.JournalEntryNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JournalEntryQueryAdapterImpl implements JournalEntryQueryAdapter {

    private final JournalEntryRepository journalEntryRepository;

    @Override
    public JournalEntry findById(Long id) {
        return journalEntryRepository.findById(id)
                .orElseThrow(JournalEntryNotFoundException::new);
    }

    @Override
    public List<JournalEntry> findByBankAccount(BankAccount bankAccount) {
        return journalEntryRepository.findByBankAccountOrderByCreatedAtDesc(bankAccount);
    }

    @Override
    public List<JournalEntry> findByBankAccountAndDateRange(
            BankAccount bankAccount,
            ZonedDateTime startDate,
            ZonedDateTime endDate) {
        return journalEntryRepository.findByBankAccountAndCreatedAtBetweenOrderByCreatedAtDesc(
                bankAccount, startDate, endDate);
    }

    @Override
    public List<JournalEntry> findByTransaction(Transaction transaction) {
        return journalEntryRepository.findByTransactionOrderByCreatedAtDesc(transaction);
    }

    @Override
    public List<JournalEntry> findByBankAccountAndEntryType(
            BankAccount bankAccount,
            EntryType entryType) {
        return journalEntryRepository.findByBankAccountAndEntryTypeOrderByCreatedAtDesc(
                bankAccount, entryType);
    }

    @Override
    public List<JournalEntry> findByDateRange(
            ZonedDateTime startDate,
            ZonedDateTime endDate) {
        return journalEntryRepository.findByDateRangeOrderByCreatedAtDesc(startDate, endDate);
    }

    @Override
    public List<JournalEntry> findByEntryTypeAndDateRange(
            EntryType entryType,
            ZonedDateTime startDate,
            ZonedDateTime endDate) {
        return journalEntryRepository.findByEntryTypeAndDateRangeOrderByCreatedAtDesc(
                entryType, startDate, endDate);
    }
}
