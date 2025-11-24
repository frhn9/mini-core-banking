package org.fd.mcb.modules.journalentry.service;

import org.fd.mcb.modules.journalentry.dto.request.ManualJournalEntryRequest;
import org.fd.mcb.modules.journalentry.dto.request.ReversalRequest;
import org.fd.mcb.modules.journalentry.dto.response.ManualJournalEntryResponse;

public interface ManualJournalEntryService {

    ManualJournalEntryResponse createManualEntry(ManualJournalEntryRequest request);

    ManualJournalEntryResponse reverseManualEntry(ReversalRequest request);
}
