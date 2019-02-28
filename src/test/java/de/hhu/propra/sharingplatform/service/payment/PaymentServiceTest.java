package de.hhu.propra.sharingplatform.service.payment;

import de.hhu.propra.sharingplatform.model.Offer;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.model.contracts.BorrowContract;
import de.hhu.propra.sharingplatform.model.contracts.SellContract;
import de.hhu.propra.sharingplatform.model.items.ItemRental;
import de.hhu.propra.sharingplatform.model.items.ItemSale;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PaymentServiceTest {

    SellContract sellContract;
    ItemSale itemSale;
    BorrowContract borrowContract;
    ItemRental itemRental;
    PaymentService paymentService;
    IPaymentApi api;
    User owner;
    User taker;

    @Before
    public void prepare(){
        api = mock(IPaymentApi.class);
        paymentService = new PaymentService(api);

        owner = new User();
        owner.setPropayId("foo");
        taker = new User();
        taker.setPropayId("bar");

        itemSale = new ItemSale(owner);
        itemSale.setPrice(10);
        sellContract = new SellContract(itemSale, taker);
        itemRental = new ItemRental(owner);
        itemRental.setDailyRate(10);
        itemRental.setBail(5);
        Offer offer = new Offer(itemRental, taker, LocalDateTime.now(),
            LocalDateTime.now().plusDays(1));
        borrowContract = new BorrowContract(offer);
    }

    @Test
    public void createPaymentRent(){
        when(api.getAccountBalanceLiquid("bar")).thenReturn(1000);
        paymentService.createPayment(borrowContract);

        verify(api, times(1)).reserveMoney("bar", "foo", 5);
        verify(api, times(1)).reserveMoney("bar", "foo", 20);
    }

    @Test
    public void paySell(){
        when(api.getAccountBalanceLiquid("bar")).thenReturn(1000);
        paymentService.transferPayment(sellContract);

        verify(api, times(1)).transferMoney(10, "bar", "foo");
    }

    @Test
    public void payBorrow(){
        when(api.getAccountBalanceLiquid("bar")).thenReturn(1000);
        when(api.reserveMoney(anyString(), anyString(), anyInt()))
            .thenReturn((long) 1)
            .thenReturn((long) 2);
        paymentService.createPayment(borrowContract);
        paymentService.transferPayment(borrowContract);
        paymentService.freeBailReservation(borrowContract);

        verify(api, times(1)).freeReservation(1, "bar");
        verify(api, times(1)).freeReservation(2, "bar");
    }

    @Test
    public void freeCharge(){
        when(api.getAccountBalanceLiquid("bar")).thenReturn(1000);
        when(api.reserveMoney(anyString(), anyString(), anyInt()))
            .thenReturn((long) 1)
            .thenReturn((long) 2);
        paymentService.createPayment(borrowContract);
        paymentService.freeChargeReservation(borrowContract);

        verify(api, times(1)).freeReservation(2, "bar");
    }

    @Test
    public void punish(){
        when(api.getAccountBalanceLiquid("bar")).thenReturn(1000);
        when(api.reserveMoney(anyString(), anyString(), anyInt()))
            .thenReturn((long) 2)
            .thenReturn((long) 3);
        paymentService.createPayment(borrowContract);
        paymentService.punishBailReservation(borrowContract);

        verify(api, times(1)).punishReservation(2, "bar");
    }
}