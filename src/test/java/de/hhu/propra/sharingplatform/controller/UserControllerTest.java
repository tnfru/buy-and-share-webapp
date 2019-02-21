package de.hhu.propra.sharingplatform.controller;

import static org.hamcrest.CoreMatchers.containsString;

import de.hhu.propra.sharingplatform.dao.UserRepo;
import de.hhu.propra.sharingplatform.security.SecConfig;
import de.hhu.propra.sharingplatform.service.UserService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.StatusAssertions;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
@Import({UserService.class})
@ConditionalOnClass
public class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    @MockBean
    private SecConfig secConfig;

    @Ignore
    @Test
    public void userRegisterGetNotLoggedIn() throws Exception {
        mvc.perform(get("/user/register")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Name")));
    }

    @Ignore
    @Test
    @WithMockUser(value = "spring", roles = "Admin")
    public void userRegisterGetLoggedIn() throws Exception {
        mvc.perform(get("/user/register")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().isOk());
    }

    @Ignore
    @Test
    public void userRegisterPostNotLoggedInPasswordCorrect() throws Exception {
        mvc.perform(post("/user/register")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("name", "name")
            .param("address", "address")
            .param("accountName", "accountName")
            .param("email", "email")
            .param("password", "password")
            .param("confirm", "password"))

            .andExpect(content().string(containsString("400")));
    }

    @Ignore
    @Test
    public void userRegisterPostNotLoggedInPasswordNotCorrect() throws Exception {
        mvc.perform(post("/user/register")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("name", "name")
            .param("address", "address")
            .param("accountName", "accountName")
            .param("email", "email")
            .param("password", "password")
            .param("confirm", "123"))
            .andExpect(status().is3xxRedirection());
    }
}
