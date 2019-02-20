package de.hhu.propra.sharingplatform.controller;

import static org.hamcrest.CoreMatchers.containsString;
import de.hhu.propra.sharingplatform.dao.UserRepo;
import de.hhu.propra.sharingplatform.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.StatusAssertions;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
@Import({UserService.class})
public class UserControllerTest {


    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserRepo userRepo;


    @MockBean
    private UserService userService;

    @Test
    public void userRegisterGetNotLoggedIn() throws Exception {
        mvc.perform(get("/user/register")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Send")));
    }

    @Test
    @WithMockUser("accountname")
    public void userRegisterGetLoggedIn() throws Exception {
        mvc.perform(get("/user/register")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("logged in")));
    }



}
