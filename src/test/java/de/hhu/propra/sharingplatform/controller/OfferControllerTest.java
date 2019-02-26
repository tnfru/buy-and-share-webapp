package de.hhu.propra.sharingplatform.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.hhu.propra.sharingplatform.dao.ItemRepo;
import de.hhu.propra.sharingplatform.dao.OfferRepo;
import de.hhu.propra.sharingplatform.dao.UserRepo;
import de.hhu.propra.sharingplatform.model.Item;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.service.ImageService;
import de.hhu.propra.sharingplatform.service.ItemService;
import de.hhu.propra.sharingplatform.service.OfferService;
import de.hhu.propra.sharingplatform.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
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
@Import( {ItemService.class})
@Ignore
public class OfferControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepo userRepo;

    @MockBean
    private ItemRepo itemRepo;

    @MockBean
    private OfferRepo offerRepo;

    @MockBean
    private OfferService offerService;

    @MockBean
    private ImageService imageService;

    @Autowired
    private OfferController offerController;

    private User user;
    private Item item;

    @Before
    public void init() {
        user = new User();
        user.setName("Test");
        user.setId((long) 1);

        item = new Item(user);
        item.setId((long) 1);
        item.setName("TestItem");
        item.setOwner(user);
        item.setBail(100);
        item.setPrice(20);
        item.setDescription("This is a test");
        item.setLocation("Test-Location");
    }

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

    // Not Logged In

    @Test
    public void offerRequestGetNotLoggedIn() throws Exception {
        mvc.perform(get("/offer/request/10000")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    public void offerRequestPostNotLoggedIn() throws Exception {
        mvc.perform(post("/offer/request/10000")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("any", "any"))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    public void offerShowNotLoggedIn() throws Exception {
        mvc.perform(get("/offer/show/10000")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    public void offerDeleteNotLoggedIn() throws Exception {
        mvc.perform(get("/offer/remove/10000")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    public void offerAcceptNotLoggedIn() throws Exception {
        mvc.perform(get("/offer/show/10000/accept")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    public void offerDeclineNotLoggedIn() throws Exception {
        mvc.perform(get("/offer/show/10000/decline")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().is3xxRedirection());
    }

    //// User Logged In
    // get offer/request

    @Test
    @WithMockUser
    public void offerRequestLoggedInItemNotInDb() throws Exception {
        when(itemRepo.findById(anyLong())).thenReturn(Optional.empty());

        mvc.perform(get("/offer/request/10000")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    public void offerRequestLoggedInItemDeleted() throws Exception {
        item.setDeleted(true);
        when(itemRepo.findById(anyLong())).thenReturn(Optional.of(item));

        mvc.perform(get("/offer/request/10000")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    public void offerRequestLoggedInValid() throws Exception {
        when(itemRepo.findById(anyLong())).thenReturn(Optional.of(item));

        mvc.perform(get("/offer/request/10000")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Borrow")));
    }

    // post offer/request

    @Test
    @WithMockUser
    public void offerRequestPostLoggedInValid() throws Exception {
        when(userService.fetchUserByAccountName(any())).thenReturn(user);
        ArgumentCaptor<Long> a1 = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<User> a2 = ArgumentCaptor.forClass(User.class);
        ArgumentCaptor<LocalDateTime> a3 = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> a4 = ArgumentCaptor.forClass(LocalDateTime.class);

        mvc.perform(post("/offer/request/1337")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("daterange", "23.02.2019 - 27.02.2019"))
            .andExpect(status().is3xxRedirection());

        verify(offerService, times(1))
            .create(a1.capture(), a2.capture(), a3.capture(), a4.capture());

        assertEquals(a1.getValue().longValue(), 1337);
        assertEquals(a2.getValue(), user);
        assertTrue(a3.getValue().equals(LocalDateTime.of(2019, 2, 23, 0, 0, 0)));
        assertTrue(a4.getValue().equals(LocalDateTime.of(2019, 2, 27, 23, 59, 59)));
    }

    @Test
    @WithMockUser
    public void offerRequestPostLoggedInInvalid() throws Exception {
        when(userService.fetchUserByAccountName(any())).thenReturn(user);

        mvc.perform(post("/offer/request/1337")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("daterange", "23.02.2wer019 - 27.02.2019"))
            .andExpect(status().isForbidden());
    }

    // offer/show

    @Test
    @WithMockUser
    public void offerShowInvalidItem() throws Exception {
        when(userService.fetchUserByAccountName(any())).thenReturn(user);
        when(itemRepo.findById(anyLong())).thenReturn(Optional.empty());

        mvc.perform(get("/offer/show/10000")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    public void offerShowValidItem() throws Exception {
        when(userService.fetchUserByAccountName(any())).thenReturn(user);
        when(itemRepo.findById(anyLong())).thenReturn(Optional.of(item));
        when(offerService.getItemOffers(anyLong(), any(), anyBoolean()))
            .thenReturn(new ArrayList<>());

        mvc.perform(get("/offer/show/10000")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().isOk());
    }

    // offer/remove

    @Test
    @WithMockUser
    public void offerRemoveValidOffer() throws Exception {
        when(userService.fetchUserByAccountName(any())).thenReturn(user);
        Mockito.doNothing().when(offerService).deleteOffer(anyLong(), eq(user));

        mvc.perform(get("/offer/remove/1000")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().is3xxRedirection());

        verify(offerService, times(1)).deleteOffer(anyLong(), eq(user));
    }

    @Test
    @WithMockUser
    public void offerRemoveOfferNotInDb() throws Exception {
        when(userService.fetchUserByAccountName(any())).thenReturn(user);
        when(offerRepo.findOneById(anyLong())).thenReturn(null);

        mvc.perform(get("/offer/remove/1000")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().is3xxRedirection());
    }

    // offer/accept

    @Test
    @WithMockUser
    public void offeAcceptOfferNotInDb() throws Exception {
        when(userService.fetchUserByAccountName(any())).thenReturn(user);
        when(offerRepo.findOneById(anyLong())).thenReturn(null);

        mvc.perform(get("/offer/show/1000/accept")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser
    public void offeAcceptOfferValid() throws Exception {
        when(userService.fetchUserByAccountName(any())).thenReturn(user);
        Mockito.doNothing().when(offerService).acceptOffer(anyLong(), eq(user));

        mvc.perform(get("/offer/show/1000/accept")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().is3xxRedirection());

        verify(offerService, times(1)).acceptOffer(anyLong(), eq(user));
    }

    // offer/decline

    @Test
    @WithMockUser
    public void offeDeclineOfferNotInDb() throws Exception {
        when(userService.fetchUserByAccountName(any())).thenReturn(user);
        when(offerRepo.findOneById(anyLong())).thenReturn(null);

        mvc.perform(get("/offer/show/1000/decline")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser
    public void offeDeclineOfferValid() throws Exception {
        when(userService.fetchUserByAccountName(any())).thenReturn(user);
        Mockito.doNothing().when(offerService).declineOffer(anyLong(), eq(user));

        mvc.perform(get("/offer/show/1000/decline")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().is3xxRedirection());

        verify(offerService, times(1)).declineOffer(anyLong(), eq(user));
    }
}