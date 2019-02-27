package de.hhu.propra.sharingplatform.dao;

import static org.junit.Assert.assertEquals;

import de.hhu.propra.sharingplatform.dto.Status;
import de.hhu.propra.sharingplatform.model.Conflict;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

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
        Conflict punishedBail = new Conflict();
        punishedBail.setStatus(Status.PUNISHED_BAIL);

        repo.save(pending1);
        repo.save(pending2);
        repo.save(punishedBail);

        List<Conflict> conflicts = repo.findAllByStatus(Status.PENDING);

        for (Conflict conflict : conflicts) {
            assertEquals(Status.PENDING, conflict.getStatus());
        }
        assertEquals(2, conflicts.size());
    }

    @Test
    public void statusRejected() {
        Conflict pending1 = new Conflict();
        pending1.setStatus(Status.PENDING);
        Conflict pending2 = new Conflict();
        pending2.setStatus(Status.PENDING);
        Conflict punishedBail = new Conflict();
        punishedBail.setStatus(Status.PUNISHED_BAIL);

        repo.save(pending1);
        repo.save(pending2);
        repo.save(punishedBail);

        List<Conflict> conflicts = repo.findAllByStatus(Status.PUNISHED_BAIL);

        for (Conflict conflict : conflicts) {
            assertEquals(Status.PUNISHED_BAIL, conflict.getStatus());
        }
        assertEquals(1, conflicts.size());
    }
}