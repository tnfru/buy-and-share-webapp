package de.hhu.propra.sharingplatform.faker;

import com.github.javafaker.Faker;
import de.hhu.propra.sharingplatform.dao.ItemRepo;
import de.hhu.propra.sharingplatform.dao.UserRepo;
import de.hhu.propra.sharingplatform.model.Item;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.service.OfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletContext;
import java.util.*;
import java.util.logging.Logger;

@Component
public class DataFaker implements ServletContextInitializer {

    private final Environment env;

    private final UserRepo userRepo;

    private final ItemRepo itemRepo;

    private final OfferService offerService;

    private Logger log = Logger.getLogger(DataFaker.class.getName());

    private Faker faker;

    @Autowired
    public DataFaker(Environment env, UserRepo userRepo, ItemRepo itemRepo,
                     OfferService offerService) {
        this.env = env;
        this.userRepo = userRepo;
        this.itemRepo = itemRepo;
        this.offerService = offerService;
        Random rnd = new Random();
        rnd.setSeed(1337);
        this.faker = new Faker(Locale.ENGLISH, rnd);
    }

    public DataFaker(long seed, Environment env, UserRepo userRepo, ItemRepo itemRepo,
                     OfferService offerService) {
        this.env = env;
        this.userRepo = userRepo;
        this.itemRepo = itemRepo;
        this.offerService = offerService;
        Random rnd = new Random();
        rnd.setSeed(seed);
        this.faker = new Faker(Locale.ENGLISH, rnd);
    }

    @Override
    @Transactional
    public void onStartup(ServletContext servletContext) {
        log.info("Generating Database");
        UserFaker userFaker = new UserFaker(faker);
        ItemFaker itemFaker = new ItemFaker(faker);

        log.info("    Creating User...");
        List<User> users = new ArrayList<>();
        userFaker.createUsers(users, 15);

        log.info("    Creating Items...");
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            User user = getRandomUser(users);
            itemFaker.createItems(items, user, 4);
        }

        log.info("    Persist Items...");
        persistItem(items);
        log.info("    Persist Users...");
        persistUser(users);

        log.info("    Creating Offers...");
        for (int i = 0; i < 10; i++) {
            User user = getRandomUser(users);
            Item item = getRandomItem(items);

            if (item.getOwner().getId() != user.getId()) {
                offerService.create(item.getId(), user,
                    new Date(29, 2, 30),
                    new Date(30, 3, 10));
            } else {
                i--;
            }
        }

        log.info("Done!");
    }

    private void persistUser(List<User> users) {
        for (User user : users) {
            userRepo.save(user);
        }
    }

    private void persistItem(List<Item> items) {
        for (Item item : items) {
            itemRepo.save(item);
        }
    }

    private User getRandomUser(List<User> users) {
        return users.get(faker.number().numberBetween(0, users.size()));
    }

    private Item getRandomItem(List<Item> items) {
        return items.get(faker.number().numberBetween(0, items.size()));
    }
}
