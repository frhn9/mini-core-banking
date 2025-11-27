package org.fd.mcb.modules.journalentry.service;

import java.util.List;
import org.fd.mcb.modules.journalentry.dto.request.JournalEntrySearchRequest;
import org.fd.mcb.modules.journalentry.dto.response.JournalEntryDTO;

public interface JournalEntryQueryService {

    List<JournalEntryDTO> searchJournalEntries(JournalEntrySearchRequest request);

    JournalEntryDTO getJournalEntryById(Long id);
}
