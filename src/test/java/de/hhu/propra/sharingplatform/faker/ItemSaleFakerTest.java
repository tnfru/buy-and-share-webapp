package de.hhu.propra.sharingplatform.faker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import com.github.javafaker.Faker;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.model.items.ItemSale;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ItemSaleFakerTest {

    private ItemSaleFaker itemSaleFaker;
    private UserFaker userFaker;
    private Faker faker;

    @Before
    public void initDataFaker() {
        long seed = 1337;
        Random rnd = new Random();
        rnd.setSeed(seed);
        faker = new Faker(Locale.ENGLISH, rnd);
        itemSaleFaker = new ItemSaleFaker(faker);
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

        ItemSale itemSale = itemSaleFaker.create(user);

        String pattern = "^[a-zA-Z0-9?!',\\. ]*$";

        assertEquals(user, itemSale.getOwner());
        assertTrue(itemSale.getName().matches(pattern));
        assertTrue(itemSale.getDescription().matches(pattern));
        assertTrue(itemSale.getPrice() < 9999 && 0 < itemSale.getPrice());
        assertFalse(itemSale.isDeleted());
        assertTrue(itemSale.getLocation().matches(pattern));
        assertNotEquals(null, itemSale.getContracts());
    }

    @Test
    public void createZeroItemsTest() {
        User user = userFaker.create();

        List<ItemSale> itemRentals = new ArrayList<>();

        itemSaleFaker.createItems(itemRentals, user, 0);

        assertEquals(0, itemRentals.size());
    }

    @Test
    public void createItemsTest() {
        User user1 = userFaker.create();
        User user2 = userFaker.create();

        List<ItemSale> items1 = new ArrayList<>();
        List<ItemSale> items2 = new ArrayList<>();

        itemSaleFaker.createItems(items1, user1, 3);
        itemSaleFaker.createItems(items2, user2, 19);

        assertEquals(3, items1.size());
        for (ItemSale itemSale : items1) {
            assertEquals(user1, itemSale.getOwner());
        }

        assertEquals(19, items2.size());
        for (ItemSale itemSale : items2) {
            assertEquals(user2, itemSale.getOwner());
        }
    }

}
