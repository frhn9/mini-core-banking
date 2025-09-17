package org.fd.mcb.modules.journalentry.adapter.command.impl;

import lombok.RequiredArgsConstructor;
import org.fd.mcb.modules.journalentry.adapter.command.JournalEntryCommandAdapter;
import org.fd.mcb.modules.journalentry.dto.context.JournalEntryContext;
import org.fd.mcb.modules.journalentry.mapper.JournalEntryMapper;
import org.fd.mcb.modules.journalentry.model.entity.JournalEntry;
import org.fd.mcb.modules.journalentry.model.repository.JournalEntryRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JournalEntryCommandAdapterImpl implements JournalEntryCommandAdapter {

    private final JournalEntryMapper journalEntryMapper;

    private final JournalEntryRepository journalEntryRepository;

    @Override
    public JournalEntry save(JournalEntryContext journalEntryContext) {
        return journalEntryRepository.save(journalEntryMapper.toJournalEntry(journalEntryContext));
    }
}
