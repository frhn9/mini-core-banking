package org.fd.mcb.modules.journalentry.dto.context;

import lombok.Builder;
import lombok.Data;
import org.fd.mcb.modules.journalentry.enums.EntryType;
import org.fd.mcb.modules.master.model.entity.BankAccount;
import org.fd.mcb.modules.master.model.entity.Transaction;

import java.math.BigDecimal;

@Data
@Builder
public class JournalEntryContext {

    private Transaction transaction;

    private BankAccount bankAccount;

    private BigDecimal amount;

    private EntryType entryType;

}
