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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
@Import({ItemFaker.class, DataFaker.class, Faker.class})
public class ItemFakerTest {

    private ItemFaker itemFaker;
    private UserFaker userFaker;

    @Before
    public void initDataFaker() {
        long seed = 1337;
        Random rnd = new Random();
        rnd.setSeed(seed);
        Faker faker = new Faker(Locale.ENGLISH, rnd);
        itemFaker = new ItemFaker(faker);
        userFaker = new UserFaker(faker);
    }

    @Test
    public void createItemTest() {
        User user = userFaker.create();

        Item item = itemFaker.create(user);
        System.out.println(item);

        assertEquals(user, item.getOwner());
        assertEquals("Enterprise", item.getName());
        assertEquals("Qui maxime qui. Nobis vel veniam iure numquam in.", item.getDescription());
        assertEquals(50, item.getDeposit());
        assertEquals(30, item.getPrice());
        assertTrue(item.isAvailable());
        assertFalse(item.isDeleted());
        assertEquals("Lake Kiarra", item.getLocation());
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
