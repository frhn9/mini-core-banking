package org.fd.mcb.modules.master.adapter.command;

import org.fd.mcb.modules.master.model.entity.AccountHold;
import org.fd.mcb.modules.transaction.dto.context.AccountHoldContext;

public interface AccountHoldCommandAdapter {

    AccountHold save(AccountHoldContext context);

    void releaseHold(Long holdId);

}
