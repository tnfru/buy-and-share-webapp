package de.hhu.propra.sharingplatform.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.hhu.propra.sharingplatform.dao.ItemRepo;
import de.hhu.propra.sharingplatform.dao.UserRepo;
import de.hhu.propra.sharingplatform.service.ItemService;
import de.hhu.propra.sharingplatform.service.OfferService;
import de.hhu.propra.sharingplatform.service.UserService;
import java.time.LocalDateTime;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

@RunWith(SpringRunner.class)
@WebMvcTest(OfferController.class)
@Import(OfferController.class)
public class OfferControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ItemService itemService;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepo userRepo;

    @MockBean
    private ItemRepo itemRepo;

    @MockBean
    OfferService offerService;

    @Autowired
    private OfferController offerController;

    @Test
    public void getStartValidInput() {
        String stringTime = "23.01.2019 - 23.02.2019";

        LocalDateTime time = offerController.getStart(stringTime);

        assertTrue(time.isEqual(LocalDateTime.of(2019, 1, 23, 0, 0, 0)));

    }

    @Test
    public void readTimeStartWrongInput() {
        boolean thrown = false;
        String stringTime = "- 23.01.2019";

        try {
            offerController.readTime(stringTime, 0);
        } catch (ResponseStatusException responseException) {
            thrown = true;
            assertEquals("403 FORBIDDEN \"Wrong dateformat\"", responseException.getMessage());
        }

        assertTrue(thrown);
    }

    @Test
    public void readTimeStartInvalidInput() {
        boolean thrown = false;
        String stringTime = "2345 - 23.01.2019";

        try {
            offerController.readTime(stringTime, 0);
        } catch (ResponseStatusException responseException) {
            thrown = true;
            assertEquals("403 FORBIDDEN \"Wrong dateformat\"", responseException.getMessage());
        }

        assertTrue(thrown);
    }

    @Test
    public void getEndValidInput() {
        String stringTime = "23.01.2019 - 23.01.2019";

        LocalDateTime time = offerController.getEnd(stringTime);

        assertTrue(time.isEqual(LocalDateTime.of(2019, 1, 23, 23, 59, 59)));
    }

    @Test
    public void readTimeEndWrongInput() {
        boolean thrown = false;
        String stringTime = "23.01.2019 - ";

        try {
            offerController.readTime(stringTime, 1);
        } catch (ResponseStatusException responseException) {
            thrown = true;
            assertEquals("403 FORBIDDEN \"Wrong dateformat\"", responseException.getMessage());
        }

        assertTrue(thrown);
    }

    @Test
    public void readTimeEndInvalidInput() {
        boolean thrown = false;
        String stringTime = "23.01.2019 - 234";

        try {
            offerController.readTime(stringTime, 1);
        } catch (ResponseStatusException responseException) {
            thrown = true;
            assertEquals("403 FORBIDDEN \"Wrong dateformat\"", responseException.getMessage());
        }

        assertTrue(thrown);
    }

    @Ignore
    @Test
    @WithMockUser
    // pls mock repos
    public void offerRequestItemDoesNotExist() throws Exception {
        mvc.perform(get("/offer/request/10000")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().is4xxClientError());
    }
}