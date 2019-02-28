package de.hhu.propra.sharingplatform.controller;

import de.hhu.propra.sharingplatform.dao.ConflictRepo;
import de.hhu.propra.sharingplatform.dao.contractdao.BorrowContractRepo;
import de.hhu.propra.sharingplatform.model.Conflict;
import de.hhu.propra.sharingplatform.model.Offer;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.model.contracts.BorrowContract;
import de.hhu.propra.sharingplatform.model.items.ItemRental;
import de.hhu.propra.sharingplatform.service.ConflictService;
import de.hhu.propra.sharingplatform.service.ContractService;
import de.hhu.propra.sharingplatform.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ConflictController.class)
public class ConflictControllerTest {


    @Autowired
    private MockMvc mvc;

    @MockBean
    UserService userService;

    @MockBean
    BorrowContract borrowContract;

    @MockBean
    BorrowContractRepo borrowContractRepo;

    @MockBean
    ContractService contractService;

    @MockBean
    ConflictService conflictService;

    @MockBean
    ConflictRepo conflictRepo;


    //get

    @Test
    public void getShowConflicNotLoggedIn() throws Exception {
        mvc.perform(get("/conflicts/show")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(roles = "user")
    public void getShowConflicNotAdmin() throws Exception {
        mvc.perform(get("/conflicts/show")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().isForbidden());
    }


    @Test
    @WithMockUser(roles = "admin")
    public void getShowConflicAdmin() throws Exception {

        User requester = new User();
        List<Conflict> conflicts = new ArrayList<Conflict>();
        requester.setConflicts(conflicts);

        when(userService.fetchUserByAccountName(anyString()))
            .thenReturn(requester);

        mvc.perform(get("/conflicts/show")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().is2xxSuccessful());
    }


    @Test
    @WithMockUser(username = "accountName")
    public void getConflict() throws Exception {
        User requester = new User();
        List<Conflict> conflicts = new ArrayList<Conflict>();
        requester.setConflicts(conflicts);

        when(userService.fetchUserByAccountName(anyString()))
            .thenReturn(requester);


        mvc.perform(get("/openConflict/1")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().is2xxSuccessful());

    }

    @Test
    @WithMockUser()
    public void getShowUserConflicts() throws Exception {
        User requester = new User();
        List<Conflict> conflicts = new ArrayList<Conflict>();
        requester.setConflicts(conflicts);

        when(userService.fetchUserByAccountName(anyString()))
            .thenReturn(requester);

        when(contractService.fetchBorrowContractById(anyLong()))
            .thenReturn(borrowContract);

        when(borrowContract.getConflicts()).thenReturn(conflicts);
        mvc.perform(get("/showConflicts/1")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().is2xxSuccessful());
    }


    @Test
    @WithMockUser(roles = "admin")
    public void getConflicsDetailsAsAdmin() throws Exception {


        Conflict conflict = new Conflict();
        User requester = new User();
        requester.setAccountName("Requester");
        conflict.setRequester(requester);

        User borrower = new User();
        borrower.setAccountName("Borrower");

        ItemRental item = new ItemRental(requester);

        LocalDateTime start = LocalDateTime.of(1, 1, 1, 1, 1);
        LocalDateTime end = LocalDateTime.of(1, 1, 30, 1, 1);

        Offer offer = new Offer(item, borrower, start, end);
        BorrowContract contract = new BorrowContract(offer);

        conflict.setContract(contract);

        List<Conflict> conflicts = new ArrayList<Conflict>();
        requester.setConflicts(conflicts);

        when(userService.fetchUserByAccountName(anyString()))
            .thenReturn(requester);

        when(conflictService.fetchConflictById(anyLong()))
            .thenReturn(conflict);

        mvc.perform(get("/conflicts/1/details")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().is2xxSuccessful());

    }


    //post

    @Test
    @WithMockUser()
    public void postOpenConflictCorrectParam() throws Exception {
        User requester = new User();
        List<Conflict> conflicts = new ArrayList<Conflict>();
        requester.setConflicts(conflicts);

        when(userService.fetchUserByAccountName(anyString()))
            .thenReturn(requester);


        mvc.perform(post("/openConflict/1")
            .param("description", "descripttion")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().is3xxRedirection());
    }


    @Test
    @WithMockUser(roles = "user")
    public void postOpenConflictWrongParam() throws Exception {
        User requester = new User();
        List<Conflict> conflicts = new ArrayList<Conflict>();
        requester.setConflicts(conflicts);

        when(userService.fetchUserByAccountName(anyString()))
            .thenReturn(requester);


        mvc.perform(post("/openConflict/1")
            .param("ss", "descripttion")
            .contentType(MediaType.TEXT_HTML))
            .andExpect(status().is4xxClientError());
    }


    @Test
    @WithMockUser(roles = "admin")
    public void postConflictPunishBailAsAdmin() throws Exception {

        User requester = new User();
        List<Conflict> conflicts = new ArrayList<Conflict>();
        requester.setConflicts(conflicts);

        when(userService.fetchUserByAccountName(anyString()))
            .thenReturn(requester);

        mvc.perform(post("/conflicts/1/punishBail")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED))
            .andExpect(status().is3xxRedirection());
    }


    @Test
    @WithMockUser(roles = "user")
    public void postConflictPunishBailNotAdmin() throws Exception {
        mvc.perform(post("/conflicts/1/punishBail")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED))
            .andExpect(status().isForbidden());
    }


    @Test
    @WithMockUser(roles = "admin")
    public void postConflictCancleBailAsAdmin() throws Exception {

        User requester = new User();
        List<Conflict> conflicts = new ArrayList<Conflict>();
        requester.setConflicts(conflicts);

        when(userService.fetchUserByAccountName(anyString()))
            .thenReturn(requester);


        mvc.perform(post("/conflicts/1/cancel")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(roles = "user")
    public void postConflicCancleNotAdmin() throws Exception {
        mvc.perform(post("/conflicts/1/cancel")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED))
            .andExpect(status().isForbidden());
    }


    @Test
    @WithMockUser(roles = "admin")
    public void postConflictContinueBailAsAdmin() throws Exception {

        User requester = new User();
        List<Conflict> conflicts = new ArrayList<Conflict>();
        requester.setConflicts(conflicts);

        when(userService.fetchUserByAccountName(anyString()))
            .thenReturn(requester);

        mvc.perform(post("/conflicts/1/continue")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED))
            .andExpect(status().is3xxRedirection());
    }


    @Test
    @WithMockUser(roles = "user")
    public void postConflictContinueBailNotAdmin() throws Exception {
        mvc.perform(post("/conflicts/1/continue")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "admin")
    public void postConflicFreeBailAsAdmin() throws Exception {

        User requester = new User();
        Conflict conflict = new Conflict();
        User borrower = new User();
        ItemRental item = new ItemRental(requester);

        LocalDateTime start = LocalDateTime.of(1, 1, 1, 1, 1);
        LocalDateTime end = LocalDateTime.of(1, 1, 30, 1, 1);

        Offer offer = new Offer(item, borrower, start, end);

        BorrowContract contract = new BorrowContract(offer);
        contract.setId(12L);
        conflict.setContract(contract);
        List<Conflict> conflicts = new ArrayList<Conflict>();
        conflicts.add(conflict);
        requester.setConflicts(conflicts);


        when(conflictService.fetchConflictById(anyLong()))
            .thenReturn(conflict);

        when(userService.fetchUserByAccountName(anyString()))
            .thenReturn(requester);

        mvc.perform(post("/conflicts/1/freeBail")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(roles = "user")
    public void postConflicFreeBailNotAdmin() throws Exception {
        mvc.perform(post("/conflicts/1/freeBail")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED))
            .andExpect(status().isForbidden());
    }


}
