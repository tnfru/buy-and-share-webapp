package de.hhu.propra.sharingplatform.service;

import de.hhu.propra.sharingplatform.dao.PaymentRepo;
import de.hhu.propra.sharingplatform.model.Contract;
import de.hhu.propra.sharingplatform.model.Item;
import de.hhu.propra.sharingplatform.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
        Date start = mock(Date.class);
        Date end = mock(Date.class);
        when(start.getTime()).thenReturn(1337 * millisecondsInDay);
        when(end.getTime()).thenReturn(7331 * millisecondsInDay);

        Item item = mock(Item.class);
        when(item.getPrice()).thenReturn(10.0);

        Contract contract = mock(Contract.class);
        when(contract.getStart()).thenReturn(start);
        when(contract.getRealEnd()).thenReturn(end);
        when(contract.getItem()).thenReturn(item);

        assertEquals(59940.0, paymentService.calculateTotalPrice(contract), 0.01);
    }

    @Test
    public void correctPriceTwo() {
        Date start = mock(Date.class);
        Date end = mock(Date.class);
        when(start.getTime()).thenReturn(2 * millisecondsInDay);
        when(end.getTime()).thenReturn(3 * millisecondsInDay);

        Item item = mock(Item.class);
        when(item.getPrice()).thenReturn(1.0);

        Contract contract = mock(Contract.class);
        when(contract.getStart()).thenReturn(start);
        when(contract.getRealEnd()).thenReturn(end);
        when(contract.getItem()).thenReturn(item);

        assertEquals(1.0, paymentService.calculateTotalPrice(contract), 0.01);
    }

    @Test
    public void recipientIsSolvent() {
        Date start = mock(Date.class);
        Date end = mock(Date.class);
        when(start.getTime()).thenReturn(2 * millisecondsInDay);
        when(end.getTime()).thenReturn(3 * millisecondsInDay);

        Item item = mock(Item.class);
        when(item.getPrice()).thenReturn(2.0);
        when(item.getBail()).thenReturn(1000.0);

        Contract contract = mock(Contract.class);
        when(contract.getStart()).thenReturn(start);
        when(contract.getRealEnd()).thenReturn(end);
        when(contract.getItem()).thenReturn(item);
        User fakeUser = new User();
        when(contract.getBorrower()).thenReturn(fakeUser);
        double totalAmount = 1000.0 + paymentService.calculateTotalPrice(contract);
        when(apiService.checkSolvent(fakeUser, totalAmount)).thenReturn(true);

        assertTrue(paymentService.recipientSolvent(contract));
    }

    @Test
    public void recipientNotSolvent() {
        Date start = mock(Date.class);
        Date end = mock(Date.class);
        when(start.getTime()).thenReturn(2 * millisecondsInDay);
        when(end.getTime()).thenReturn(3 * millisecondsInDay);

        Item item = mock(Item.class);
        when(item.getPrice()).thenReturn(2.0);
        when(item.getBail()).thenReturn(1000.0);

        Contract contract = mock(Contract.class);
        when(contract.getStart()).thenReturn(start);
        when(contract.getRealEnd()).thenReturn(end);
        when(contract.getItem()).thenReturn(item);
        User fakeUser = new User();
        when(contract.getBorrower()).thenReturn(fakeUser);

        double totalAmount = 1000.0 + paymentService.calculateTotalPrice(contract);
        when(apiService.checkSolvent(fakeUser, totalAmount)).thenReturn(false);

        assertFalse(paymentService.recipientSolvent(contract));
    }
}
