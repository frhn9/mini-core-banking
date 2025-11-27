package org.fd.mcb.modules.journalentry.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fd.mcb.modules.journalentry.adapter.query.JournalEntryQueryAdapter;
import org.fd.mcb.modules.journalentry.dto.request.JournalEntrySearchRequest;
import org.fd.mcb.modules.journalentry.dto.response.JournalEntryDTO;
import org.fd.mcb.modules.journalentry.mapper.JournalEntryMapper;
import org.fd.mcb.modules.journalentry.model.entity.JournalEntry;
import org.fd.mcb.modules.journalentry.service.JournalEntryQueryService;
import org.fd.mcb.modules.master.adapter.query.BankAccountQueryAdapter;
import org.fd.mcb.modules.master.enums.AccountType;
import org.fd.mcb.modules.master.model.entity.BankAccount;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class JournalEntryQueryServiceImpl implements JournalEntryQueryService {

    private final JournalEntryQueryAdapter journalEntryQueryAdapter;
    private final BankAccountQueryAdapter bankAccountQueryAdapter;
    private final JournalEntryMapper journalEntryMapper;

    @Override
    @Transactional(readOnly = true)
    public List<JournalEntryDTO> searchJournalEntries(JournalEntrySearchRequest request) {
        log.info("Searching journal entries with criteria: {}", request);

        List<JournalEntry> journalEntries;

        if (request.getAccountNumber() != null) {
            BankAccount bankAccount = bankAccountQueryAdapter.findByAccountNumber(
                    request.getAccountNumber(), AccountType.SAVINGS);

            if (request.getStartDate() != null && request.getEndDate() != null) {
                // Search by account and date range
                journalEntries = journalEntryQueryAdapter.findByBankAccountAndDateRange(
                        bankAccount, request.getStartDate(), request.getEndDate());
            } else if (request.getEntryType() != null) {
                // Search by account and entry type
                journalEntries = journalEntryQueryAdapter.findByBankAccountAndEntryType(
                        bankAccount, request.getEntryType());
            } else {
                // Search by account only
                journalEntries = journalEntryQueryAdapter.findByBankAccount(bankAccount);
            }
        } else if (request.getStartDate() != null && request.getEndDate() != null) {
            if (request.getEntryType() != null) {
                // Search by date range and entry type
                journalEntries = journalEntryQueryAdapter.findByEntryTypeAndDateRange(
                        request.getEntryType(), request.getStartDate(), request.getEndDate());
            } else {
                // Search by date range only
                journalEntries = journalEntryQueryAdapter.findByDateRange(
                        request.getStartDate(), request.getEndDate());
            }
        } else {
            log.warn("No search criteria provided, returning empty list");
            return List.of();
        }

        return journalEntryMapper.toDTOList(journalEntries);
    }

    @Override
    @Transactional(readOnly = true)
    public JournalEntryDTO getJournalEntryById(Long id) {
        log.info("Fetching journal entry by ID: {}", id);
        JournalEntry journalEntry = journalEntryQueryAdapter.findById(id);
        return journalEntryMapper.toDTO(journalEntry);
    }
}
