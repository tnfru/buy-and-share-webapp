package de.hhu.propra.sharingplatform.dao;

import org.springframework.data.repository.CrudRepository;

public interface ConflictRepo extends CrudRepository<ConflictRepo, Long> {

    //List<ConflictRepo> findAllByStatusPending();
}
