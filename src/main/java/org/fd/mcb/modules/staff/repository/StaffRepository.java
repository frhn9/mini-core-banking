
package org.fd.mcb.modules.staff.repository;

import java.util.UUID;
import org.fd.mcb.modules.staff.model.entity.Staff;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffRepository extends CrudRepository<Staff, UUID> {
}
