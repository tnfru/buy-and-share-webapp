package de.hhu.propra.sharingplatform.service;

import de.hhu.propra.sharingplatform.dao.OfferRepo;
import de.hhu.propra.sharingplatform.model.Item;
import de.hhu.propra.sharingplatform.model.Offer;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.service.validation.OfferValidator;
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
        Item item = itemService.findItem(itemId);
        validate(item, requester, start, end);

        Offer offer = new Offer(item, requester, start, end);
        item.getOffers().add(offer);
        requester.getOffers().add(offer);
        offerRepo.save(offer);
    }

    public void validate(Item item, User requester, Date start, Date end) {
        OfferValidator.validate(item, requester, start, end, paymentService, apiService);
    }

    public List<Offer> getItemOffers(long itemId, User user, boolean onlyClosed) {
        if (itemService.userIsOwner(itemId, user.getId())) {
            if (!onlyClosed) {
                return offerRepo.findAllByItemIdAndAcceptIsFalseAndDeclineIsFalse(itemId);
            } else {
                return offerRepo.findAllByItemIdAndAcceptIsTrueOrDeclineIsTrue(itemId);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "This item does not belong to you");
        }
    }

    public void acceptOffer(long offerId, User user) {
        Offer offer = offerRepo.findOneById(offerId);
        if (itemService.userIsOwner(offer.getItem().getId(), user.getId())) {
            offer.setAccept(true);
            removeOverlappingOffer(offer);
            offerRepo.save(offer);
            //TODO: create contract needs ProPay Api
            // contractService.create(offer);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "This item does not belong to you");
        }
    }

    private void removeOverlappingOffer(Offer offer) {
        Item item = offer.getItem();
        List<Offer> offersWithSameItem = offerRepo.findAllByItemId(item.getId());
        for (Offer offerToTest : offersWithSameItem) {
            if (offer.getId().equals(offerToTest.getId())) {
                continue;
            }
            if (offer.getStart().after(offerToTest.getStart())) {
                if (offerToTest.getEnd().after(offer.getStart())) {
                    offerToTest.setDecline(true);
                    offerRepo.save(offerToTest);
                }
            } else {
                if (offer.getEnd().after(offerToTest.getStart())) {
                    offerToTest.setDecline(true);
                    offerRepo.save(offerToTest);
                }
            }
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

    public void removeOffersFromDeletedItem(long itemId) {
        List<Offer> toBeDeleted =
            offerRepo.findAllByItemIdAndAcceptIsFalseAndDeclineIsFalse(itemId);
        for (Offer offer :
            toBeDeleted) {
            offer.setDecline(true);
            offerRepo.save(offer);
        }
    }
}
