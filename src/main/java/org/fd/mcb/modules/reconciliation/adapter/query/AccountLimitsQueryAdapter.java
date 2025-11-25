package org.fd.mcb.modules.reconciliation.adapter.query;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.fd.mcb.modules.master.model.entity.BankAccount;
import org.fd.mcb.modules.reconciliation.model.entity.AccountLimits;

public interface AccountLimitsQueryAdapter {

    Optional<AccountLimits> findByAccount(BankAccount account);

    Optional<AccountLimits> findActiveByAccountId(Long accountId, ZonedDateTime now);

    List<AccountLimits> findExpired(ZonedDateTime now);
}
