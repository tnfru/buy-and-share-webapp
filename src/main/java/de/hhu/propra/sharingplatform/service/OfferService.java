package de.hhu.propra.sharingplatform.service;

import de.hhu.propra.sharingplatform.dao.ContractRepo;
import de.hhu.propra.sharingplatform.dao.OfferRepo;
import de.hhu.propra.sharingplatform.model.ItemRental;
import de.hhu.propra.sharingplatform.model.Offer;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.service.payment.ApiService;
import de.hhu.propra.sharingplatform.service.payment.IPaymentApi;
import de.hhu.propra.sharingplatform.service.payment.IPaymentService;
import de.hhu.propra.sharingplatform.service.validation.OfferValidator;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
public class OfferService {

    private OfferRepo offerRepo;

    private ContractService contractService;

    private IPaymentApi apiService;

    private IPaymentService paymentService;

    private ItemService itemService;

    private ContractRepo contractRepo;

    @Autowired
    public OfferService(ContractService contractService, OfferRepo offerRepo,
        ApiService apiService, IPaymentService paymentService,
        ItemService itemService, ContractRepo contractRepo) {
        this.contractService = contractService;
        this.offerRepo = offerRepo;
        this.apiService = apiService;
        this.paymentService = paymentService;
        this.itemService = itemService;
        this.contractRepo = contractRepo;
    }

    public void create(long itemId, User requester, LocalDateTime start, LocalDateTime end) {
        ItemRental itemRental = (ItemRental) itemService.findItem(itemId);
        validate(itemRental, requester, start, end);

        Offer offer = new Offer(itemRental, requester, start, end);
        itemRental.getOffers().add(offer);
        requester.getOffers().add(offer);
        offerRepo.save(offer);
    }

    public void validate(ItemRental itemRental, User requester, LocalDateTime start,
        LocalDateTime end) {
        OfferValidator.validate(itemRental, requester, start, end, paymentService, apiService);
        OfferValidator.periodIsAvailable(contractRepo, itemRental, start, end);
    }

    public List<Offer> getItemOffers(long itemId, User user, boolean onlyClosed) {
        if (itemService.userIsOwner(itemId, user.getId())) {
            if (!onlyClosed) {
                return offerRepo.findAllByItemRentalIdAndAcceptIsFalseAndDeclineIsFalse(itemId);
            } else {
                return offerRepo.findAllByItemRentalIdAndAcceptIsTrueOrDeclineIsTrue(itemId);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "This itemRental does not belong to you");
        }
    }

    public void acceptOffer(long offerId, User owner) {
        Offer offer = offerRepo.findOneById(offerId);
        validate(offer.getItemRental(), offer.getBorrower(), offer.getStart(), offer.getEnd());

        if (itemService.userIsOwner(offer.getItemRental().getId(), owner.getId())) {
            offer.setAccept(true);
            offerRepo.save(offer);
            removeOverlappingOffer(offer); // todo test this
            contractService.create(offer);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "This itemRental does not belong to you");
        }
    }

    void removeOverlappingOffer(Offer acceptedOffer) {
        ItemRental itemRental = acceptedOffer.getItemRental();
        List<Offer> itemOffers = offerRepo
            .findAllByItemRentalIdAndDeclineIsFalseAndAcceptIsFalse(itemRental.getId());
        for (Offer offer : itemOffers) {
            if (!(acceptedOffer.getStart().isAfter(offer.getEnd()) || acceptedOffer.getEnd()
                .isBefore(offer.getStart()))) {
                offer.setDecline(true);
                offerRepo.save(offer);
            }
        }
    }

    public void declineOffer(long offerId, User user) {
        Offer offer = offerRepo.findOneById(offerId);
        if (itemService.userIsOwner(offer.getItemRental().getId(), user.getId())) {
            offer.setDecline(true);
            offerRepo.save(offer);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "This itemRental does not belong to you");
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
            offerRepo.findAllByItemRentalIdAndAcceptIsFalseAndDeclineIsFalse(itemId);
        for (Offer offer :
            toBeDeleted) {
            offer.setDecline(true);
            offerRepo.save(offer);
        }
    }
}
