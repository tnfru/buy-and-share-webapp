package de.hhu.propra.sharingplatform.model.items;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hhu.propra.sharingplatform.model.Offer;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.model.contracts.BorrowContract;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class ItemRentalTest {

    private ItemRental itemRental;

    @Before
    public void init() {
        itemRental = new ItemRental(new User());
    }

    @Test
    public void getActiveOffersOffersFilled() {
        Offer offer1 = new Offer(itemRental, new User(), LocalDateTime.now(),
            LocalDateTime.now().plusDays(1));
        Offer offer2 = new Offer(itemRental, new User(), LocalDateTime.now(),
            LocalDateTime.now().plusDays(1));
        Offer offer3 = new Offer(itemRental, new User(), LocalDateTime.now(),
            LocalDateTime.now().plusDays(1));
        Offer offer4 = new Offer(itemRental, new User(), LocalDateTime.now(),
            LocalDateTime.now().plusDays(1));
        Offer offer5 = new Offer(itemRental, new User(), LocalDateTime.now(),
            LocalDateTime.now().plusDays(1));
        Offer offer6 = new Offer(itemRental, new User(), LocalDateTime.now(),
            LocalDateTime.now().plusDays(1));

        offer3.setDecline(true);
        offer4.setDecline(true);
        offer5.setAccept(true);

        List<Offer> offers = new ArrayList<>();
        offers.add(offer1);
        offers.add(offer2);
        offers.add(offer3);
        offers.add(offer4);
        offers.add(offer5);
        offers.add(offer6);

        itemRental.setOffers(offers);

        assertEquals(3, itemRental.getActiveOffers());
    }

    @Test
    public void getActiveOffersOffersEmpty() {
        List<Offer> offers = new ArrayList<>();

        itemRental.setOffers(offers);

        assertEquals(0, itemRental.getActiveOffers());
    }

    @Test
    public void getActiveOffersResultZero() {
        Offer offer1 = new Offer(itemRental, new User(), LocalDateTime.now(),
            LocalDateTime.now().plusDays(1));
        Offer offer2 = new Offer(itemRental, new User(), LocalDateTime.now(),
            LocalDateTime.now().plusDays(1));
        Offer offer3 = new Offer(itemRental, new User(), LocalDateTime.now(),
            LocalDateTime.now().plusDays(1));
        Offer offer4 = new Offer(itemRental, new User(), LocalDateTime.now(),
            LocalDateTime.now().plusDays(1));

        offer1.setDecline(true);
        offer2.setAccept(true);
        offer3.setDecline(true);
        offer4.setAccept(true);

        List<Offer> offers = new ArrayList<>();
        offers.add(offer1);
        offers.add(offer2);
        offers.add(offer3);
        offers.add(offer4);

        itemRental.setOffers(offers);

        assertEquals(0, itemRental.getActiveOffers());
    }

    @Test
    public void getChosenContractsFinished() {
        Offer offer1 = new Offer(itemRental, new User(), LocalDateTime.now(),
            LocalDateTime.now().plusDays(1));
        Offer offer2 = new Offer(itemRental, new User(), LocalDateTime.now(),
            LocalDateTime.now().plusDays(1));
        Offer offer3 = new Offer(itemRental, new User(), LocalDateTime.now(),
            LocalDateTime.now().plusDays(1));
        Offer offer4 = new Offer(itemRental, new User(), LocalDateTime.now(),
            LocalDateTime.now().plusDays(1));

        BorrowContract contract1 = new BorrowContract(offer1);
        BorrowContract contract2 = new BorrowContract(offer2);
        BorrowContract contract3 = new BorrowContract(offer3);
        BorrowContract contract4 = new BorrowContract(offer4);

        contract1.setFinished(true);

        List<BorrowContract> contracts = new ArrayList<>();
        contracts.add(contract1);
        contracts.add(contract2);
        contracts.add(contract3);
        contracts.add(contract4);

        itemRental.setContracts(contracts);

        assertEquals(1, itemRental.getChosenContracts(true).size());
        assertEquals(3, itemRental.getChosenContracts(false).size());
        assertEquals(contract1, itemRental.getChosenContracts(true).get(0));
        assertTrue(itemRental.getChosenContracts(false).contains(contract2));
        assertTrue(itemRental.getChosenContracts(false).contains(contract3));
        assertTrue(itemRental.getChosenContracts(false).contains(contract4));

    }
}