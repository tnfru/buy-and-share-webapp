package de.hhu.propra.sharingplatform.dao;

import de.hhu.propra.sharingplatform.dto.Status;
import de.hhu.propra.sharingplatform.model.Conflict;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConflictRepo extends CrudRepository<Conflict, Long> {

    @Query("SELECT c FROM Conflict as c WHERE c.status = :status")
    List<Conflict> findAllByStatus(@Param("status") Status status);
}
