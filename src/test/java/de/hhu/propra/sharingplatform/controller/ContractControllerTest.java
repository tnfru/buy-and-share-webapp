package de.hhu.propra.sharingplatform.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.hhu.propra.sharingplatform.dao.ItemRepo;
import de.hhu.propra.sharingplatform.dao.UserRepo;
import de.hhu.propra.sharingplatform.model.Offer;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.model.contracts.BorrowContract;
import de.hhu.propra.sharingplatform.model.contracts.Contract;
import de.hhu.propra.sharingplatform.model.items.ItemRental;
import de.hhu.propra.sharingplatform.service.ContractService;
import de.hhu.propra.sharingplatform.service.ImageService;
import de.hhu.propra.sharingplatform.service.ItemService;
import de.hhu.propra.sharingplatform.service.OfferService;
import de.hhu.propra.sharingplatform.service.RecommendationService;
import de.hhu.propra.sharingplatform.service.UserService;
import java.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest(ContractController.class)
@Import({ItemService.class})
public class ContractControllerTest {

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
    private ContractService contractService;

    @MockBean
    private RecommendationService recommendationService;

    @MockBean
    private Contract contract;

    private Contract testContract() {
        User user1 = new User();
        User user2 = new User();

        ItemRental item = new ItemRental(user1);
        Offer offer = new Offer(item, user2, LocalDateTime.now(), LocalDateTime.now().plusDays(2));
        return new BorrowContract(offer);
    }

    @Before
    public void exclueBaseController() {
        when(userService.fetchUserByAccountName(anyString())).thenReturn(new User());
    }

    /*
     *  NOT Logged In
     */

    @Test
    public void contractAcceptReturnNotLoggedIn() throws Exception {
        mvc.perform(post("/contract/1000/acceptReturn")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    public void contractReturnItemNotLoggedIn() throws Exception {
        mvc.perform(post("/contract/1000/returnItem")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    public void saleItemNotLoggedIn() throws Exception {
        mvc.perform(post("/contract/sale/1000")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().is3xxRedirection());
    }

    /*
     *  Logged In
     */

    @Test
    @WithMockUser("accountname")
    public void acceptReturnLoggedInValid() throws Exception {

        Mockito.doNothing().when(contractService).acceptReturn(anyLong(), eq("accountname"));

        mvc.perform(post("/contract/1000/acceptReturn")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().is3xxRedirection());

        verify(contractService, times(1)).acceptReturn(anyLong(), eq("accountname"));
    }

    @Test
    @WithMockUser("accountname")
    public void returnItemLoggedInValid() throws Exception {

        Mockito.doNothing().when(contractService).returnItem(anyLong(), eq("accountname"));

        mvc.perform(post("/contract/1000/returnItem")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().is3xxRedirection());

        verify(contractService, times(1)).returnItem(anyLong(), eq("accountname"));
    }

    @Test
    @WithMockUser("accountname")
    public void sellItemLoggedInValid() throws Exception {

        Mockito.doNothing().when(contractService).buySaleItem(anyLong(), eq("accountname"));

        mvc.perform(post("/contract/sale/1000")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().is3xxRedirection());

        verify(contractService, times(1)).buySaleItem(anyLong(), eq("accountname"));
    }
}