package de.hhu.propra.sharingplatform.dao;

import de.hhu.propra.sharingplatform.dto.Status;
import de.hhu.propra.sharingplatform.model.Conflict;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ConflictRepoTest {

    @Autowired
    ConflictRepo repo;

    @Test
    public void statusPending() {
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

        for (Conflict conflict : conflicts) {
            assertEquals(conflict.getStatus(), Status.PENDING);
        }
        assertEquals(conflicts.size(), 2);
    }

    @Test
    public void statusRejected() {
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

        for (Conflict conflict : conflicts) {
            assertEquals(conflict.getStatus(), Status.REJECTED);
        }
        assertEquals(conflicts.size(), 1);
    }
}