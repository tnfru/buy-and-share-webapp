package de.hhu.propra.sharingplatform.service;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.anyDouble;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hhu.propra.sharingplatform.dao.OfferRepo;
import de.hhu.propra.sharingplatform.model.Item;
import de.hhu.propra.sharingplatform.model.Offer;
import de.hhu.propra.sharingplatform.model.User;
import java.time.LocalDateTime;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

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

    @MockBean
    private ItemService itemService;

    private OfferService offerService;

    private User owner;
    private User borrower;
    private Item item;
    private Offer offer;

    @Before
    public void setUpTests() {
        offerService = new OfferService(contractService, offerRepo, apiService,
            paymentService, itemService);
        owner = new User();
        borrower = new User();
        item = new Item(owner);

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now();
        end = end.plusDays(3);
        offer = new Offer(item, borrower, start, end);
    }

    /*
    @Test
    public void createTest() {
        offerService.create(item, borrower, new LocalDateTime(), new LocalDateTime());

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
        offerService.create(null, borrower, new LocalDateTime(), new LocalDateTime());
    }

    @Test(expected = NullPointerException.class)
    public void createUserNullTest() {
        offerService.create(item, null, new LocalDateTime(), new LocalDateTime());
    }*/

    @Ignore
    @Test
    public void acceptOfferTest() {
        when(offerRepo.findOneById(anyLong())).thenReturn(offer);
        //offerService.accept(anyLong());
        ArgumentCaptor<Offer> argument1 = ArgumentCaptor.forClass(Offer.class);
        ArgumentCaptor<Offer> argument2 = ArgumentCaptor.forClass(Offer.class);

        verify(contractService, times(1)).create(argument1.capture());
        verify(offerRepo, times(1)).save(argument2.capture());

        assertEquals(offer, argument1.getValue());
        assertEquals(offer, argument2.getValue());

        assertTrue(offer.isAccept());
        assertFalse(offer.isDecline());
    }

    @Ignore
    @Test(expected = NullPointerException.class)
    public void acceptOfferNotInDbTest() {
        when(offerRepo.findOneById(anyLong())).thenReturn(null);

        //offerService.accept(anyLong());
    }

    @Ignore
    @Test
    public void declineOfferTest() {
        when(offerRepo.findOneById(anyLong())).thenReturn(offer);
        //offerService.declineOffer(anyLong(), );
        ArgumentCaptor<Offer> argument = ArgumentCaptor.forClass(Offer.class);

        verify(contractService, times(0)).create(any());
        verify(offerRepo, times(1)).save(argument.capture());

        assertEquals(offer, argument.getValue());

        assertTrue(offer.isDecline());
        assertFalse(offer.isAccept());
    }

    @Ignore
    @Test(expected = NullPointerException.class)
    public void declineOfferNotInDbTest() {
        when(offerRepo.findOneById(anyLong())).thenReturn(null);

        //offerService.decline(anyLong());
    }

    /* Validate functions tests for each return value */

    /*@Test
    public void startAfterEnd() {
        Item item = mock(Item.class);
        User requester = mock(User.class);
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().minusDays(1);

        when(paymentService.calculateTotalPrice(any(), any(), any())).thenReturn(100.0);

        assertEquals(1, offerService.validate(item, requester, start, end));
    }

    @Test
    public void sameStartAndEnd() {
        Item item = mock(Item.class);
        User requester = mock(User.class);

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.from(start);

        when(paymentService.calculateTotalPrice(any(), any(), any())).thenReturn(100.0);

        assertEquals(1, offerService.validate(item, requester, start, end));
    }

    @Test
    public void itemUnavailable() {
        Item item = mock(Item.class);
        when(item.isAvailable()).thenReturn(false);
        User requester = mock(User.class);

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(360);

        when(paymentService.calculateTotalPrice(any(), any(), any())).thenReturn(100.0);

        assertEquals(2, offerService.validate(item, requester, start, end));
    }

    @Test
    public void notSolvent() {
        Item item = mock(Item.class);
        when(item.isAvailable()).thenReturn(true);
        User requester = mock(User.class);

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(360);

        when(paymentService.calculateTotalPrice(any(), any(), any())).thenReturn(100.0);
        when(apiService.isSolvent(any(), anyDouble())).thenReturn(false);

        assertEquals(3, offerService.validate(item, requester, start, end));
    }

    @Test
    public void requesterBanned() {
        Item item = mock(Item.class);
        when(item.isAvailable()).thenReturn(true);
        when(item.getBail()).thenReturn(10.0);
        User requester = mock(User.class);
        when(requester.isBan()).thenReturn(true);

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(360);

        when(paymentService.calculateTotalPrice(any(), any(), any())).thenReturn(100.0);
        when(apiService.isSolvent(any(), anyDouble())).thenReturn(true);
        when(apiService.isSolventFake(any(), anyDouble())).thenReturn(true);

        assertEquals(4, offerService.validate(item, requester, start, end));
    }

    @Test
    public void allGucci() {
        Item item = mock(Item.class);
        when(item.isAvailable()).thenReturn(true);
        User requester = mock(User.class);
        when(requester.isBan()).thenReturn(false);

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(360);

        when(paymentService.calculateTotalPrice(any(), any(), any())).thenReturn(100.0);
        when(apiService.isSolvent(any(), anyDouble())).thenReturn(true);
        when(apiService.isSolventFake(any(), anyDouble())).thenReturn(true);

        assertEquals(0, offerService.validate(item, requester, start, end));
    }*/

}