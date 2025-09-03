
package org.fd.mcb.modules.master.model.repository;

import org.fd.mcb.modules.master.model.entity.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, Long> {
}
