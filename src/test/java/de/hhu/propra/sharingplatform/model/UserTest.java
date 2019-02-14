package de.hhu.propra.sharingplatform.model;

import static org.junit.Assert.*;

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
}