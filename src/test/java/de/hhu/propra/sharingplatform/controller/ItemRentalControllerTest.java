package de.hhu.propra.sharingplatform.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.hhu.propra.sharingplatform.dao.ItemRentalRepo;
import de.hhu.propra.sharingplatform.dao.UserRepo;
import de.hhu.propra.sharingplatform.model.ItemRental;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.service.ImageService;
import de.hhu.propra.sharingplatform.service.ItemService;
import de.hhu.propra.sharingplatform.service.OfferService;
import de.hhu.propra.sharingplatform.service.RecommendationService;
import de.hhu.propra.sharingplatform.service.UserService;
import java.util.Optional;
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

@RunWith(SpringRunner.class)
@WebMvcTest(ItemController.class)
@Import({ItemService.class})
public class ItemRentalControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserRepo userRepo;

    @MockBean
    private ItemRentalRepo itemRentalRepo;

    @MockBean
    private UserService userService;

    @MockBean
    private OfferService offerService;

    @MockBean
    private ImageService imageService;

    @MockBean
    private RecommendationService recommendationService;

    private User testUser() {
        User user = new User();
        user.setAccountName("accountname");
        user.setPassword("password");
        user.setEmail("mail");
        user.setAddress("address");
        user.setName("name");
        user.setBan(false);
        user.setDeleted(false);
        user.setId(1L);

        return user;
    }

    private ItemRental testItem(User user) {
        ItemRental itemRental = new ItemRental(user);
        itemRental.setBail(1);
        itemRental.setDeleted(false);
        itemRental.setDescription("desc");
        itemRental.setLocation("loc");
        itemRental.setName("itemRental");
        itemRental.setOwner(user);
        itemRental.setDailyRate(2);
        itemRental.setId(3L);

        return itemRental;
    }

    /*
    NOT LOGGED in
     */

    @Test
    public void getItemDetailsNotLoggedIn() throws Exception {
        mvc.perform(get("/itemRental/details/1000")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    public void getNewItemNotLoggedIn() throws Exception {
        mvc.perform(get("/itemRental/new")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    public void postNewItemNotLoggedIn() throws Exception {
        mvc.perform(post("/itemRental/new")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("any", "any"))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    public void getRemoveItemNotLoggedIn() throws Exception {
        mvc.perform(get("/itemRental/remove/1000")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    public void getEditItemNotLoggedIn() throws Exception {
        mvc.perform(get("/itemRental/edit/1000")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    public void postEditItemNotLoggedIn() throws Exception {
        mvc.perform(post("/itemRental/edit/1000")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("any", "any"))
            .andExpect(status().is3xxRedirection());
    }



    /*
    LOGGED IN
     */

    @Test
    @WithMockUser
    public void itemDetailsDontExistLoggedIn() throws Exception {
        mvc.perform(get("/itemRental/details/1000")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser("accountname")
    public void itemDetailsExistLoggedInAndOwner() throws Exception {
        User user = testUser();

        ItemRental itemRental = new ItemRental(user);
        itemRental.setBail(1);
        itemRental.setDeleted(false);
        itemRental.setDescription("desc");
        itemRental.setLocation("loc");
        itemRental.setName("itemRental");
        itemRental.setDailyRate(2);
        itemRental.setId(3L);

        Optional<ItemRental> optI = Optional.of(itemRental);
        when(itemRentalRepo.findOneById(3)).thenReturn(itemRental);
        when(itemRentalRepo.findById(3)).thenReturn(optI);
        when(userService.fetchUserByAccountName("accountname")).thenReturn(user);
        when(userService.fetchUserIdByAccountName("accountname")).thenReturn(1L);

        mvc.perform(get("/itemRental/details/3")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Edit ItemRental")));
    }

    @Test
    @WithMockUser("otheraccountname")
    public void itemDetailsExistLoggedInNotOwner() throws Exception {
        User user = testUser();

        User user2 = new User();
        user2.setAccountName("otheraccountname");
        user2.setPassword("password");
        user2.setEmail("mail");
        user2.setAddress("address");
        user2.setName("name2");
        user2.setBan(false);
        user2.setDeleted(false);
        user2.setId(2L);

        ItemRental itemRental = testItem(user);

        Optional<ItemRental> optI = Optional.of(itemRental);
        Optional<User> optU2 = Optional.of(user2);

        when(itemRentalRepo.findById(3)).thenReturn(optI);
        when(itemRentalRepo.findOneById(3)).thenReturn(itemRental);
        when(userRepo.findByAccountName("otheraccountname")).thenReturn(optU2);

        mvc.perform(get("/itemRental/details/3")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Request")));
    }

    @Test
    @WithMockUser("accountname")
    public void getNewItemLoggedIn() throws Exception {
        mvc.perform(get("/itemRental/new")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("New ItemRental")));
    }

    //TODO: Add image upload
    //@Test
    @WithMockUser("accountname")
    public void postNewItemWrongLoggedIn() throws Exception {
        User user = testUser();

        Optional<User> optU = Optional.of(user);

        when(userRepo.findByAccountName("accountname")).thenReturn(optU);

        mvc.perform(post("/itemRental/new")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("any", "any"))
            .andExpect(status().is4xxClientError());
    }


    //TODO: Add image upload
    //@Test
    @WithMockUser("accountname")
    public void postNewItemCorrectLoggedIn() throws Exception {
        User user = testUser();

        Optional<User> optU = Optional.of(user);

        when(userRepo.findByAccountName("accountname")).thenReturn(optU);

        mvc.perform(post("/itemRental/new")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("name", "name")
            .param("dailyRate", "1")
            .param("bail", "2")
            .param("location", "loc")
            .param("description", "desc"))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser("accountname")
    public void removeItemDontExistsLoggedIn() throws Exception {
        User user = testUser();

        Optional<User> optU = Optional.of(user);

        when(userRepo.findByAccountName("accountname")).thenReturn(optU);

        mvc.perform(get("/itemRental/remove/1000")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser("otheraccountname")
    public void removeItemExistsLoggedInNotOwner() throws Exception {
        User user = testUser();

        User user2 = new User();
        user2.setAccountName("otheraccountname");
        user2.setPassword("password");
        user2.setEmail("mail");
        user2.setAddress("address");
        user2.setName("name2");
        user2.setBan(false);
        user2.setDeleted(false);
        user2.setId(2L);

        ItemRental itemRental = testItem(user);

        Optional<ItemRental> optI = Optional.of(itemRental);
        Optional<User> optU2 = Optional.of(user2);

        when(userRepo.findByAccountName("otheraccountname")).thenReturn(optU2);
        when(itemRentalRepo.findById(3)).thenReturn(optI);
        when(itemRentalRepo.findOneById(3)).thenReturn(itemRental);

        mvc.perform(get("/itemRental/remove/3")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().isForbidden());

        verify(itemRentalRepo, times(0)).save(any());
    }

    @Test
    @WithMockUser("accountname")
    public void removeItemExistsLoggedInIsOwner() throws Exception {
        User user = testUser();

        ItemRental itemRental = testItem(user);

        Optional<ItemRental> optI = Optional.of(itemRental);

        when(userService.fetchUserIdByAccountName("accountname")).thenReturn(1L);
        when(itemRentalRepo.findById(3)).thenReturn(optI);
        when(itemRentalRepo.findOneById(3)).thenReturn(itemRental);

        mvc.perform(get("/itemRental/remove/3")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().is3xxRedirection());

        verify(itemRentalRepo, times(1)).save(any());
    }

    @Test
    @WithMockUser("accountname")
    public void editItemDontExistsLoggedIn() throws Exception {
        User user = testUser();

        mvc.perform(get("/itemRental/edit/3")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser("otheraccountname")
    public void editItemExistsLoggedInNotOwner() throws Exception {
        User user = testUser();

        User user2 = new User();
        user2.setAccountName("otheraccountname");
        user2.setPassword("password");
        user2.setEmail("mail");
        user2.setAddress("address");
        user2.setName("name2");
        user2.setBan(false);
        user2.setDeleted(false);
        user2.setId(2L);

        ItemRental itemRental = testItem(user);

        Optional<ItemRental> optI = Optional.of(itemRental);
        Optional<User> optU2 = Optional.of(user2);

        when(userRepo.findByAccountName("otheraccountname")).thenReturn(optU2);
        when(itemRentalRepo.findById(3)).thenReturn(optI);

        mvc.perform(get("/itemRental/edit/3")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser("accountname")
    public void editItemExistsLoggedInIsOwner() throws Exception {
        User user = testUser();

        ItemRental itemRental = testItem(user);

        Optional<ItemRental> optI = Optional.of(itemRental);

        when(userService.fetchUserIdByAccountName("accountname")).thenReturn(1L);
        when(itemRentalRepo.findById(3)).thenReturn(optI);

        mvc.perform(get("/itemRental/edit/3")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser("accountname")
    public void postEditItemDontExistLoggedIn() throws Exception {
        User user = testUser();

        Optional<User> optU = Optional.of(user);

        when(userRepo.findByAccountName("accountname")).thenReturn(optU);

        mvc.perform(post("/itemRental/edit/1000")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("any", "any"))
            .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser("otheraccountname")
    public void postEditItemExistLoggedInNotOwner() throws Exception {
        User user = testUser();

        User user2 = new User();
        user2.setAccountName("otheraccountname");
        user2.setPassword("password");
        user2.setEmail("mail");
        user2.setAddress("address");
        user2.setName("name2");
        user2.setBan(false);
        user2.setDeleted(false);
        user2.setId(2L);

        ItemRental itemRental = testItem(user);

        Optional<User> optU = Optional.of(user2);

        when(userRepo.findByAccountName("otheraccountname")).thenReturn(optU);
        when(itemRentalRepo.findOneById(3L)).thenReturn(itemRental);
        when(itemRentalRepo.findById(3L)).thenReturn(Optional.of(itemRental));

        mvc.perform(post("/itemRental/edit/3")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("any", "any"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser("accountname")
    public void postEditItemExistLoggedInIsOwner() throws Exception {
        User user = testUser();

        ItemRental itemRental = testItem(user);

        when(itemRentalRepo.findOneById(3L)).thenReturn(itemRental);
        when(itemRentalRepo.findById(3L)).thenReturn(Optional.of(itemRental));
        when(userService.fetchUserIdByAccountName("accountname")).thenReturn(1L);

        mvc.perform(post("/itemRental/edit/3")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("name", "name")
            .param("dailyRate", "1")
            .param("bail", "2")
            .param("location", "loc")
            .param("description", "desc"))
            .andExpect(status().is3xxRedirection());

        verify(itemRentalRepo, times(1)).save(any());
    }

    @Test
    @WithMockUser("accountname")
    public void postEditItemExistLoggedInIsOwnerInvalidItem() throws Exception {
        User user = testUser();

        ItemRental itemRental = testItem(user);

        when(itemRentalRepo.findOneById(3L)).thenReturn(itemRental);
        when(itemRentalRepo.findById(3L)).thenReturn(Optional.of(itemRental));
        when(userService.fetchUserIdByAccountName("accountname")).thenReturn(1L);

        mvc.perform(post("/itemRental/edit/3")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("name", "name")
            .param("location", "loc")
            .param("description", "desc"))
            .andExpect(status().isBadRequest());

        verify(itemRentalRepo, times(0)).save(any());
    }
}
