
package org.fd.mcb.modules.journalentry.repository;

import java.util.UUID;
import org.fd.mcb.modules.journalentry.model.entity.JournalEntry;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JournalEntryRepository extends CrudRepository<JournalEntry, UUID> {
}
