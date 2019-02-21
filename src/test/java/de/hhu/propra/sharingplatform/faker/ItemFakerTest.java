package de.hhu.propra.sharingplatform.faker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import com.github.javafaker.Faker;
import de.hhu.propra.sharingplatform.model.Item;
import de.hhu.propra.sharingplatform.model.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ItemFakerTest {

    private ItemFaker itemFaker;
    private UserFaker userFaker;
    private Faker faker;

    @Before
    public void initDataFaker() {
        long seed = 1337;
        Random rnd = new Random();
        rnd.setSeed(seed);
        faker = new Faker(Locale.ENGLISH, rnd);
        itemFaker = new ItemFaker(faker);
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

        Item item = itemFaker.create(user);

        String pattern = "^[a-zA-Z0-9?!',\\. ]*$";

        assertEquals(user, item.getOwner());
        assertTrue(item.getName().matches(pattern));
        assertTrue(item.getDescription().matches(pattern));
        assertTrue(item.getBail() < 9999 && 0 < item.getBail());
        assertTrue(item.getPrice() < 200 && 0 < item.getPrice());
        assertFalse(item.isDeleted());
        assertTrue(item.getLocation().matches(pattern));
        assertNotEquals(null, item.getOffers());
    }

    @Test
    public void createZeroItemsTest() {
        User user = userFaker.create();

        List<Item> items = new ArrayList<>();

        itemFaker.createItems(items, user, 0);

        assertEquals(0, items.size());
    }

    @Test
    public void createItemsTest() {
        User user1 = userFaker.create();
        User user2 = userFaker.create();

        List<Item> items1 = new ArrayList<>();
        List<Item> items2 = new ArrayList<>();

        itemFaker.createItems(items1, user1, 3);
        itemFaker.createItems(items2, user2, 19);

        assertEquals(3, items1.size());
        for (Item item : items1) {
            assertEquals(user1, item.getOwner());
        }

        assertEquals(19, items2.size());
        for (Item item : items2) {
            assertEquals(user2, item.getOwner());
        }
    }

}
