package de.hhu.propra.sharingplatform.faker;

import com.github.javafaker.Faker;
import de.hhu.propra.sharingplatform.model.User;

import java.util.List;

class UserFaker {

    private Faker faker;

    UserFaker(Faker faker) {
        this.faker = faker;
    }

    private User createUserRole(String role) {
        User user = new User();
        user.setName(faker.name().fullName());
        user.setAddress(faker.address().fullAddress());
        String username = faker.name().username();
        user.setAccountName(username);
        user.setEmail(username + "@example.com");
        user.setPropayId(faker.name().lastName());
        user.setRole(role);
        user.setPositiveRating(faker.number().numberBetween(0, 40));
        user.setNegativeRating(faker.number().numberBetween(0, 20));
        user.setPassword("123");
        //user.setPasswordHash("$2a$10$k3tiDNBzPrEZpem.kabN8u2L5u3jutsoEehrojVB/BgwpRMxNBy..");
        user.setBan(false);
        user.setDeleted(false);

        return user;
    }

    public User create() {
        return createUserRole("user");
    }

    void createUsers(List<User> users, int count) {
        for (int i = 0; i < count; i++) {
            users.add(create());
        }
    }

    public User createAdmin() {
        User admin =  createUserRole("admin");
        admin.setAccountName("admin");
        return admin;
    }
}
