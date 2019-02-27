package de.hhu.propra.sharingplatform.faker;

import com.github.javafaker.Faker;
import java.time.LocalDateTime;

class TimeFaker {

    private Faker faker;

    public TimeFaker(Faker faker) {
        this.faker = faker;
    }

    public LocalDateTime rndTime() {
        int year = faker.number().numberBetween(2001, 2030);
        int month = faker.number().numberBetween(1, 12);
        int day = faker.number().numberBetween(1, 28);
        int hours = faker.number().numberBetween(0, 23);
        int minutes = faker.number().numberBetween(0, 60);
        int seconds = faker.number().numberBetween(0, 60);

        return LocalDateTime.of(year, month, day, hours, minutes, seconds);
    }

    public LocalDateTime rndTimeAfter(LocalDateTime time) {
        LocalDateTime future;
        future = time.plusDays(faker.number().numberBetween(1, 20));
        future = future.plusHours(faker.number().numberBetween(0, 50));
        future = future.plusMinutes(faker.number().numberBetween(0, 1337));

        return future;
    }
}
