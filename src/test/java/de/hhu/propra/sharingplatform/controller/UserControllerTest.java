package de.hhu.propra.sharingplatform.controller;

import static org.hamcrest.CoreMatchers.containsString;

import de.hhu.propra.sharingplatform.dao.UserRepo;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.service.UserService;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
@Import({UserService.class})
@ConditionalOnClass
@Ignore
public class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    UserRepo userRepo;

    @MockBean
    UserService userService;

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
        mvc.perform(post("/user/register")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("name", "name")
            .param("address", "address")
            .param("accountName", "accountName")
            .param("email", "email")
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
            .param("email", "email")
            .param("password", "password")
            .param("confirm", "123"))
            .andExpect(status().is3xxRedirection());
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
    @WithMockUser(roles = "user")
    public void getUserRegisterLoggedIn() throws Exception {
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

        when(userService.fetchUserByAccountName("accountName")).thenReturn(user);

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

        when(userService.fetchUserByAccountName("accountName")).thenReturn(user);

        mvc.perform(get("/user/account")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Own Items")));
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

        when(userService.fetchUserByAccountName("accountName")).thenReturn(user);

        mvc.perform(get("/user/changePassword")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Confirm Password")));
        ;
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

        when(userService.fetchUserByAccountName("accountName")).thenReturn(user);

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

        when(userService.fetchUserByAccountName("accountName")).thenReturn(user);

        mvc.perform(get("/user/changePassword")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser("accountName")
    public void postUserEditLoggedIn() throws Exception {
        User user = new User();
        user.setAccountName("accountName");
        user.setPassword("password");
        user.setEmail("mail");
        user.setAddress("address");
        user.setName("name");
        user.setBan(false);
        user.setDeleted(false);
        user.setId(1L);

        when(userService.fetchUserByAccountName("accountName")).thenReturn(user);

        mvc.perform(get("/user/edit")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "Admin")
    public void postUserRegisterLoggedIn() throws Exception {
        mvc.perform(post("/user/register")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED))
            .andExpect(status().is3xxRedirection());
    }

}
