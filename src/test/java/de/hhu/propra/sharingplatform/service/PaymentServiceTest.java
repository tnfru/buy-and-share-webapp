package de.hhu.propra.sharingplatform.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hhu.propra.sharingplatform.dao.PaymentRepo;
import de.hhu.propra.sharingplatform.model.Contract;
import de.hhu.propra.sharingplatform.model.Item;
import de.hhu.propra.sharingplatform.model.User;
import java.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class PaymentServiceTest {

    private long millisecondsInDay;

    @MockBean
    private PaymentRepo paymentRepo;

    @MockBean
    private ApiService apiService;

    private PaymentService paymentService;

    @Before
    public void setUp() {
        this.paymentService = new PaymentService(paymentRepo, apiService);
        this.millisecondsInDay = 1000 * 60 * 60 * 24;
    }

    @Test
    public void correctPrice() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(9);

        Item item = mock(Item.class);
        when(item.getPrice()).thenReturn(10);

        Contract contract = mock(Contract.class);
        when(contract.getStart()).thenReturn(start);
        when(contract.getExpectedEnd()).thenReturn(end);
        when(contract.getItem()).thenReturn(item);

        assertEquals(100, paymentService.calculateTotalPrice(contract), 0.01);
    }

    @Test
    public void correctPriceTwo() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        Item item = mock(Item.class);
        when(item.getPrice()).thenReturn(1);

        Contract contract = mock(Contract.class);
        when(contract.getStart()).thenReturn(start);
        when(contract.getExpectedEnd()).thenReturn(end);
        when(contract.getItem()).thenReturn(item);

        assertEquals(2.0, paymentService.calculateTotalPrice(contract), 0.01);
    }

    @Test
    public void recipientIsSolvent() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        Item item = mock(Item.class);
        when(item.getPrice()).thenReturn(2);
        when(item.getBail()).thenReturn(1000);

        Contract contract = mock(Contract.class);
        when(contract.getStart()).thenReturn(start);
        when(contract.getExpectedEnd()).thenReturn(end);
        when(contract.getItem()).thenReturn(item);
        User fakeUser = new User();
        when(contract.getBorrower()).thenReturn(fakeUser);
        int totalAmount = 1000 + paymentService.calculateTotalPrice(contract);
        when(apiService.isSolvent(fakeUser, totalAmount)).thenReturn(true);

        assertTrue(paymentService.recipientSolvent(contract));
    }

    @Test
    public void recipientNotSolvent() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        Item item = mock(Item.class);
        when(item.getPrice()).thenReturn(2);
        when(item.getBail()).thenReturn(1000);

        Contract contract = mock(Contract.class);
        when(contract.getStart()).thenReturn(start);
        when(contract.getExpectedEnd()).thenReturn(end);
        when(contract.getItem()).thenReturn(item);
        User fakeUser = new User();
        when(contract.getBorrower()).thenReturn(fakeUser);

        int totalAmount = 1000 + paymentService.calculateTotalPrice(contract);
        when(apiService.isSolvent(fakeUser, totalAmount)).thenReturn(false);

        assertFalse(paymentService.recipientSolvent(contract));
    }
}
