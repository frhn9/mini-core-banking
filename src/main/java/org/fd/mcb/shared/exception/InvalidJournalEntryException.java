package org.fd.mcb.shared.exception;

import org.fd.mcb.configs.exception.ModuleException;
import org.fd.mcb.shared.response.ResponseEnum;

public class InvalidJournalEntryException extends ModuleException {
    public InvalidJournalEntryException() {
        super(ResponseEnum.INVALID_JOURNAL_ENTRY);
    }
}
