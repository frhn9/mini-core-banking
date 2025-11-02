package org.fd.mcb.modules.master.model.repository;

import org.fd.mcb.modules.master.enums.HoldStatus;
import org.fd.mcb.modules.master.model.entity.AccountHold;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountHoldRepository extends CrudRepository<AccountHold, Long> {

    List<AccountHold> findByAccountIdAndStatus(Long accountId, HoldStatus status);

    Optional<AccountHold> findByTransactionId(Long transactionId);

    @Query("SELECT COALESCE(SUM(ah.amount), 0) FROM AccountHold ah WHERE ah.account.id = :accountId AND ah.status = :status")
    BigDecimal sumAmountByAccountIdAndStatus(@Param("accountId") Long accountId, @Param("status") HoldStatus status);

    List<AccountHold> findByStatusAndExpiresAtBefore(HoldStatus status, ZonedDateTime expiresAt);
}
