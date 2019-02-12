package de.hhu.propra.sharingplatform.faker;

import com.github.javafaker.Faker;
import de.hhu.propra.sharingplatform.model.Item;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.modelDAO.ItemRepo;
import de.hhu.propra.sharingplatform.modelDAO.UserRepo;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import javax.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class DataFaker implements ServletContextInitializer {

    @Autowired
    private Environment env;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ItemRepo itemRepo;

    private Faker faker;

    public DataFaker() {
        Random rnd = new Random();
        rnd.setSeed(1337);
        this.faker = new Faker(Locale.ENGLISH, rnd);
    }

    public DataFaker(long seed) {
        Random rnd = new Random();
        rnd.setSeed(seed);
        this.faker = new Faker(Locale.ENGLISH, rnd);
    }

    @Override
    public void onStartup(ServletContext servletContext) {
        System.out.println("Generating Database");
        UserFaker userFaker = new UserFaker(faker);
        ItemFaker itemFaker = new ItemFaker(faker);

        System.out.println("    Creating User...");
        List<User> users = new ArrayList<>();
        userFaker.createUsers(users, 10);

        System.out.println("    Creating Items...");
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            User user = getRandomUser(users);
            itemFaker.createItems(items, user, 2);
        }

        persistUser(users);
        persistItem(items);

        System.out.println("Done!");
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
}
