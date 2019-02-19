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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class UserServiceTest {

    @MockBean
    private UserRepo userRepo;

    private UserService userService;

    public User createFakeUser() {

        return new User();
    }

    public User createRealUser(String name, String accountName, String address, String email) {
        User user = new User();
        user.setName(name);
        user.setAccountName(accountName);
        user.setAddress(address);
        user.setEmail(email);
        return user;
    }

    @Before
    public void setUp() {
        userService = new UserService(userRepo);
        //userService = mock(UserService.class);
        //User user = createRealUser();
        /*
        when(user.getPasswordHash()).thenReturn("foo");
        when(user.getName()).thenReturn("foo");
        when(user.getEmail()).thenReturn("foo@bar.de");
        when(user.getAddress()).thenReturn("foo");
        userTwo = new User();
        password = "123";
        confirm = "123";
        name = "testPerson";
        String addresse = "testAddresse";
        String email = "test@test.de";
        user.setEmail(email);
        user.setName(name);
        user.setAddress(addresse);
        */
    }

    @Test
    public void persistUser() {
        User user = createRealUser("name", "accountname", "addresse", "e@mail.de");
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

    }

    @Test
    public void fetchUserByAccountName() {
    }

    @Test
    public void updatePassword() {
    }

    @Test
    public void fetchUserIdByAccountName() {
    }

    @Test
    public void checkPassword() {
    }

}