package de.hhu.propra.sharingplatform.model;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class UserTest {

    @Test
    public void checkCorrectPassword() {
        User user = new User();
        user.setPassword("testpw");

        assertTrue(user.checkPassword("testpw"));
    }

    @Test
    public void checkWrongPassword() {
        User user = new User();
        user.setPassword("testpw");
        assertFalse(user.checkPassword("123"));
    }

    @Test
    public void checkCorrectPasswordWithSpecialChars() {
        User user = new User();
        user.setPassword("@²³{[]}~öä");

        assertTrue(user.checkPassword("@²³{[]}~öä"));
    }

    @Test
    public void checkPasswordHashNotPassword() {
        User user = new User();
        user.setPassword("@²³{[]}~öä");

        assertNotEquals(user.getPasswordHash(), "@²³{[]}~öä");
    }

    //TODO: Start some spring magic to inject the pepper...
    //@Test
    public void checkSaltPepperExists() {
        User user = new User();
        user.setPassword("123");

        assertNotNull(user.getPepper());
        assertNotNull(user.getSalt());
        assert user.getPepper().length() > 0;
        assert user.getSalt().length() > 0;
    }

    @Test
    public void checkSaltChangesAfterResetPassword() {
        User user = new User();
        user.setPassword("123");
        String firstSalt = user.getSalt();

        user.setPassword("222");

        assertNotEquals(user.getSalt().length(), firstSalt);
    }
}