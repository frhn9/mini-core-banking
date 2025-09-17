package org.fd.mcb.modules.journalentry.adapter.command;

import org.fd.mcb.modules.journalentry.dto.context.JournalEntryContext;
import org.fd.mcb.modules.journalentry.model.entity.JournalEntry;

public interface JournalEntryCommandAdapter {

    JournalEntry save(JournalEntryContext journalEntryContext);

}
