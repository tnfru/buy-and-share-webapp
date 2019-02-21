package de.hhu.propra.sharingplatform.dao;

import org.springframework.data.repository.CrudRepository;

public interface Conflict extends CrudRepository<Conflict, Long> {

    //List<Conflict> findAllByStatusPending();
}
