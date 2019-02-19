package de.hhu.propra.sharingplatform.service;

import de.hhu.propra.sharingplatform.dao.OfferRepo;
import de.hhu.propra.sharingplatform.model.Item;
import de.hhu.propra.sharingplatform.model.Offer;
import de.hhu.propra.sharingplatform.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class OfferServiceTest {

    @MockBean
    private OfferRepo offerRepo;

    @MockBean
    private ContractService contractService;

    @MockBean
    private PaymentService paymentService;

    @MockBean
    private ApiService apiService;

    private OfferService offerService;

    private User owner;
    private User borrower;
    private Item item;
    private Offer offer;

    @Before
    public void setUpTests() {
        offerService = new OfferService(contractService, offerRepo, apiService, paymentService);
        owner = new User();
        borrower = new User();
        item = new Item(owner);

        Date start = new Date();
        Date end = new Date();
        end.setTime(start.getTime() + 1337);
        offer = new Offer(item, borrower, start, end);
    }

    /*
    @Test
    public void createTest() {
        offerService.create(item, borrower, new Date(), new Date());

        ArgumentCaptor<Offer> argument = ArgumentCaptor.forClass(Offer.class);
        verify(offerRepo, times(1)).save(argument.capture());
        Offer saveOffer = argument.getValue();

        assertTrue(borrower.getOffers().contains(saveOffer));
        assertTrue(item.getOffers().contains(saveOffer));

        assertFalse(offer.isAccept());
        assertFalse(offer.isDecline());
    }

    @Test(expected = NullPointerException.class)
    public void createItemNullTest() {
        offerService.create(null, borrower, new Date(), new Date());
    }

    @Test(expected = NullPointerException.class)
    public void createUserNullTest() {
        offerService.create(item, null, new Date(), new Date());
    }*/

    @Test
    public void acceptOfferTest() {
        when(offerRepo.findOneById(anyLong())).thenReturn(offer);
        offerService.accept(anyLong());
        ArgumentCaptor<Offer> argument1 = ArgumentCaptor.forClass(Offer.class);
        ArgumentCaptor<Offer> argument2 = ArgumentCaptor.forClass(Offer.class);

        verify(contractService, times(1)).create(argument1.capture());
        verify(offerRepo, times(1)).save(argument2.capture());

        assertEquals(offer, argument1.getValue());
        assertEquals(offer, argument2.getValue());

        assertTrue(offer.isAccept());
        assertFalse(offer.isDecline());
    }

    @Test(expected = NullPointerException.class)
    public void acceptOfferNotInDbTest() {
        when(offerRepo.findOneById(anyLong())).thenReturn(null);

        offerService.accept(anyLong());
    }

    @Test
    public void declineOfferTest() {
        when(offerRepo.findOneById(anyLong())).thenReturn(offer);
        offerService.decline(anyLong());
        ArgumentCaptor<Offer> argument = ArgumentCaptor.forClass(Offer.class);

        verify(contractService, times(0)).create(any());
        verify(offerRepo, times(1)).save(argument.capture());

        assertEquals(offer, argument.getValue());

        assertTrue(offer.isDecline());
        assertFalse(offer.isAccept());
    }

    @Test(expected = NullPointerException.class)
    public void declineOfferNotInDbTest() {
        when(offerRepo.findOneById(anyLong())).thenReturn(null);

        offerService.decline(anyLong());
    }

    /* Validate functions tests for each return value */

    @Test
    public void startAfterEnd() {
        long millisecondsInDay = 1000 * 60 * 60 * 24;

        Item item = mock(Item.class);
        User requester = mock(User.class);
        Date start = mock(Date.class);
        when(start.getTime()).thenReturn(1337 * millisecondsInDay);
        Date end = mock(Date.class);
        when(end.getTime()).thenReturn(1336 * millisecondsInDay);

        when(paymentService.calculateTotalPrice(any(), any(), any())).thenReturn(100.0);

        assertEquals(1, offerService.validate(item, requester, start, end));
    }

    @Test
    public void sameStartAndEnd() {
        long millisecondsInDay = 1000 * 60 * 60 * 24;

        Item item = mock(Item.class);
        User requester = mock(User.class);

        Date start = mock(Date.class);
        when(start.getTime()).thenReturn(1337 * millisecondsInDay);
        Date end = mock(Date.class);
        when(end.getTime()).thenReturn(1337 * millisecondsInDay);

        when(paymentService.calculateTotalPrice(any(), any(), any())).thenReturn(100.0);

        assertEquals(1, offerService.validate(item, requester, start, end));
    }

    @Test
    public void itemUnavailable() {
        long millisecondsInDay = 1000 * 60 * 60 * 24;

        Item item = mock(Item.class);
        when(item.isAvailable()).thenReturn(false);
        User requester = mock(User.class);

        Date start = mock(Date.class);
        when(start.getTime()).thenReturn(1337 * millisecondsInDay);
        Date end = mock(Date.class);
        when(end.getTime()).thenReturn(7331 * millisecondsInDay);

        when(paymentService.calculateTotalPrice(any(), any(), any())).thenReturn(100.0);

        assertEquals(2, offerService.validate(item, requester, start, end));
    }

    @Test
    public void notSolvent() {
        long millisecondsInDay = 1000 * 60 * 60 * 24;

        Item item = mock(Item.class);
        when(item.isAvailable()).thenReturn(true);
        User requester = mock(User.class);

        Date start = mock(Date.class);
        when(start.getTime()).thenReturn(1337 * millisecondsInDay);
        Date end = mock(Date.class);
        when(end.getTime()).thenReturn(7331 * millisecondsInDay);

        when(paymentService.calculateTotalPrice(any(), any(), any())).thenReturn(100.0);
        when(apiService.isSolvent(any(), anyDouble())).thenReturn(false);

        assertEquals(3, offerService.validate(item, requester, start, end));
    }

    @Test
    public void requesterBanned() {
        long millisecondsInDay = 1000 * 60 * 60 * 24;

        Item item = mock(Item.class);
        when(item.isAvailable()).thenReturn(true);
        when(item.getBail()).thenReturn(10.0);
        User requester = mock(User.class);
        when(requester.isBan()).thenReturn(true);

        Date start = mock(Date.class);
        when(start.getTime()).thenReturn(1337 * millisecondsInDay);
        Date end = mock(Date.class);
        when(end.getTime()).thenReturn(7331 * millisecondsInDay);

        when(paymentService.calculateTotalPrice(any(), any(), any())).thenReturn(100.0);
        when(apiService.isSolvent(any(), anyDouble())).thenReturn(true);

        assertEquals(4, offerService.validate(item, requester, start, end));
    }

    @Test
    public void allGucci() {
        long millisecondsInDay = 1000 * 60 * 60 * 24;

        Item item = mock(Item.class);
        when(item.isAvailable()).thenReturn(true);
        User requester = mock(User.class);
        when(requester.isBan()).thenReturn(false);

        Date start = mock(Date.class);
        when(start.getTime()).thenReturn(1337 * millisecondsInDay);
        Date end = mock(Date.class);
        when(end.getTime()).thenReturn(7331 * millisecondsInDay);

        when(paymentService.calculateTotalPrice(any(), any(), any())).thenReturn(100.0);
        when(apiService.isSolvent(any(), anyDouble())).thenReturn(true);

        assertEquals(0, offerService.validate(item, requester, start, end));
    }

}