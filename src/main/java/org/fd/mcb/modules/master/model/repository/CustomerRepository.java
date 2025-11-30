
package org.fd.mcb.modules.master.model.repository;

import org.fd.mcb.modules.master.model.entity.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, Long> {
  boolean existsByCin(String cin);
  boolean existsByEmail(String email);
  boolean existsByNationalId(String nationalId);
  Optional<Customer> findByCin(String cin);
}
