
package org.fd.mcb.modules.master.model.repository;

import jakarta.persistence.LockModeType;
import org.fd.mcb.modules.master.model.entity.Transaction;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Long> {

    Optional<Transaction> findByAuthCode(String authCode);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Transaction> findByAuthCodeAndStatusIn(String authCode, org.fd.mcb.modules.master.enums.TransactionStatus... statuses);
}
