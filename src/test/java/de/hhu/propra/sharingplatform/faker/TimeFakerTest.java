package de.hhu.propra.sharingplatform.faker;

import static org.junit.Assert.assertTrue;

import com.github.javafaker.Faker;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Random;
import org.junit.Before;
import org.junit.Test;

public class TimeFakerTest {

    private TimeFaker timeFaker;
    private Faker faker;

    @Before
    public void initDataFaker() {
        long seed = 1337;
        Random rnd = new Random();
        rnd.setSeed(seed);
        faker = new Faker(Locale.ENGLISH, rnd);
        timeFaker = new TimeFaker(faker);
    }

    @Test
    public void rndTime() {
        LocalDateTime time = timeFaker.rndTime();

        LocalDateTime past = LocalDateTime.of(2000, 1, 1, 0, 0, 0);

        assertTrue(time.isAfter(past));
    }

    @Test
    public void rndTimeAfter() {
        LocalDateTime time = timeFaker.rndTime();
        LocalDateTime future = timeFaker.rndTimeAfter(time);

        assertTrue(time.isBefore(future));
    }
}