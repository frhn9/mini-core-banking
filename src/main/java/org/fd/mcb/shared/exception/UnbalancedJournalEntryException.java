package org.fd.mcb.shared.exception;

import org.fd.mcb.configs.exception.ModuleException;
import org.fd.mcb.shared.response.ResponseEnum;

public class UnbalancedJournalEntryException extends ModuleException {
    public UnbalancedJournalEntryException() {
        super(ResponseEnum.UNBALANCED_JOURNAL_ENTRY);
    }
}
