package de.hhu.propra.sharingplatform.service.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hhu.propra.sharingplatform.dao.ContractRepo;
import de.hhu.propra.sharingplatform.model.Contract;
import de.hhu.propra.sharingplatform.model.Item;
import de.hhu.propra.sharingplatform.model.Offer;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.service.ApiService;
import de.hhu.propra.sharingplatform.service.PaymentService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.server.ResponseStatusException;

public class OfferValidatorTest {

    private User owner;
    private User borrower;
    private Item item;
    private LocalDateTime start;
    private LocalDateTime end;

    @MockBean
    private ApiService apiService;

    @MockBean
    private PaymentService paymentService;

    @MockBean
    private ContractRepo contractRepo;

    public void alternativeSetUpTests() {
        owner = new User();
        borrower = new User();
        item = new Item(owner);
        item.setBail(234);

        start = LocalDateTime.now().plusDays(3);
        end = start.plusDays(1);
        Contract contractOne = new Contract(new Offer(item, borrower, start, end));

        LocalDateTime newStart = end.plusDays(3);
        LocalDateTime newEnd = start.plusDays(4);
        Contract contractTwo = new Contract(new Offer(item, borrower, newStart, newEnd));

        List<Contract> contractList = new ArrayList<>();
        contractList.add(contractOne);
        contractList.add(contractTwo);

        contractRepo = mock(ContractRepo.class);
        when(contractRepo.findAllByItem(item)).thenReturn(contractList);
        when(contractRepo.findAllByItemAndFinishedIsFalse(item)).thenReturn(contractList);
    }


    @Before
    public void setUpTests() {
        owner = new User();
        borrower = new User();
        item = new Item(owner);
        item.setBail(234);

        start = LocalDateTime.now();
        end = LocalDateTime.now().plusDays(3);
    }
    
    @Test
    public void periodIsNotAvailable() {
        alternativeSetUpTests();
        boolean thrown = false;

        LocalDateTime testStart = LocalDateTime.now().plusDays(6);
        LocalDateTime testEnd = start.plusDays(9);

        try {
            OfferValidator.periodIsAvailable(contractRepo, item, testStart, testEnd);
        } catch (ResponseStatusException responseException) {
            thrown = true;
            assertEquals("400 BAD_REQUEST \"Invalid period\"",
                responseException.getMessage());
        }
        assertTrue(thrown);
    }

    @Test
    public void periodIsAfterAvailable() {
        alternativeSetUpTests();
        boolean thrown = false;

        start = LocalDateTime.now().plusDays(14);
        end = start.plusDays(2);

        try {
            OfferValidator.periodIsAvailable(contractRepo, item, start, end);
        } catch (ResponseStatusException responseException) {
            thrown = true;
        }
        assertFalse(thrown);
    }


    @Test
    public void periodIsBeforeAvailable() {
        alternativeSetUpTests();
        boolean thrown = false;

        start = LocalDateTime.now();
        end = start.plusDays(2);

        try {
            OfferValidator.periodIsAvailable(contractRepo, item, start, end);
        } catch (ResponseStatusException responseException) {
            thrown = true;
        }
        assertFalse(thrown);
    }


    // alte tests
    @Test
    public void validateEndIsBeforeStart() {
        LocalDateTime wrongEnd = start.minusDays(1);
        boolean thrown = false;

        paymentService = mock(PaymentService.class);
        when(paymentService.calculateTotalPrice(item, start, end)).thenReturn(120);

        try {
            OfferValidator.validate(item, borrower, start, wrongEnd, paymentService, apiService);
        } catch (ResponseStatusException responseException) {
            thrown = true;
            assertEquals("400 BAD_REQUEST \"End date needs to be after Start date\"",
                responseException.getMessage());
        }
        assertTrue(thrown);
    }

    @Ignore
    @Test
    public void validateEndSameAsStart() {
        boolean thrown = false;

        paymentService = mock(PaymentService.class);
        when(paymentService.calculateTotalPrice(item, start, start)).thenReturn(120);

        try {
            OfferValidator.validate(item, borrower, start, start, paymentService, apiService);
        } catch (ResponseStatusException responseException) {
            thrown = true;
            assertEquals("400 BAD_REQUEST \"End date needs to be after Start date\"",
                responseException.getMessage());
        }
        assertTrue(thrown);
    }

    @Test
    public void validateIsSolvent() {
        boolean thrown = false;

        paymentService = mock(PaymentService.class);
        apiService = mock(ApiService.class);
        when(paymentService.calculateTotalPrice(item, start, end)).thenReturn(120);
        when(apiService.isSolvent(eq(borrower), anyInt())).thenReturn(false);

        try {
            OfferValidator.validate(item, borrower, start, end, paymentService, apiService);
        } catch (ResponseStatusException responseException) {
            thrown = true;
            assertEquals("400 BAD_REQUEST \"Not enough money\"",
                responseException.getMessage());
        }
        assertTrue(thrown);
    }

    @Test
    public void validateIsBanned() {
        boolean thrown = false;

        borrower.setBan(true);

        paymentService = mock(PaymentService.class);
        apiService = mock(ApiService.class);
        when(paymentService.calculateTotalPrice(item, start, end)).thenReturn(120);
        when(apiService.isSolvent(eq(borrower), anyInt())).thenReturn(true);

        try {
            OfferValidator.validate(item, borrower, start, end, paymentService, apiService);
        } catch (ResponseStatusException responseException) {
            thrown = true;
            assertEquals("400 BAD_REQUEST \"Account currently suspended\"",
                responseException.getMessage());
        }
        assertTrue(thrown);
    }

    @Test
    public void validateValidInput() {
        boolean thrown = false;

        paymentService = mock(PaymentService.class);
        apiService = mock(ApiService.class);
        when(paymentService.calculateTotalPrice(item, start, end)).thenReturn(234);
        when(apiService.isSolvent(eq(borrower), anyInt())).thenReturn(true);

        try {
            OfferValidator.validate(item, borrower, start, end, paymentService, apiService);
        } catch (ResponseStatusException responseException) {
            thrown = true;
        }
        assertFalse(thrown);
    }

}