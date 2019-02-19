package de.hhu.propra.sharingplatform.service;

import de.hhu.propra.sharingplatform.dao.ItemRepo;
import de.hhu.propra.sharingplatform.dao.OfferRepo;
import de.hhu.propra.sharingplatform.model.Item;
import de.hhu.propra.sharingplatform.model.Offer;
import de.hhu.propra.sharingplatform.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;

@Service
public class OfferService {

    private OfferRepo offerRepo;


    private ContractService contractService;

    private ApiService apiService;

    private PaymentService paymentService;

    private ItemService itemService;


    @Autowired
    private ItemRepo itemRepo;

    @Autowired
    public OfferService(ContractService contractService, OfferRepo offerRepo,
                        ApiService apiService, PaymentService paymentService,
                        ItemService itemService) {
        this.contractService = contractService;
        this.offerRepo = offerRepo;
        this.apiService = apiService;
        this.paymentService = paymentService;
        this.itemService = itemService;
    }

    public void create(long itemId, User requester, Date start, Date end) {
        Item item = itemRepo.findOneById(itemId);
        validate(item, requester, start, end);

        Offer offer = new Offer(item, requester, start, end);
        item.getOffers().add(offer);
        requester.getOffers().add(offer);
        offerRepo.save(offer);
    }

    /* Return values:
     *  0: all gucci
     *  1: start date >= end date
     *  2: item not available at given time
     *  3: not enough money
     *  4: borrower account banned
     */
    public int validate(Item item, User requester, Date start, Date end) {
        long millisecondsInDay = 1000 * 60 * 60 * 24;
        double totalCost = paymentService.calculateTotalPrice(item, start, end) + item.getBail();

        if ((end.getTime() - start.getTime()) / millisecondsInDay < 1) {
            return 1;
        } else if (!item.isAvailable()) {
            return 2;
        } else if (!(apiService.isSolventFake(requester, totalCost))) {
            return 3;
        } else if (requester.isBan()) {
            return 4;
        } else {
            return 0;
        }
    }

    void accept(long id) {
        Offer offer = offerRepo.findOneById(id);
        offer.setAccept(true);
        offerRepo.save(offer);
        contractService.create(offer);
    }

    void decline(long id) {
        Offer offer = offerRepo.findOneById(id);
        offer.setDecline(true);
        offerRepo.save(offer);
    }

    public List<Offer> getItemOffers(long itemId, User user) {
        if (itemService.userIsOwner(itemId, user.getId())) {
            return offerRepo.findAllByItemId(itemId);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "This item does not belong to you");
        }

    }

    public void acceptOffer(long offerId, User user) {
        Offer offer = offerRepo.findOneById(offerId);
        if (itemService.userIsOwner(offer.getItem().getId(), user.getId())) {
            offer.setAccept(true);
            contractService.create(offer);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "This item does not belong to you");
        }
    }

    public void declineOffer(long offerId, User user) {
        Offer offer = offerRepo.findOneById(offerId);
        if (itemService.userIsOwner(offer.getItem().getId(), user.getId())) {
            offer.setDecline(true);
            offerRepo.save(offer);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "This item does not belong to you");
        }
    }

    public void deleteOffer(long offerId, User user) {
        Offer offer = offerRepo.findOneById(offerId);
        if (userIsOfferOwner(offer, user.getId())) {
            offerRepo.delete(offer);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "This offer does not belong to you");
        }
    }

    private boolean userIsOfferOwner(Offer offer, long userId) {
        return offer.getBorrower().getId() == userId;
    }
}
