package de.hhu.propra.sharingplatform.service;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hhu.propra.sharingplatform.dao.contractdao.BorrowContractRepo;
import de.hhu.propra.sharingplatform.dao.contractdao.ContractRepo;
import de.hhu.propra.sharingplatform.dao.contractdao.SellContractRepo;
import de.hhu.propra.sharingplatform.dto.Status;
import de.hhu.propra.sharingplatform.model.Conflict;
import de.hhu.propra.sharingplatform.model.Offer;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.model.contracts.BorrowContract;
import de.hhu.propra.sharingplatform.model.contracts.Contract;
import de.hhu.propra.sharingplatform.model.contracts.SellContract;
import de.hhu.propra.sharingplatform.model.items.Item;
import de.hhu.propra.sharingplatform.model.items.ItemRental;
import de.hhu.propra.sharingplatform.model.items.ItemSale;
import de.hhu.propra.sharingplatform.service.payment.PaymentService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ResponseStatusException;

@RunWith(SpringRunner.class)
@Import({Offer.class, ContractService.class, SellContract.class,
    BorrowContract.class, ConflictService.class, Conflict.class})
public class ContractServiceTest {

    private BorrowContract borrowContract;

    @MockBean
    ContractRepo contractRepo;

    @MockBean
    SellContractRepo sellContractRepo;

    @MockBean
    BorrowContractRepo borrowContractRepo;

    @MockBean
    PaymentService paymentService;

    @MockBean
    ConflictService conflictService;

    @MockBean
    ItemService itemService;

    @MockBean
    UserService userService;

    @Autowired
    ContractService contractService;

    @Before
    public void setUpTests() {

        User owner = new User();
        owner.setAccountName("owner");
        User borrower = new User();
        borrower.setAccountName("borrower");
        borrower.setId((long) 1);
        ItemRental itemRental = new ItemRental(owner);
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now();
        end = end.plusDays(3);
        Offer offer = new Offer(itemRental, borrower, start, end);
        borrowContract = new BorrowContract(offer);
        borrowContract.setBorrower(borrower);
        borrowContract.setId((long) 1);
        List<Conflict> conflicts = new ArrayList<>();
        borrowContract.setConflicts(conflicts);
        borrowContract.setFinished(false);

        Conflict conflict = new Conflict();
        conflict.setContract(borrowContract);
        conflict.setId(1);

    }

    @Test
    public void createContractTest() {

        setUpTests();

        User owner = new User();
        User borrower = new User();
        ItemRental itemRental = new ItemRental(owner);

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now();
        end = end.plusDays(3);
        Offer offer = new Offer(itemRental, borrower, start, end);

        ArgumentCaptor<BorrowContract> argument = ArgumentCaptor.forClass(BorrowContract.class);
        contractService.create(offer);

        verify(borrowContractRepo, times(1)).save(argument.capture());

    }

    @Test
    public void returnItemTest() {

        User owner = new User();
        User borrower = new User();
        borrower.setAccountName("borrower");
        borrower.setId((long) 1);
        ItemRental itemRental = new ItemRental(owner);
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now();
        end = end.plusDays(3);
        Offer offer = new Offer(itemRental, borrower, start, end);
        borrowContract = new BorrowContract(offer);
        borrowContract.setBorrower(borrower);
        borrowContract.setId((long) 1);

        when(borrowContractRepo.findOneById(borrowContract.getId())).thenReturn(borrowContract);

        ArgumentCaptor<BorrowContract> argument = ArgumentCaptor.forClass(BorrowContract.class);

        contractService.returnItem(borrowContract.getId(), borrower.getAccountName());

        verify(borrowContractRepo, times(1)).save(argument.capture());
    }

    @Test
    public void acceptReturnTest() {

        User owner = new User();
        owner.setAccountName("owner");
        User borrower = new User();
        borrower.setAccountName("borrower");
        borrower.setId((long) 1);
        ItemRental itemRental = new ItemRental(owner);
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now();
        end = end.plusDays(3);
        Offer offer = new Offer(itemRental, borrower, start, end);
        borrowContract = new BorrowContract(offer);
        borrowContract.setBorrower(borrower);
        borrowContract.setId((long) 1);

        when(borrowContractRepo.findOneById(borrowContract.getId())).thenReturn(borrowContract);

        ArgumentCaptor<BorrowContract> argument = ArgumentCaptor.forClass(BorrowContract.class);

        contractService.acceptReturn(borrowContract.getId(), owner.getAccountName());

        verify(borrowContractRepo, times(1)).save(argument.capture());
    }

    @Test
    public void openConflictTest() {

        User owner = new User();
        owner.setAccountName("owner");
        User borrower = new User();
        borrower.setAccountName("borrower");
        borrower.setId((long) 1);
        ItemRental itemRental = new ItemRental(owner);
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now();
        end = end.plusDays(3);
        Offer offer = new Offer(itemRental, borrower, start, end);
        borrowContract = new BorrowContract(offer);
        borrowContract.setBorrower(borrower);
        borrowContract.setId((long) 1);
        List<Conflict> conflicts = new ArrayList<>();
        borrowContract.setConflicts(conflicts);

        when(borrowContractRepo.findOneById(borrowContract.getId())).thenReturn(borrowContract);

        ArgumentCaptor<BorrowContract> argument = ArgumentCaptor.forClass(BorrowContract.class);

        contractService.openConflict("desc", "borrower", borrowContract.getId());

        verify(borrowContractRepo, times(1)).save(argument.capture());
    }

    @Test
    public void calcPriceTest() {
        ArgumentCaptor<Contract> argument = ArgumentCaptor.forClass(Contract.class);

        when(borrowContractRepo.findOneById(anyLong())).thenReturn(borrowContract);

        contractService.calcPrice(anyLong());

        verify(paymentService, times(1)).createPayment(argument.capture());

        assertEquals(borrowContract, argument.getValue());
    }

    @Test
    public void calcPriceNotInDbTest() {
        ArgumentCaptor<Contract> argument = ArgumentCaptor.forClass(Contract.class);

        when(contractRepo.findOneById(anyLong())).thenReturn(null);

        contractService.calcPrice(anyLong());

        verify(paymentService, times(1)).createPayment(argument.capture());

        Assert.assertNull(argument.getValue());

    }

    @Test
    public void buySaleItemTest() {

        User owner = new User();
        owner.setAccountName("owner");
        owner.setId((long) 1);

        Item item = new ItemSale();
        item.setId((long) 1);
        ((ItemSale) item).setPrice(1);
        item.setOwner(owner);

        User customer = new User();
        customer.setId((long) 2);
        customer.setAccountName("customer");
        customer.setPropayId("1");

        when(userService.fetchUserByAccountName("customer")).thenReturn(customer);
        when(itemService.findItem(item.getId())).thenReturn(item);

        ArgumentCaptor<SellContract> argument = ArgumentCaptor.forClass(SellContract.class);

        contractService.buySaleItem(item.getId(), customer.getAccountName());

        verify(sellContractRepo, times(1)).save(argument.capture());
    }

    @Test
    public void endContractTest() {

        User owner = new User();
        owner.setAccountName("owner");
        User borrower = new User();
        borrower.setAccountName("borrower");
        borrower.setId((long) 1);
        ItemRental itemRental = new ItemRental(owner);
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now();
        end = end.plusDays(3);
        Offer offer = new Offer(itemRental, borrower, start, end);
        borrowContract = new BorrowContract(offer);
        borrowContract.setBorrower(borrower);
        borrowContract.setId((long) 1);
        List<Conflict> conflicts = new ArrayList<>();
        borrowContract.setConflicts(conflicts);
        borrowContract.setFinished(false);

        Conflict conflict = new Conflict();
        conflict.setContract(borrowContract);
        conflict.setId(1);

        when(conflictService.fetchConflictById(conflict.getId())).thenReturn(conflict);

        ArgumentCaptor<BorrowContract> argument = ArgumentCaptor.forClass(BorrowContract.class);

        contractService.endContract(1);

        verify(borrowContractRepo, times(1)).save(argument.capture());

    }

    @Test
    public void cancelContractTest() {

        User owner = new User();
        owner.setAccountName("owner");
        User borrower = new User();
        borrower.setAccountName("borrower");
        borrower.setId((long) 1);
        ItemRental itemRental = new ItemRental(owner);
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now();
        end = end.plusDays(3);
        Offer offer = new Offer(itemRental, borrower, start, end);
        borrowContract = new BorrowContract(offer);
        borrowContract.setBorrower(borrower);
        borrowContract.setId((long) 1);
        List<Conflict> conflicts = new ArrayList<>();
        borrowContract.setConflicts(conflicts);
        borrowContract.setFinished(false);

        Conflict conflict = new Conflict();
        conflict.setContract(borrowContract);
        conflict.setId(1);

        when(conflictService.fetchConflictById(conflict.getId())).thenReturn(conflict);

        ArgumentCaptor<BorrowContract> argument = ArgumentCaptor.forClass(BorrowContract.class);

        contractService.cancelContract(1);

        verify(borrowContractRepo, times(1)).save(argument.capture());

    }

    @Test
    public void continiueContract() {
        User owner = new User();
        owner.setAccountName("owner");
        User borrower = new User();
        borrower.setAccountName("borrower");
        borrower.setId((long) 1);
        ItemRental itemRental = new ItemRental(owner);
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now();
        end = end.plusDays(3);
        Offer offer = new Offer(itemRental, borrower, start, end);
        borrowContract = new BorrowContract(offer);
        borrowContract.setBorrower(borrower);
        borrowContract.setId((long) 1);
        List<Conflict> conflicts = new ArrayList<>();
        borrowContract.setConflicts(conflicts);
        borrowContract.setFinished(false);

        Conflict conflict = new Conflict();
        conflict.setContract(borrowContract);
        conflict.setId(1);

        when(conflictService.fetchConflictById(conflict.getId())).thenReturn(conflict);

        ArgumentCaptor<BorrowContract> argument = ArgumentCaptor.forClass(BorrowContract.class);

        contractService.continueContract(1);

        verify(borrowContractRepo, times(1)).save(argument.capture());

    }
    
    @Test
    public void returnItemWrongAccountname() {
        boolean thrown = false;

        User owner = new User();
        owner.setAccountName("owner");
        User borrower = new User();
        borrower.setAccountName("borrower");
        borrower.setId((long) 1);
        ItemRental itemRental = new ItemRental(owner);
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now();
        end = end.plusDays(3);
        Offer offer = new Offer(itemRental, borrower, start, end);
        borrowContract = new BorrowContract(offer);
        borrowContract.setBorrower(borrower);
        borrowContract.setId((long) 1);
        List<Conflict> conflicts = new ArrayList<>();
        borrowContract.setConflicts(conflicts);
        borrowContract.setFinished(false);

        when(borrowContractRepo.findOneById(borrowContract.getId())).thenReturn(borrowContract);

        try {

            contractService.returnItem(1, "falseName");

        } catch (ResponseStatusException rse) {
            thrown = true;
            assertEquals("403 FORBIDDEN \"This contract does not involve you\"", rse.getMessage());
        }
        assertTrue(thrown);
    }

    @Test
    public void acceptReturnWrongPerson() {
        boolean thrown = false;

        User owner = new User();
        owner.setAccountName("owner");
        User borrower = new User();
        borrower.setAccountName("borrower");
        borrower.setId((long) 1);
        ItemRental itemRental = new ItemRental(owner);
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now();
        end = end.plusDays(3);
        Offer offer = new Offer(itemRental, borrower, start, end);
        borrowContract = new BorrowContract(offer);
        borrowContract.setBorrower(borrower);
        borrowContract.setId((long) 1);
        List<Conflict> conflicts = new ArrayList<>();
        borrowContract.setConflicts(conflicts);
        borrowContract.setFinished(false);

        when(borrowContractRepo.findOneById(borrowContract.getId())).thenReturn(borrowContract);

        try {

            contractService.acceptReturn(1, "falseName");

        } catch (ResponseStatusException rse) {
            thrown = true;
            assertEquals("403 FORBIDDEN \"This contract does not involve you\"", rse.getMessage());
        }
        assertTrue(thrown);
    }

    @Test
    public void validateOwnerFailure() {
        boolean thrown = false;

        User owner = new User();
        owner.setAccountName("owner");
        User borrower = new User();
        borrower.setAccountName("borrower");
        borrower.setId((long) 1);
        ItemRental itemRental = new ItemRental(owner);
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now();
        end = end.plusDays(3);
        Offer offer = new Offer(itemRental, borrower, start, end);
        borrowContract = new BorrowContract(offer);
        borrowContract.setBorrower(borrower);
        borrowContract.setId((long) 1);
        List<Conflict> conflicts = new ArrayList<>();
        borrowContract.setConflicts(conflicts);
        borrowContract.setFinished(false);

        when(borrowContractRepo.findOneById(borrowContract.getId())).thenReturn(borrowContract);

        try {

            contractService.validateOwner(1, "falseName");

        } catch (ResponseStatusException rse) {
            thrown = true;
            assertEquals("403 FORBIDDEN \"This contract does not involve you\"", rse.getMessage());
        }
        assertTrue(thrown);
    }

    @Test
    public void validateOwnerCorrectOwner() {
        boolean thrown = false;

        User owner = new User();
        owner.setAccountName("owner");
        User borrower = new User();
        borrower.setAccountName("borrower");
        borrower.setId((long) 1);
        ItemRental itemRental = new ItemRental(owner);
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now();
        end = end.plusDays(3);
        Offer offer = new Offer(itemRental, borrower, start, end);
        borrowContract = new BorrowContract(offer);
        borrowContract.setBorrower(borrower);
        borrowContract.setId((long) 1);
        List<Conflict> conflicts = new ArrayList<>();
        borrowContract.setConflicts(conflicts);
        borrowContract.setFinished(false);

        when(borrowContractRepo.findOneById(borrowContract.getId())).thenReturn(borrowContract);

        try {

            contractService.validateOwner(1, "owner");

        } catch (ResponseStatusException rse) {
            thrown = true;
            assertEquals("403 FORBIDDEN \"This contract does not involve you\"", rse.getMessage());
        }
        assertFalse(thrown);
    }

    @Test
    public void openConflictTooManyConflicts() {
        boolean thrown = false;

        User owner = new User();
        owner.setAccountName("owner");
        User borrower = new User();
        borrower.setAccountName("borrower");
        borrower.setId((long) 1);
        ItemRental itemRental = new ItemRental(owner);
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now();
        end = end.plusDays(3);
        Offer offer = new Offer(itemRental, borrower, start, end);
        borrowContract = new BorrowContract(offer);
        borrowContract.setBorrower(borrower);
        borrowContract.setId((long) 1);
        Conflict conflict = new Conflict();
        conflict.setContract(borrowContract);
        conflict.setId(1);
        conflict.setStatus(Status.PENDING);
        List<Conflict> conflicts = new ArrayList<>();
        conflicts.add(conflict);
        borrowContract.setConflicts(conflicts);
        borrowContract.setFinished(false);

        when(borrowContractRepo.findOneById(borrowContract.getId())).thenReturn(borrowContract);

        try {

            contractService.openConflict("desc", "owner", 1);

        } catch (ResponseStatusException rse) {
            thrown = true;
            assertEquals("400 BAD_REQUEST \"There can only be one open conflict at a time\"",
                rse.getMessage());
        }
        assertTrue(thrown);
    }

}