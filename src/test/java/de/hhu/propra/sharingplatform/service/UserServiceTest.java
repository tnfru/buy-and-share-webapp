package de.hhu.propra.sharingplatform.service;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hhu.propra.sharingplatform.dao.UserRepo;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.service.payment.IBankAccountService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ResponseStatusException;

@RunWith(SpringRunner.class)
public class UserServiceTest {

    @MockBean
    private UserRepo userRepo;

    @MockBean
    private IBankAccountService bankAccountService;

    @MockBean
    private PasswordEncoder encoder;

    @MockBean
    private ImageService imageSaver;

    private UserService userService;

    public User createUser(String name, String accountName, String address, String email) {
        User user = new User();
        user.setName(name);
        user.setAccountName(accountName);
        user.setAddress(address);
        user.setEmail(email);
        return user;
    }

    @Before
    public void setUp() {
        userService = new UserService(userRepo, encoder, bankAccountService, imageSaver);
    }

    @Test
    public void persistUserTest() {
        User user = createUser("name", "accountname", "addresse", "e@mail.de");
        ArgumentCaptor<User> argument = ArgumentCaptor.forClass(User.class);

        userService.persistUser(user, "foo", "foo");

        verify(userRepo, times(2)).save(argument.capture());
        assertEquals(user, argument.getValue());
    }

    @Test
    public void updateUserTest() {
        User oldUser = createUser("typ", "hobo", "foo", "e@mail.de");
        User newUser = createUser("typo", "hobo", "bar", "e@mail.de");
        ArgumentCaptor<User> argument = ArgumentCaptor.forClass(User.class);
        userService.updateUser(oldUser, newUser);
        verify(userRepo, times(1)).save(argument.capture());
        assertEquals(newUser, oldUser);
    }

    @Test
    public void updatePasswordTest() {
        ArgumentCaptor<User> argument = ArgumentCaptor.forClass(User.class);
        User user = createUser("name", "dude", "wo", "e@mai.de");
        String password = "123";
        String newPassword = "321";
        String confirm = "321";

        userService.persistUser(user, password, "123");

        when(encoder.matches(anyString(), anyString())).thenReturn(true);

        userService.updatePassword(user, "123", newPassword, confirm);

        verify(userRepo, times(3)).save(argument.capture());

    }

    @Test
    public void updatePasswordIncorrectPassword() {
        boolean thrown = false;
        User user = createUser("name", "accName", "address", "e@mail.de");
        try {
            userService.updatePassword(user,"wrongOld", "new", "new");
            when(encoder.matches(anyString(), anyString())).thenReturn(false);
        } catch (ResponseStatusException rse) {
            thrown = true;
            assertEquals("400 BAD_REQUEST \"Incorrect Password\"",
                rse.getMessage());
        }
        verify(userRepo, times(0)).save(any());
        assertTrue(thrown);
    }


    @Test
    public void fetchUserByAccountNameUserNotFound() {
        boolean thrown = false;
        try {
            userService.fetchUserByAccountName("accName");
        } catch (ResponseStatusException rse) {
            thrown = true;
            assertEquals("500 INTERNAL_SERVER_ERROR \"Something went wrong.\"",
                rse.getMessage());
        }
        verify(userRepo, times(0)).save(any());
        assertTrue(thrown);
    }

    @Test
    public void fetchUserIdByAccountNameUserNotFound() {
        boolean thrown = false;
        try {
            userService.fetchUserIdByAccountName("accName");
        } catch (ResponseStatusException rse) {
            thrown = true;
            assertEquals("500 INTERNAL_SERVER_ERROR \"Something went wrong.\"",
                rse.getMessage());
        }
        verify(userRepo, times(0)).save(any());
        assertTrue(thrown);
    }

    @Test
    public void fetchUserByIdUserNotFound() {
        boolean thrown = false;
        try {
            userService.fetchUserById((long) 1);
        } catch (ResponseStatusException rse) {
            thrown = true;
            assertEquals("500 INTERNAL_SERVER_ERROR \"Something went wrong.\"",
                rse.getMessage());
        }
        verify(userRepo, times(0)).save(any());
        assertTrue(thrown);
    }

    @Test
    public void generatePasswordPasswordNotEqual() {
        boolean thrown = false;
        User user = createUser("name", "accName", "address", "e@mail.de");
        try {
            userService.persistUser(user, "password", "passwort");
        } catch (ResponseStatusException rse) {
            thrown = true;
            assertEquals("400 BAD_REQUEST \"Passwords need to be the same.\"",
                rse.getMessage());
        }
        verify(userRepo, times(0)).save(any());
        assertTrue(thrown);
    }

}