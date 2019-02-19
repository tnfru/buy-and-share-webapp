package de.hhu.propra.sharingplatform.service;

import de.hhu.propra.sharingplatform.dao.ItemRepo;
import de.hhu.propra.sharingplatform.dao.OfferRepo;
import de.hhu.propra.sharingplatform.model.Item;
import de.hhu.propra.sharingplatform.model.Offer;
import de.hhu.propra.sharingplatform.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class OfferService {

    private OfferRepo offerRepo;


    private ContractService contractService;

    private ApiService apiService;

    private PaymentService paymentService;


    @Autowired
    private ItemRepo itemRepo;

    @Autowired
    public OfferService(ContractService contractService, OfferRepo offerRepo,
                        ApiService apiService, PaymentService paymentService) {
        this.contractService = contractService;
        this.offerRepo = offerRepo;
        this.apiService = apiService;
        this.paymentService = paymentService;
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
}
