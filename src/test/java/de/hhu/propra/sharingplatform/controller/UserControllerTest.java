package de.hhu.propra.sharingplatform.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.hhu.propra.sharingplatform.dao.UserRepo;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.service.UserService;
import de.hhu.propra.sharingplatform.service.payment.PropayAccountService;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@TestPropertySource(locations = "classpath:application-dev.properties")
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserRepo userRepo;

    @Autowired
    private UserService userService;

    @MockBean
    private PropayAccountService propayAccountService;

    // Not logged-in

    // GET

    @Test
    public void getUserRegisterNotLoggedIn() throws Exception {
        mvc.perform(get("/user/register")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Name")));
    }

    @Test
    public void getUserAccountNotLoggedIn() throws Exception {
        mvc.perform(get("/user/account")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    public void getUserEditNotLoggedIn() throws Exception {
        mvc.perform(get("/user/edit")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    public void getUserchangePasswordNotLoggedIn() throws Exception {
        mvc.perform(get("/user/changePassword")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().is3xxRedirection());
    }

    // POST
    @Test
    public void postUserRegisterNotLoggedInPasswordCorrect() throws Exception {
        User user = new User();
        user.setAccountName("othername");
        user.setPassword("password");
        user.setEmail("email@mail.com");
        user.setAddress("address");
        user.setName("othername");
        user.setRole("user");
        user.setBan(false);
        user.setDeleted(false);
        user.setId(1L);

        when(userRepo.findByAccountName(any())).thenReturn(Optional.empty())
            .thenReturn(Optional.ofNullable(user)).thenReturn(Optional.ofNullable(user));

        mvc.perform(post("/user/register")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("name", "othername")
            .param("address", "address")
            .param("accountName", "othername")
            .param("email", "email@mail.com")
            .param("password", "password")
            .param("confirm", "password"))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    public void postUserRegisterPostNotLoggedInPasswordNotCorrect() throws Exception {
        mvc.perform(post("/user/register")
            .contentType(MediaType.ALL)
            .param("name", "name")
            .param("address", "address")
            .param("accountName", "accountName")
            .param("email", "email@mail.com")
            .param("password", "password")
            .param("confirm", "123"))
            .andExpect(status().is4xxClientError());
    }

    @Test
    public void postUserEditNotLoggedIn() throws Exception {
        mvc.perform(post("/user/edit")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("any", "any"))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    public void postUserChangePasswordNotLoggedIn() throws Exception {
        mvc.perform(post("/user/changePassword")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("any", "any"))
            .andExpect(status().is3xxRedirection());
    }

    // logged-in

    // GET

    @Test
    @WithMockUser(username = "account")
    public void getUserRegisterLoggedIn() throws Exception {
        User user = new User();
        user.setAccountName("account");
        user.setPassword("password");
        user.setEmail("mail");
        user.setAddress("address");
        user.setName("name");
        user.setRole("user");
        user.setBan(false);
        user.setDeleted(false);
        user.setId(1L);

        when(userRepo.findByAccountName("account")).thenReturn(
            java.util.Optional.ofNullable(user));

        mvc.perform(get("/user/register")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser("accountName")
    public void getUserAccountLoggedIn() throws Exception {
        User user = new User();
        user.setAccountName("accountName");
        user.setPassword("password");
        user.setEmail("mail");
        user.setAddress("address");
        user.setName("name");
        user.setRole("user");
        user.setBan(false);
        user.setDeleted(false);
        user.setId(1L);

        when(userRepo.findByAccountName("accountName")).thenReturn(
            java.util.Optional.ofNullable(user));

        mvc.perform(get("/user/account")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Own Items")));
    }


    @Test
    @WithMockUser("accountName")
    public void getAdminAccountLoggedIn() throws Exception {
        User user = new User();
        user.setAccountName("accountName");
        user.setPassword("password");
        user.setEmail("mail");
        user.setAddress("address");
        user.setName("name");
        user.setRole("admin");
        user.setBan(false);
        user.setDeleted(false);
        user.setId(1L);

        when(userRepo.findByAccountName("accountName")).thenReturn(
            java.util.Optional.ofNullable(user));

        mvc.perform(get("/user/account")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Own Items")))
            .andExpect(content().string(containsString("Admin")));
    }

    @Test
    @WithMockUser("accountName")
    public void getUserChangePasswordLoggedIn() throws Exception {
        User user = new User();
        user.setAccountName("accountName");
        user.setPassword("password");
        user.setEmail("mail");
        user.setAddress("address");
        user.setName("name");
        user.setBan(false);
        user.setDeleted(false);
        user.setId(1L);

        when(userRepo.findByAccountName("accountName")).thenReturn(
            java.util.Optional.ofNullable(user));

        mvc.perform(get("/user/changePassword")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Confirm Password")));
    }

    @Test
    @WithMockUser("accountName")
    public void getUserEditLoggedIn() throws Exception {
        User user = new User();
        user.setAccountName("accountName");
        user.setPassword("password");
        user.setEmail("mail");
        user.setAddress("address");
        user.setName("name");
        user.setBan(false);
        user.setDeleted(false);
        user.setId(1L);

        when(userRepo.findByAccountName("accountName")).thenReturn(
            java.util.Optional.ofNullable(user));

        mvc.perform(get("/user/edit")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Name")));
    }

    // POST

    @Test
    @WithMockUser("accountName")
    public void postUserChangePasswordLoggedIn() throws Exception {
        User user = new User();
        user.setAccountName("accountName");
        user.setPassword("password");
        user.setEmail("mail");
        user.setAddress("address");
        user.setName("name");
        user.setBan(false);
        user.setDeleted(false);
        user.setId(1L);

        when(userRepo.findByAccountName("accountName")).thenReturn(
            java.util.Optional.ofNullable(user));

        mvc.perform(get("/user/changePassword")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED))
            .andExpect(status().isOk());
    }


    @Test
    @WithMockUser("accountName")
    public void postUserEditUserCorrect() throws Exception {
        User user = new User();
        user.setAccountName("accountName");
        user.setPassword("password");
        user.setEmail("mail");
        user.setAddress("address");
        user.setName("name");
        user.setBan(false);
        user.setDeleted(false);
        user.setId(1L);

        when(userRepo.findByAccountName("accountName")).thenReturn(
            java.util.Optional.ofNullable(user));

        mvc.perform(post("/user/edit")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("name", "name")
            .param("address", "address")
            .param("accountName", "accountName")
            .param("email", "email@mail.com")
            .param("password", "password")
            .param("confirm", "123"))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(roles = "user", username = "accountName")
    public void postChangePasswordCorrect() throws Exception {
        User user = new User();
        user.setAccountName("accountName");
        user.setPassword("password");
        user.setEmail("mail@mail.de");
        user.setAddress("address");
        user.setName("name");
        user.setBan(false);
        user.setDeleted(false);
        user.setId(100L);
        user.setPropayId("asdf");
        user.setImageFileName("/images/dummy.png");

        when(userRepo.findByAccountName("accountName")).thenReturn(
            java.util.Optional.ofNullable(user));

        mvc.perform(post("/user/changePassword")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("oldPassword", "password")
            .param("newPassword", "123")
            .param("confirm", "123"))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(roles = "user", username = "accountName")
    public void postUpdatePropay() throws Exception {
        User user = new User();
        user.setAccountName("accountName");
        user.setPassword("password");
        user.setEmail("mail@mail.de");
        user.setAddress("address");
        user.setName("name");
        user.setBan(false);
        user.setDeleted(false);
        user.setId(100L);
        user.setPropayId("asdf");
        user.setImageFileName("/images/dummy.png");

        when(userRepo.findByAccountName("accountName")).thenReturn(
            java.util.Optional.ofNullable(user));

        doNothing().when(propayAccountService)
            .transferMoney(anyInt(), any(String.class), any(String.class));

        mvc.perform(post("/user/edit/propay")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("propayAccount", "test")
            .param("propayAmount", "123"))
            .andExpect(status().is3xxRedirection());

        assertThat(userRepo.findByAccountName("accountName").get().getPropayId(), is("test"));
    }

    @Test
    @WithMockUser("accountName")
    public void getUserProfile() throws Exception {
        User user = new User();
        user.setAccountName("accountName");
        user.setPassword("password");
        user.setEmail("mail");
        user.setAddress("address");
        user.setName("name");
        user.setBan(false);
        user.setDeleted(false);
        user.setId(100L);

        when(userRepo.findById(any())).thenReturn(
            java.util.Optional.ofNullable(user));

        when(userRepo.findByAccountName("accountName")).thenReturn(
            java.util.Optional.ofNullable(user));

        mvc.perform(get("/user/100")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("accountName")));
    }

    @Test
    @WithMockUser(roles = "user", username = "accountName")
    public void getPropayBalance() throws Exception {
        User user = new User();
        user.setAccountName("accountName");
        user.setPassword("password");
        user.setEmail("mail@mail.de");
        user.setAddress("address");
        user.setName("name");
        user.setBan(false);
        user.setDeleted(false);
        user.setId(100L);
        user.setPropayId("asdf");
        user.setImageFileName("/images/dummy.png");

        when(userRepo.findByAccountName("accountName")).thenReturn(
            java.util.Optional.ofNullable(user));

        when(propayAccountService.getAccountBalance(anyString())).thenReturn(5);

        mvc.perform(get("/user/propay")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED))
            .andExpect(status().isOk());
    }
}
