
package org.fd.mcb.modules.master.model.repository;

import org.fd.mcb.modules.master.model.entity.PaymentType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentTypeRepository extends CrudRepository<PaymentType, Integer> {
}
