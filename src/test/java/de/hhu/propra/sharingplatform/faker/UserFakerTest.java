package de.hhu.propra.sharingplatform.faker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import com.github.javafaker.Faker;
import de.hhu.propra.sharingplatform.model.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import org.junit.Before;
import org.junit.Test;


public class UserFakerTest {

    private UserFaker userFaker;

    @Before
    public void initDataFaker() {
        long seed = 1337;
        Random rnd = new Random();
        rnd.setSeed(seed);
        Faker faker = new Faker(Locale.ENGLISH, rnd);
        userFaker = new UserFaker(faker);
    }

    @Test
    public void createUserTest() {
        User user = userFaker.create();

        String pattern = "^[a-zA-Z0-9?!',\\. ]*$";

        assertTrue(user.getName().matches(pattern));
        assertTrue(user.getAddress().matches(pattern));
        assertTrue(user.getEmail().contains("@"));
        assertTrue(user.getEmail().replace("@", "!?").matches(pattern));
        assertTrue(0 < user.getPositiveRating() && 0 < user.getNegativeRating());
        assertFalse(user.isBan());
        assertFalse(user.isDeleted());
        assertNotEquals(null, user.getItemRentals());
        assertNotEquals(null, user.getContracts());
        assertNotEquals(null, user.getOffers());
    }

    @Test
    public void createZeroUser() {

        List<User> user = new ArrayList<>();

        userFaker.createUsers(user, 0);

        assertEquals(0, user.size());
    }


    @Test
    public void createItemsTest() {
        List<User> user1 = new ArrayList<>();
        List<User> user2 = new ArrayList<>();

        userFaker.createUsers(user1, 5);
        userFaker.createUsers(user2, 13);

        assertEquals(5, user1.size());
        assertEquals(13, user2.size());
    }
}
