
package org.fd.mcb.modules.master.model.repository;

import org.fd.mcb.modules.master.model.entity.ChargeTransaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChargeTransactionRepository extends CrudRepository<ChargeTransaction, Long> {
}
