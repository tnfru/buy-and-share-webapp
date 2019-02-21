package de.hhu.propra.sharingplatform.service.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hhu.propra.sharingplatform.model.Item;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.service.ApiService;
import de.hhu.propra.sharingplatform.service.PaymentService;
import java.time.LocalDateTime;
import org.junit.Before;
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

    @Before
    public void setUpTests() {
        owner = new User();
        borrower = new User();
        item = new Item(owner);
        item.setBail(234.0);

        start = LocalDateTime.now();
        end = LocalDateTime.now().plusDays(3);
    }

    @Test
    public void validateEndIsBeforeStart() {
        LocalDateTime wrongEnd = start.minusDays(1);
        boolean thrown = false;

        paymentService = mock(PaymentService.class);
        when(paymentService.calculateTotalPrice(item, start, end)).thenReturn(120.0);

        try {
            OfferValidator.validate(item, borrower, start, wrongEnd, paymentService, apiService);
        } catch (ResponseStatusException responseException) {
            thrown = true;
            assertEquals("400 BAD_REQUEST \"End date needs to be after Start date\"",
                responseException.getMessage());
        }
        assertTrue(thrown);
    }

    @Test
    public void validateEndSameAsStart() {
        boolean thrown = false;

        paymentService = mock(PaymentService.class);
        when(paymentService.calculateTotalPrice(item, start, start)).thenReturn(120.0);

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
        when(paymentService.calculateTotalPrice(item, start, end)).thenReturn(120.0);
        when(apiService.isSolvent(eq(borrower), anyDouble())).thenReturn(false);

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
        when(paymentService.calculateTotalPrice(item, start, end)).thenReturn(120.0);
        when(apiService.isSolvent(eq(borrower), anyDouble())).thenReturn(true);

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
        when(paymentService.calculateTotalPrice(item, start, end)).thenReturn(234.0);
        when(apiService.isSolvent(eq(borrower), anyDouble())).thenReturn(true);

        try {
            OfferValidator.validate(item, borrower, start, end, paymentService, apiService);
        } catch (ResponseStatusException responseException) {
            thrown = true;
        }
        assertFalse(thrown);
    }

}