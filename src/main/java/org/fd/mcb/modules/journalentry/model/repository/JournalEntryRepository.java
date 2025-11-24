
package org.fd.mcb.modules.journalentry.model.repository;

import java.time.ZonedDateTime;
import java.util.List;
import org.fd.mcb.modules.journalentry.enums.EntryType;
import org.fd.mcb.modules.journalentry.model.entity.JournalEntry;
import org.fd.mcb.modules.master.model.entity.BankAccount;
import org.fd.mcb.modules.master.model.entity.Transaction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JournalEntryRepository extends CrudRepository<JournalEntry, Long> {

    List<JournalEntry> findByBankAccountOrderByCreatedAtDesc(BankAccount bankAccount);

    List<JournalEntry> findByBankAccountAndCreatedAtBetweenOrderByCreatedAtDesc(
            BankAccount bankAccount,
            ZonedDateTime startDate,
            ZonedDateTime endDate);

    List<JournalEntry> findByTransactionOrderByCreatedAtDesc(Transaction transaction);

    List<JournalEntry> findByBankAccountAndEntryTypeOrderByCreatedAtDesc(
            BankAccount bankAccount,
            EntryType entryType);

    @Query("SELECT je FROM JournalEntry je WHERE je.createdAt BETWEEN :startDate AND :endDate ORDER BY je.createdAt DESC")
    List<JournalEntry> findByDateRangeOrderByCreatedAtDesc(
            @Param("startDate") ZonedDateTime startDate,
            @Param("endDate") ZonedDateTime endDate);

    @Query("SELECT je FROM JournalEntry je WHERE je.entryType = :entryType AND je.createdAt BETWEEN :startDate AND :endDate ORDER BY je.createdAt DESC")
    List<JournalEntry> findByEntryTypeAndDateRangeOrderByCreatedAtDesc(
            @Param("entryType") EntryType entryType,
            @Param("startDate") ZonedDateTime startDate,
            @Param("endDate") ZonedDateTime endDate);
}
