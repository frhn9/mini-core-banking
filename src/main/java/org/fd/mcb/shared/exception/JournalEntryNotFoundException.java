package org.fd.mcb.shared.exception;

import org.fd.mcb.configs.exception.ModuleException;
import org.fd.mcb.shared.response.ResponseEnum;

public class JournalEntryNotFoundException extends ModuleException {
    public JournalEntryNotFoundException() {
        super(ResponseEnum.JOURNAL_ENTRY_NOT_FOUND);
    }
}
