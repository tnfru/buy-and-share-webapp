package de.hhu.propra.sharingplatform.faker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import com.github.javafaker.Faker;
import de.hhu.propra.sharingplatform.model.ItemRental;
import de.hhu.propra.sharingplatform.model.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ItemRentalFakerTest {

    private ItemRentalFaker itemRentalFaker;
    private UserFaker userFaker;
    private Faker faker;

    @Before
    public void initDataFaker() {
        long seed = 1337;
        Random rnd = new Random();
        rnd.setSeed(seed);
        faker = new Faker(Locale.ENGLISH, rnd);
        itemRentalFaker = new ItemRentalFaker(faker);
        userFaker = new UserFaker(faker);
    }

    @Test
    public void seedConsistentOutputTest() {
        Assert.assertEquals("Octavia", faker.name().firstName());
        Assert.assertEquals("58262", faker.number().digits(5));
        Assert.assertEquals("Dicta ex laudantium in quidem sed.", faker.lorem().paragraph(1));
    }

    @Test
    public void createItemTest() {
        User user = userFaker.create();

        ItemRental itemRental = itemRentalFaker.create(user);

        String pattern = "^[a-zA-Z0-9?!',\\. ]*$";

        assertEquals(user, itemRental.getOwner());
        assertTrue(itemRental.getName().matches(pattern));
        assertTrue(itemRental.getDescription().matches(pattern));
        assertTrue(itemRental.getBail() < 9999 && 0 < itemRental.getBail());
        assertTrue(itemRental.getDailyRate() < 200 && 0 < itemRental.getDailyRate());
        assertFalse(itemRental.isDeleted());
        assertTrue(itemRental.getLocation().matches(pattern));
        assertNotEquals(null, itemRental.getOffers());
    }

    @Test
    public void createZeroItemsTest() {
        User user = userFaker.create();

        List<ItemRental> itemRentals = new ArrayList<>();

        itemRentalFaker.createItems(itemRentals, user, 0);

        assertEquals(0, itemRentals.size());
    }

    @Test
    public void createItemsTest() {
        User user1 = userFaker.create();
        User user2 = userFaker.create();

        List<ItemRental> items1 = new ArrayList<>();
        List<ItemRental> items2 = new ArrayList<>();

        itemRentalFaker.createItems(items1, user1, 3);
        itemRentalFaker.createItems(items2, user2, 19);

        assertEquals(3, items1.size());
        for (ItemRental itemRental : items1) {
            assertEquals(user1, itemRental.getOwner());
        }

        assertEquals(19, items2.size());
        for (ItemRental itemRental : items2) {
            assertEquals(user2, itemRental.getOwner());
        }
    }

}
