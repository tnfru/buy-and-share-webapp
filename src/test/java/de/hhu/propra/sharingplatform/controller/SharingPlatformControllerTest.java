package de.hhu.propra.sharingplatform.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.hhu.propra.sharingplatform.dao.ItemRentalRepo;
import de.hhu.propra.sharingplatform.dao.ItemSaleRepo;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.model.items.ItemRental;
import de.hhu.propra.sharingplatform.model.items.ItemSale;
import java.util.Collections;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:application-dev.properties")
@AutoConfigureMockMvc
public class SharingPlatformControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ItemRentalRepo itemRentalRepo;

    @MockBean
    private ItemSaleRepo itemSaleRepo;

    @Test
    public void getMainPageHeaderFooterLoaded() throws Exception {
        mvc.perform(get("/")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Borrow.Ly")))
            .andExpect(content().string(containsString("About us")));
    }

    @Test
    public void postMainPageSearch() throws Exception {
        User user = new User();
        user.setId(100L);
        user.setName("user");
        user.setAccountName("user");

        ItemRental stuff = new ItemRental(user);
        when(itemRentalRepo.findAll()).thenReturn(Collections.singleton(stuff));

        when(itemRentalRepo.findAllByNameContainsIgnoreCaseAndDeletedIsFalse("stuff"))
            .thenReturn(Collections
            .singletonList(stuff));

        mvc.perform(post("/")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("search", "stuff")
            .param("btn", "rental"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("stuff")));
    }

    @Test
    public void getSaleMainPageHeaderFooterLoaded() throws Exception {
        mvc.perform(get("/sale")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Borrow.Ly")))
            .andExpect(content().string(containsString("About us")));
    }

    @Test
    public void postSaleMainPageSearch() throws Exception {
        User user = new User();
        user.setId(100L);
        user.setName("user");
        user.setAccountName("user");

        ItemSale stuff = new ItemSale(user);
        when(itemSaleRepo.findAll()).thenReturn(Collections.singleton(stuff));

        when(itemSaleRepo.findAllByNameContainsIgnoreCaseAndDeletedIsFalse("stuff"))
            .thenReturn(Collections
            .singletonList(stuff));

        mvc.perform(post("/sale")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("search", "stuff")
            .param("btn", "sale"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("stuff")));
    }
}