package org.fd.mcb.modules.reconciliation.model.repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.fd.mcb.modules.master.model.entity.BankAccount;
import org.fd.mcb.modules.reconciliation.enums.AccountTier;
import org.fd.mcb.modules.reconciliation.model.entity.AccountLimits;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountLimitsRepository extends CrudRepository<AccountLimits, Long> {

    Optional<AccountLimits> findByAccount(BankAccount account);

    @Query("SELECT al FROM AccountLimits al WHERE al.account.id = :accountId AND (al.expiresAt IS NULL OR al.expiresAt > :now) ORDER BY al.appliedAt DESC LIMIT 1")
    Optional<AccountLimits> findActiveByAccountId(
            @Param("accountId") Long accountId,
            @Param("now") ZonedDateTime now);

    List<AccountLimits> findByTier(AccountTier tier);

    @Query("SELECT al FROM AccountLimits al WHERE al.expiresAt IS NOT NULL AND al.expiresAt <= :now")
    List<AccountLimits> findExpired(@Param("now") ZonedDateTime now);

    void deleteByAccount(BankAccount account);
}
