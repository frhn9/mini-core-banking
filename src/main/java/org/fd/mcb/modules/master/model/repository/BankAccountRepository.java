
package org.fd.mcb.modules.master.model.repository;

import jakarta.persistence.LockModeType;
import org.fd.mcb.modules.master.enums.AccountType;
import org.fd.mcb.modules.master.model.entity.BankAccount;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankAccountRepository extends CrudRepository<BankAccount, Long> {

    @Lock(LockModeType.PESSIMISTIC_READ)
    Optional<BankAccount> findByAccountNumberAndAccountType(String accountNumber, AccountType accountType);

}
