package de.hhu.propra.sharingplatform.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hhu.propra.sharingplatform.dao.OfferRepo;
import de.hhu.propra.sharingplatform.dao.PaymentRepo;
import de.hhu.propra.sharingplatform.model.Item;
import de.hhu.propra.sharingplatform.model.Offer;
import de.hhu.propra.sharingplatform.model.User;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class OfferServiceTest {

    @MockBean
    private OfferRepo offerRepo;

    @MockBean
    private PaymentRepo paymentRepo;

    @MockBean
    private ContractService contractService;

    @MockBean
    private PaymentService paymentService;

    @MockBean
    private ApiService apiService;

    @Autowired
    private OfferService offerService;

    private User owner;
    private User borrower;
    private Item item;
    private Offer offer;

    @Before
    public void setUpTests() {
        owner = new User();
        borrower = new User();
        item = new Item(owner);

        Date start = new Date();
        Date end = new Date();
        end.setTime(start.getTime() + 1337);
        offer = new Offer(item, borrower, start, end);
    }

    @Test
    public void createTest() {
        offerService.create(item, borrower, new Date(), new Date());

        ArgumentCaptor<Offer> argument = ArgumentCaptor.forClass(Offer.class);
        verify(offerRepo, times(1)).save(argument.capture());
        Offer saveOffer = argument.getValue();

        Assert.assertTrue(borrower.getOffers().contains(saveOffer));
        Assert.assertTrue(item.getOffers().contains(saveOffer));

        Assert.assertFalse(offer.isAccept());
        Assert.assertFalse(offer.isDecline());
    }

    @Test(expected = NullPointerException.class)
    public void createItemNullTest() {
        offerService.create(null, borrower, new Date(), new Date());
    }

    @Test(expected = NullPointerException.class)
    public void createUserNullTest() {
        offerService.create(item, null, new Date(), new Date());
    }

    @Test
    public void acceptOfferTest() {
        when(offerRepo.findOneById(anyLong())).thenReturn(offer);
        offerService.accept(anyLong());
        ArgumentCaptor<Offer> argument1 = ArgumentCaptor.forClass(Offer.class);
        ArgumentCaptor<Offer> argument2 = ArgumentCaptor.forClass(Offer.class);

        verify(contractService, times(1)).create(argument1.capture());
        verify(offerRepo, times(1)).save(argument2.capture());

        Assert.assertEquals(offer, argument1.getValue());
        Assert.assertEquals(offer, argument2.getValue());

        Assert.assertTrue(offer.isAccept());
        Assert.assertFalse(offer.isDecline());
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

        Assert.assertEquals(offer, argument.getValue());

        Assert.assertTrue(offer.isDecline());
        Assert.assertFalse(offer.isAccept());
    }

    @Test(expected = NullPointerException.class)
    public void declineOfferNotInDbTest() {
        when(offerRepo.findOneById(anyLong())).thenReturn(null);

        offerService.decline(anyLong());
    }
}