package de.hhu.propra.sharingplatform.service;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hhu.propra.sharingplatform.dao.UserRepo;
import de.hhu.propra.sharingplatform.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class UserServiceTest {

    @MockBean
    private UserRepo userRepo;

    @MockBean
    private PasswordEncoder encoder;

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
        userService = new UserService(userRepo, encoder);
    }

    @Test
    public void persistUser() {
        User user = createUser("name", "accountname", "addresse", "e@mail.de");
        ArgumentCaptor<User> argument = ArgumentCaptor.forClass(User.class);

        userService.persistUser(user, "foo", "foo");

        verify(userRepo, times(1)).save(argument.capture());
        assertEquals(user, argument.getValue());
    }

    @Test
    public void loginUsingSpring() {
        //ToDo ?
    }

    @Test
    public void updateUser() {
        User oldUser = createUser("typ", "hobo", "foo", "e@mail.de");
        User newUser = createUser("typo", "hobo", "bar", "e@mail.de");
        ArgumentCaptor<User> argument = ArgumentCaptor.forClass(User.class);
        userService.updateUser(oldUser, newUser);
        verify(userRepo, times(1)).save(argument.capture());
        assertEquals(newUser, oldUser);
    }

    @Test
    public void updatePassword() {
        ArgumentCaptor<User> argument = ArgumentCaptor.forClass(User.class);
        User user = createUser("name", "dude", "wo", "e@mai.de");
        String password = "123";
        String newPassword = "321";
        String confirm = "321";

        userService.persistUser(user, password, "123");

        when(encoder.matches(anyString(), anyString())).thenReturn(true);

        userService.updatePassword(user, "123", newPassword, confirm);

        verify(userRepo, times(2)).save(argument.capture());

    }

    @Test
    public void fetchUserByAccountName() {
    }


    @Test
    public void fetchUserIdByAccountName() {
    }

    @Test
    public void checkPassword() {
    }

}