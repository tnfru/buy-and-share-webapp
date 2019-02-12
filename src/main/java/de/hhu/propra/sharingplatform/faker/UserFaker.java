package de.hhu.propra.sharingplatform.faker;

import com.github.javafaker.Faker;
import de.hhu.propra.sharingplatform.model.Item;
import de.hhu.propra.sharingplatform.model.User;
import java.util.ArrayList;

public class UserFaker implements IFaker<User> {

    private Faker faker;

    public UserFaker(Faker faker) {
        this.faker = faker;
    }

    public User create() {
        User user = new User();
        user.setName(faker.name().fullName());
        user.setAdress(faker.address().fullAddress());
        user.setEmail(faker.name().username() + "@example.com");
        user.setPropayId(faker.number().numberBetween(1337, 9999));
        user.setRating(faker.number().numberBetween(0, 5));
        user.setBan(false);
        user.setDeleted(false);
        if (user.getItems() != null) {
            user.setItems(new ArrayList<>());
        }
        return user;
    }

    public User addToList(IFaker<Item> itemFaker, User owner, int count) {
        for (int i = 0; i < count; i++) {
            owner.getItems().add(itemFaker.create());
        }
        return owner;
    }

    public User addToList(IFaker<Contract> contractFaker, User owner, int count) {
        for (int i = 0; i < count; i++) {
            owner.getItems().add(contractIFaker.create());
        }
        return owner;
    }

    public User addToList(IFaker<Offer> offferFaker, User owner, int count) {
        for (int i = 0; i < count; i++) {
            owner.getItems().add(offferFaker.create());
        }
        return owner;
    }
}
