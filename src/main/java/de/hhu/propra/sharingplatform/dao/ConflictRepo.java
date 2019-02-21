package de.hhu.propra.sharingplatform.dao;

import de.hhu.propra.sharingplatform.model.Conflict;
import org.springframework.data.repository.CrudRepository;

public interface ConflictRepo extends CrudRepository<Conflict, Long> {

    //List<ConflictRepo> findAllByStatusPending();
}
