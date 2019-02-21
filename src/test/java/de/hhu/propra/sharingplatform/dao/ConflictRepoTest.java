package de.hhu.propra.sharingplatform.dao;

import de.hhu.propra.sharingplatform.dto.Status;
import de.hhu.propra.sharingplatform.model.Conflict;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

public class ConflictRepoTest {

    @Autowired
    ConflictRepo repo;

    @Test
    public void statusPending(){
        Conflict pending1 = new Conflict();
        pending1.setStatus(Status.PENDING);
        Conflict pending2 = new Conflict();
        pending2.setStatus(Status.PENDING);
        Conflict rejected = new Conflict();
        rejected.setStatus(Status.REJECTED);

        repo.save(pending1);
        repo.save(pending2);
        repo.save(rejected);

        List<Conflict> conflicts = repo.findAllByStatus(Status.PENDING);

        for (Conflict conflict: conflicts) {
            assert conflict.getStatus().equals(Status.PENDING);
        }
        assert conflicts.size() == 2;
    }

    @Test
    public void statusRejected(){
        Conflict pending1 = new Conflict();
        pending1.setStatus(Status.PENDING);
        Conflict pending2 = new Conflict();
        pending2.setStatus(Status.PENDING);
        Conflict rejected = new Conflict();
        rejected.setStatus(Status.REJECTED);

        repo.save(pending1);
        repo.save(pending2);
        repo.save(rejected);

        List<Conflict> conflicts = repo.findAllByStatus(Status.REJECTED);

        for (Conflict conflict: conflicts) {
            assert conflict.getStatus().equals(Status.REJECTED);
        }
        assert conflicts.size() == 1;
    }
}