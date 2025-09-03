
package org.fd.mcb.modules.master.repository;

import java.util.UUID;
import org.fd.mcb.modules.master.model.entity.PaymentDetail;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentDetailRepository extends CrudRepository<PaymentDetail, UUID> {
}
