package de.hhu.propra.sharingplatform.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.hhu.propra.sharingplatform.dao.ItemRepo;
import de.hhu.propra.sharingplatform.dao.UserRepo;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.model.contracts.Contract;
import de.hhu.propra.sharingplatform.model.items.Item;
import de.hhu.propra.sharingplatform.model.items.ItemSale;
import de.hhu.propra.sharingplatform.service.ImageService;
import de.hhu.propra.sharingplatform.service.ItemService;
import de.hhu.propra.sharingplatform.service.OfferService;
import de.hhu.propra.sharingplatform.service.RecommendationService;
import de.hhu.propra.sharingplatform.service.UserService;
import java.util.Optional;
import org.junit.Before;
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


@RunWith(SpringRunner.class)
@WebMvcTest(ItemSaleController.class)
@Import({ItemService.class})
public class ItemSaleControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserRepo userRepo;

    @MockBean
    private ItemRepo itemRepo;

    @MockBean
    private UserService userService;

    @MockBean
    private OfferService offerService;

    @MockBean
    private ImageService imageService;

    @MockBean
    private RecommendationService recommendationService;

    @MockBean
    private Contract contract;


    private User testUser() {
        User user = new User();
        user.setAccountName("accountname");
        user.setPassword("123");
        user.setEmail("mail");
        user.setAddress("address");
        user.setName("name");
        user.setId(1L);

        return user;
    }

    private ItemSale testItem(User user) {
        ItemSale itemSale = new ItemSale(user);
        itemSale.setDeleted(false);
        itemSale.setDescription("desc");
        itemSale.setLocation("loc");
        itemSale.setName("itemSale");
        itemSale.setOwner(user);
        itemSale.setPrice(234);
        itemSale.setId(3L);

        return itemSale;
    }

    @Before
    public void exclueBaseController() {
        when(userService.fetchUserByAccountName(anyString())).thenReturn(new User());
    }

    /*
     *  NOT Logged In
     */

    @Test
    public void getItemDetailsNotLoggedIn() throws Exception {
        mvc.perform(get("/item/sale/details/1000")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().is3xxRedirection());
    }


    @Test
    public void getNewItemNotLoggedIn() throws Exception {
        mvc.perform(get("/item/sale/new")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    public void postNewItemNotLoggedIn() throws Exception {
        mvc.perform(post("/item/sale/new")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("any", "any"))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    public void getRemoveItemNotLoggedIn() throws Exception {
        mvc.perform(post("/item/sale/remove/1000")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    public void getEditItemNotLoggedIn() throws Exception {
        mvc.perform(get("/item/sale/edit/1000")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    public void postEditItemNotLoggedIn() throws Exception {
        mvc.perform(post("/item/sale/edit/1000")
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

        mvc.perform(get("/item/rental/details/1000")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser("accountname")
    public void itemDetailsExistLoggedInAndOwner() throws Exception {
        User user = testUser();

        ItemSale itemSale = testItem(user);

        Optional<Item> optI = Optional.of(itemSale);
        when(itemRepo.findById(3L)).thenReturn(optI);
        when(userService.fetchUserByAccountName("accountname")).thenReturn(user);
        when(userService.fetchUserIdByAccountName("accountname")).thenReturn(1L);

        mvc.perform(get("/item/sale/details/3")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Edit Item")));
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
        user2.setId(2L);

        ItemSale itemSale = testItem(user);

        Optional<ItemSale> optI = Optional.of(itemSale);

        when(itemRepo.findById(3L)).thenReturn(optI);
        when(userService.fetchUserByAccountName("accountname")).thenReturn(user);
        when(userService.fetchUserByAccountName("otheraccountname")).thenReturn(user2);

        mvc.perform(get("/item/sale/details/3")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Purchase")));
    }

    @Test
    @WithMockUser("accountname")
    public void getNewItemLoggedIn() throws Exception {
        mvc.perform(get("/item/sale/new")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("New Item")));
    }

    @Test
    @WithMockUser("accountname")
    public void postNewItemWrongLoggedIn() throws Exception {
        User user = testUser();

        Optional<User> optU = Optional.of(user);

        when(userRepo.findByAccountName("accountname")).thenReturn(optU);

        mvc.perform(post("/item/sale/new")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("any", "any"))
            .andExpect(status().is4xxClientError());
    }

    // todo image upload
    @Test
    @Ignore
    @WithMockUser("accountname")
    public void postNewItemLoggedIn() throws Exception {
        User user = testUser();

        Optional<User> optU = Optional.of(user);

        when(userRepo.findByAccountName("accountname")).thenReturn(optU);

        mvc.perform(post("/item/sale/new")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("name", "name")
            .param("price", "1")
            .param("location", "loc")
            .param("image", "")
            .param("description", "desc"))
            .andExpect(status().isOk());
    }


    //TODO: Add image upload
    @Test
    @Ignore
    @WithMockUser("accountname")
    public void postNewItemCorrectLoggedIn() throws Exception {
        User user = testUser();

        Optional<User> optU = Optional.of(user);

        when(userRepo.findByAccountName("accountname")).thenReturn(optU);

        mvc.perform(post("/item/sale/new")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("name", "name")
            .param("price", "1")
            .param("location", "loc")
            .param("image", "")
            .param("description", "desc"))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser("accountname")
    public void removeItemDontExistsLoggedIn() throws Exception {
        User user = testUser();

        Optional<User> optU = Optional.of(user);

        when(userRepo.findByAccountName("accountname")).thenReturn(optU);

        mvc.perform(get("/item/sale/remove/1000")
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

        ItemSale itemSale = testItem(user);

        Optional<ItemSale> optI = Optional.of(itemSale);
        Optional<User> optU2 = Optional.of(user2);

        when(userRepo.findByAccountName("otheraccountname")).thenReturn(optU2);
        when(itemRepo.findById(3L)).thenReturn(optI);

        mvc.perform(post("/item/sale/remove/3")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().isForbidden());

        verify(itemRepo, times(0)).save(any());
    }

    @Test
    @WithMockUser("accountname")
    public void removeItemExistsLoggedInIsOwner() throws Exception {
        User user = testUser();

        ItemSale itemSale = testItem(user);

        when(userService.fetchUserIdByAccountName("accountname")).thenReturn(1L);
        when(itemRepo.findById(3L)).thenReturn(Optional.of(itemSale));

        mvc.perform(post("/item/sale/remove/3")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().is3xxRedirection());

        verify(itemRepo, times(1)).save(any());
    }

    @Test
    @WithMockUser("accountname")
    public void editItemDontExistsLoggedIn() throws Exception {
        User user = testUser();

        mvc.perform(get("/item/sale/edit/3")
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

        ItemSale itemSale = testItem(user);

        Optional<ItemSale> optI = Optional.of(itemSale);
        Optional<User> optU2 = Optional.of(user2);

        when(userRepo.findByAccountName("otheraccountname")).thenReturn(optU2);
        when(itemRepo.findById(3L)).thenReturn(optI);

        mvc.perform(get("/item/sale/edit/3")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser("accountname")
    public void editItemExistsLoggedInIsOwner() throws Exception {
        User user = testUser();

        ItemSale itemSale = testItem(user);

        Optional<ItemSale> optI = Optional.of(itemSale);

        when(userService.fetchUserIdByAccountName("accountname")).thenReturn(1L);
        when(itemRepo.findById(3L)).thenReturn(optI);

        mvc.perform(get("/item/sale/edit/3")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser("accountname")
    public void postEditItemDontExistLoggedIn() throws Exception {
        User user = testUser();

        Optional<User> optU = Optional.of(user);

        when(userRepo.findByAccountName("accountname")).thenReturn(optU);

        mvc.perform(post("/item/sale/edit/1000")
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

        ItemSale itemSale = testItem(user);

        Optional<User> optU = Optional.of(user2);

        when(userRepo.findByAccountName("otheraccountname")).thenReturn(optU);
        when(itemRepo.findById(3L)).thenReturn(Optional.of(itemSale));

        mvc.perform(post("/item/sale/edit/3")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("any", "any"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser("accountname")
    public void postEditItemExistLoggedInIsOwner() throws Exception {
        User user = testUser();

        ItemSale itemSale = testItem(user);

        when(itemRepo.findById(3L)).thenReturn(Optional.of(itemSale));
        when(userService.fetchUserIdByAccountName("accountname")).thenReturn(1L);

        mvc.perform(post("/item/sale/edit/3")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("name", "name")
            .param("price", "1")
            .param("location", "loc")
            .param("description", "desc"))
            .andExpect(status().is3xxRedirection());

        verify(itemRepo, times(1)).save(any());
    }

    @Test
    @WithMockUser("accountname")
    public void postEditItemExistLoggedInIsOwnerInvalidItem() throws Exception {
        User user = testUser();

        ItemSale itemSale = testItem(user);

        when(itemRepo.findById(3L)).thenReturn(Optional.of(itemSale));
        when(userService.fetchUserIdByAccountName("accountname")).thenReturn(1L);

        mvc.perform(post("/item/sale/edit/3")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("name", "name")
            .param("location", "loc")
            .param("description", "desc"))
            .andExpect(status().isBadRequest());

        verify(itemRepo, times(0)).save(any());
    }
}