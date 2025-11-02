package org.fd.mcb.modules.master.adapter.query;

import org.fd.mcb.modules.master.model.entity.AccountHold;

import java.math.BigDecimal;
import java.util.List;

public interface AccountHoldQueryAdapter {

    List<AccountHold> findActiveHoldsByAccountId(Long accountId);

    AccountHold findByTransactionId(Long transactionId);

    BigDecimal calculateTotalActiveHolds(Long accountId);

    List<AccountHold> findExpiredHolds();

}
