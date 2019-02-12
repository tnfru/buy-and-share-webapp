package de.hhu.propra.sharingplatform.faker;

import com.github.javafaker.Faker;
import de.hhu.propra.sharingplatform.model.User;
import java.util.ArrayList;
import java.util.List;

public class UserFaker {

    private Faker faker;

    public UserFaker(Faker faker) {
        this.faker = faker;
    }

    public User create() {
        User user = new User();
        user.setName(faker.name().fullName());
        user.setAddress(faker.address().fullAddress());
        user.setEmail(faker.name().username() + "@example.com");
        user.setPropayId(faker.number().numberBetween(123456789, 987654321));
        user.setRating(faker.number().numberBetween(0, 5));
        user.setBan(false);
        user.setDeleted(false);
        if (user.getItems() == null) {
            user.setItems(new ArrayList<>());
        }
        return user;
    }

    public void createUsers(List<User> users, int count){
        for (int i = 0; i < count; i++) {
            users.add(create());
        }
    }
}
