package de.hhu.propra.sharingplatform.model;


import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UserTest {

    private User user = new User();

    @Test
    public void getRatingNoRatings() {
        user.setNegativeRating(0);
        user.setPositiveRating(0);

        assertEquals("0.0%", user.getRating());
    }

    @Test
    public void getRatingOnlyPositive() {
        user.setPositiveRating(42);
        user.setNegativeRating(0);

        assertEquals("100.0%", user.getRating());
    }

    @Test
    public void getRatingOnlyNegative() {
        user.setPositiveRating(0);
        user.setNegativeRating(233);

        assertEquals("0.0%", user.getRating());
    }

    @Test
    public void getRatingRandomDistribution() {
        user.setPositiveRating(501);
        user.setNegativeRating(398);

        assertEquals("55.7%", user.getRating());
    }
}