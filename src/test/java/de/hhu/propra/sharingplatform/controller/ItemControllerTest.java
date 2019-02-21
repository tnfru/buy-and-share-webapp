package de.hhu.propra.sharingplatform.controller;

import de.hhu.propra.sharingplatform.dao.ItemRepo;
import de.hhu.propra.sharingplatform.dao.UserRepo;
import de.hhu.propra.sharingplatform.model.Item;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.service.ImageService;
import de.hhu.propra.sharingplatform.service.ItemService;
import de.hhu.propra.sharingplatform.service.OfferService;
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
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ItemController.class)
@Import( {ItemService.class})
public class ItemControllerTest {

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
    /*
    NOT LOGGED in
     */

    @Test
    public void getItemDetailsNotLoggedIn() throws Exception {
        mvc.perform(get("/item/details/1000")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    public void getNewItemNotLoggedIn() throws Exception {
        mvc.perform(get("/item/new")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    public void postNewItemNotLoggedIn() throws Exception {
        mvc.perform(post("/item/new")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("any", "any"))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    public void getRemoveItemNotLoggedIn() throws Exception {
        mvc.perform(get("/item/remove/1000")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    public void getEditItemNotLoggedIn() throws Exception {
        mvc.perform(get("/item/edit/1000")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    public void postEditItemNotLoggedIn() throws Exception {
        mvc.perform(post("/item/edit/1000")
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
        mvc.perform(get("/item/details/1000")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser("accountname")
    public void itemDetailsExistLoggedInAndOwner() throws Exception {
        User user = new User();
        user.setAccountName("accountname");
        user.setPassword("password");
        user.setEmail("mail");
        user.setAddress("address");
        user.setName("name");
        user.setBan(false);
        user.setDeleted(false);
        user.setId(1L);

        Item item = new Item(user);
        item.setAvailable(true);
        item.setBail(1.0);
        item.setDeleted(false);
        item.setDescription("desc");
        item.setLocation("loc");
        item.setName("item");
        item.setPrice(2.0);
        item.setId(3L);

        Optional<Item> optI = Optional.of(item);
        when(itemRepo.findOneById(3)).thenReturn(item);
        when(itemRepo.findById(3)).thenReturn(optI);
        when(userService.fetchUserByAccountName("accountname")).thenReturn(user);
        when(userService.fetchUserIdByAccountName("accountname")).thenReturn(1L);

        mvc.perform(get("/item/details/3")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Edit Item")));
    }

    @Test
    @WithMockUser("otheraccountname")
    public void itemDetailsExistLoggedInNotOwner() throws Exception {
        User user = new User();
        user.setAccountName("accountname");
        user.setPassword("password");
        user.setEmail("mail");
        user.setAddress("address");
        user.setName("name");
        user.setBan(false);
        user.setDeleted(false);
        user.setId(1L);

        User user2 = new User();
        user2.setAccountName("otheraccountname");
        user2.setPassword("password");
        user2.setEmail("mail");
        user2.setAddress("address");
        user2.setName("name2");
        user2.setBan(false);
        user2.setDeleted(false);
        user2.setId(2L);

        Item item = new Item(user);
        item.setAvailable(true);
        item.setBail(1.0);
        item.setDeleted(false);
        item.setDescription("desc");
        item.setLocation("loc");
        item.setName("item");
        item.setOwner(user);
        item.setPrice(2.0);
        item.setId(3L);

        Optional<Item> optI = Optional.of(item);
        Optional<User> optU2 = Optional.of(user2);

        when(itemRepo.findById(3)).thenReturn(optI);
        when(itemRepo.findOneById(3)).thenReturn(item);
        when(userRepo.findByAccountName("otheraccountname")).thenReturn(optU2);

        mvc.perform(get("/item/details/3")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Request")));
    }

    @Test
    @WithMockUser("accountname")
    public void getNewItemLoggedIn() throws Exception {
        mvc.perform(get("/item/new")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("New Item")));
    }

    //TODO: Add image upload
    //@Test
    @WithMockUser("accountname")
    public void postNewItemWrongLoggedIn() throws Exception {
        User user = new User();
        user.setAccountName("accountname");
        user.setPassword("password");
        user.setEmail("mail");
        user.setAddress("address");
        user.setName("name");
        user.setBan(false);
        user.setDeleted(false);
        user.setId(1L);

        Optional<User> optU = Optional.of(user);

        when(userRepo.findByAccountName("accountname")).thenReturn(optU);

        mvc.perform(post("/item/new")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("any", "any"))
            .andExpect(status().is4xxClientError());
    }


    //TODO: Add image upload
    //@Test
    @WithMockUser("accountname")
    public void postNewItemCorrectLoggedIn() throws Exception {
        User user = new User();
        user.setAccountName("accountname");
        user.setPassword("password");
        user.setEmail("mail");
        user.setAddress("address");
        user.setName("name");
        user.setBan(false);
        user.setDeleted(false);
        user.setId(1L);

        Optional<User> optU = Optional.of(user);

        when(userRepo.findByAccountName("accountname")).thenReturn(optU);

        mvc.perform(post("/item/new")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("name", "name")
            .param("price", "1")
            .param("bail", "2")
            .param("location", "loc")
            .param("description", "desc"))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser("accountname")
    public void removeItemDontExistsLoggedIn() throws Exception {
        User user = new User();
        user.setAccountName("accountname");
        user.setPassword("password");
        user.setEmail("mail");
        user.setAddress("address");
        user.setName("name");
        user.setBan(false);
        user.setDeleted(false);
        user.setId(1L);

        Optional<User> optU = Optional.of(user);

        when(userRepo.findByAccountName("accountname")).thenReturn(optU);

        mvc.perform(get("/item/remove/1000")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser("otheraccountname")
    public void removeItemExistsLoggedInNotOwner() throws Exception {
        User user = new User();
        user.setAccountName("accountname");
        user.setPassword("password");
        user.setEmail("mail");
        user.setAddress("address");
        user.setName("name");
        user.setBan(false);
        user.setDeleted(false);
        user.setId(1L);

        User user2 = new User();
        user2.setAccountName("otheraccountname");
        user2.setPassword("password");
        user2.setEmail("mail");
        user2.setAddress("address");
        user2.setName("name2");
        user2.setBan(false);
        user2.setDeleted(false);
        user2.setId(2L);

        Item item = new Item(user);
        item.setAvailable(true);
        item.setBail(1.0);
        item.setDeleted(false);
        item.setDescription("desc");
        item.setLocation("loc");
        item.setName("item");
        item.setOwner(user);
        item.setPrice(2.0);
        item.setId(3L);

        Optional<Item> optI = Optional.of(item);
        Optional<User> optU2 = Optional.of(user2);

        when(userRepo.findByAccountName("otheraccountname")).thenReturn(optU2);
        when(itemRepo.findById(3)).thenReturn(optI);
        when(itemRepo.findOneById(3)).thenReturn(item);

        mvc.perform(get("/item/remove/3")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().isForbidden());

        verify(itemRepo, times(0)).save(any());
    }

    @Test
    @WithMockUser("accountname")
    public void removeItemExistsLoggedInIsOwner() throws Exception {
        User user = new User();
        user.setAccountName("accountname");
        user.setPassword("password");
        user.setEmail("mail");
        user.setAddress("address");
        user.setName("name");
        user.setBan(false);
        user.setDeleted(false);
        user.setId(1L);

        Item item = new Item(user);
        item.setAvailable(true);
        item.setBail(1.0);
        item.setDeleted(false);
        item.setDescription("desc");
        item.setLocation("loc");
        item.setName("item");
        item.setOwner(user);
        item.setPrice(2.0);
        item.setId(3L);

        Optional<Item> optI = Optional.of(item);

        when(userService.fetchUserIdByAccountName("accountname")).thenReturn(1L);
        when(itemRepo.findById(3)).thenReturn(optI);
        when(itemRepo.findOneById(3)).thenReturn(item);

        mvc.perform(get("/item/remove/3")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().is3xxRedirection());

        verify(itemRepo, times(1)).save(any());
    }

    @Test
    @WithMockUser("accountname")
    public void editItemDontExistsLoggedIn() throws Exception {
        User user = new User();
        user.setAccountName("accountname");
        user.setPassword("password");
        user.setEmail("mail");
        user.setAddress("address");
        user.setName("name");
        user.setBan(false);
        user.setDeleted(false);
        user.setId(1L);

        mvc.perform(get("/item/edit/3")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser("otheraccountname")
    public void editItemExistsLoggedInNotOwner() throws Exception {
        User user = new User();
        user.setAccountName("accountname");
        user.setPassword("password");
        user.setEmail("mail");
        user.setAddress("address");
        user.setName("name");
        user.setBan(false);
        user.setDeleted(false);
        user.setId(1L);

        User user2 = new User();
        user2.setAccountName("otheraccountname");
        user2.setPassword("password");
        user2.setEmail("mail");
        user2.setAddress("address");
        user2.setName("name2");
        user2.setBan(false);
        user2.setDeleted(false);
        user2.setId(2L);

        Item item = new Item(user);
        item.setAvailable(true);
        item.setBail(1.0);
        item.setDeleted(false);
        item.setDescription("desc");
        item.setLocation("loc");
        item.setName("item");
        item.setOwner(user);
        item.setPrice(2.0);
        item.setId(3L);

        Optional<Item> optI = Optional.of(item);
        Optional<User> optU2 = Optional.of(user2);

        when(userRepo.findByAccountName("otheraccountname")).thenReturn(optU2);
        when(itemRepo.findById(3)).thenReturn(optI);

        mvc.perform(get("/item/edit/3")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser("accountname")
    public void editItemExistsLoggedInIsOwner() throws Exception {
        User user = new User();
        user.setAccountName("accountname");
        user.setPassword("password");
        user.setEmail("mail");
        user.setAddress("address");
        user.setName("name");
        user.setBan(false);
        user.setDeleted(false);
        user.setId(1L);

        Item item = new Item(user);
        item.setAvailable(true);
        item.setBail(1.0);
        item.setDeleted(false);
        item.setDescription("desc");
        item.setLocation("loc");
        item.setName("item");
        item.setOwner(user);
        item.setPrice(2.0);
        item.setId(3L);

        Optional<Item> optI = Optional.of(item);

        when(userService.fetchUserIdByAccountName("accountname")).thenReturn(1L);
        when(itemRepo.findById(3)).thenReturn(optI);

        mvc.perform(get("/item/edit/3")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser("accountname")
    public void postEditItemDontExistLoggedIn() throws Exception {
        User user = new User();
        user.setAccountName("accountname");
        user.setPassword("password");
        user.setEmail("mail");
        user.setAddress("address");
        user.setName("name");
        user.setBan(false);
        user.setDeleted(false);
        user.setId(1L);

        Optional<User> optU = Optional.of(user);

        when(userRepo.findByAccountName("accountname")).thenReturn(optU);

        mvc.perform(post("/item/edit/1000")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("any", "any"))
            .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser("otheraccountname")
    public void postEditItemExistLoggedInNotOwner() throws Exception {
        User user = new User();
        user.setAccountName("accountname");
        user.setPassword("password");
        user.setEmail("mail");
        user.setAddress("address");
        user.setName("name");
        user.setBan(false);
        user.setDeleted(false);
        user.setId(1L);

        User user2 = new User();
        user2.setAccountName("otheraccountname");
        user2.setPassword("password");
        user2.setEmail("mail");
        user2.setAddress("address");
        user2.setName("name2");
        user2.setBan(false);
        user2.setDeleted(false);
        user2.setId(2L);

        Item item = new Item(user);
        item.setAvailable(true);
        item.setBail(1.0);
        item.setDeleted(false);
        item.setDescription("desc");
        item.setLocation("loc");
        item.setName("item");
        item.setOwner(user);
        item.setPrice(2.0);
        item.setId(3L);

        Optional<User> optU = Optional.of(user2);

        when(userRepo.findByAccountName("otheraccountname")).thenReturn(optU);
        when(itemRepo.findOneById(3L)).thenReturn(item);
        when(itemRepo.findById(3L)).thenReturn(Optional.of(item));

        mvc.perform(post("/item/edit/3")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("any", "any"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser("accountname")
    public void postEditItemExistLoggedInIsOwner() throws Exception {
        User user = new User();
        user.setAccountName("accountname");
        user.setPassword("password");
        user.setEmail("mail");
        user.setAddress("address");
        user.setName("name");
        user.setBan(false);
        user.setDeleted(false);
        user.setId(1L);

        Item item = new Item(user);
        item.setAvailable(true);
        item.setBail(1.0);
        item.setDeleted(false);
        item.setDescription("desc");
        item.setLocation("loc");
        item.setName("item");
        item.setOwner(user);
        item.setPrice(2.0);
        item.setId(3L);

        when(itemRepo.findOneById(3L)).thenReturn(item);
        when(itemRepo.findById(3L)).thenReturn(Optional.of(item));
        when(userService.fetchUserIdByAccountName("accountname")).thenReturn(1L);

        mvc.perform(post("/item/edit/3")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("name", "name")
            .param("price", "1")
            .param("bail", "2")
            .param("location", "loc")
            .param("description", "desc"))
            .andExpect(status().is3xxRedirection());

        verify(itemRepo, times(1)).save(any());
    }

    @Test
    @WithMockUser("accountname")
    public void postEditItemExistLoggedInIsOwnerInvalidItem() throws Exception {
        User user = new User();
        user.setAccountName("accountname");
        user.setPassword("password");
        user.setEmail("mail");
        user.setAddress("address");
        user.setName("name");
        user.setBan(false);
        user.setDeleted(false);
        user.setId(1L);

        Item item = new Item(user);
        item.setAvailable(true);
        item.setBail(1.0);
        item.setDeleted(false);
        item.setDescription("desc");
        item.setLocation("loc");
        item.setName("item");
        item.setOwner(user);
        item.setPrice(2.0);
        item.setId(3L);

        when(itemRepo.findOneById(3L)).thenReturn(item);
        when(itemRepo.findById(3L)).thenReturn(Optional.of(item));
        when(userService.fetchUserIdByAccountName("accountname")).thenReturn(1L);

        mvc.perform(post("/item/edit/3")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("name", "name")
            .param("location", "loc")
            .param("description", "desc"))
            .andExpect(status().isBadRequest());

        verify(itemRepo, times(0)).save(any());
    }
}
