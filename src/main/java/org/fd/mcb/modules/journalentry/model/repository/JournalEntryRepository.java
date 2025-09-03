
package org.fd.mcb.modules.journalentry.model.repository;

import org.fd.mcb.modules.journalentry.model.entity.JournalEntry;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JournalEntryRepository extends CrudRepository<JournalEntry, Long> {
}
