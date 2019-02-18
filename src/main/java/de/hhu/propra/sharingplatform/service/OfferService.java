package de.hhu.propra.sharingplatform.service;

import de.hhu.propra.sharingplatform.dao.OfferRepo;
import de.hhu.propra.sharingplatform.model.Item;
import de.hhu.propra.sharingplatform.model.Offer;
import de.hhu.propra.sharingplatform.model.User;
import java.util.Date;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Data
@Service
public class OfferService {

    final OfferRepo offerRepo;

    final ContractService contractService;

    @Autowired
    public OfferService(ContractService contractService, OfferRepo offerRepo) {
        this.contractService = contractService;
        this.offerRepo = offerRepo;
    }

    public void create(Item item, User requester) {
        Date start = new Date();
        Date end = new Date();

        // todo item is rented for 7 days now
        end.setTime(start.getTime() + 7 * (1000 * 60 * 60 * 24));
        Offer offer = new Offer(item, requester, start, end);
        item.getOffers().add(offer);
        requester.getOffers().add(offer);
        offerRepo.save(offer);
    }

    public void accept(long id) {
        Offer offer = offerRepo.findOneById(id);
        offer.setAccept(true);
        offerRepo.save(offer);
        contractService.create(offer);
    }

    public void decline(long id) {
        Offer offer = offerRepo.findOneById(id);
        offer.setDecline(true);
        offerRepo.save(offer);
    }
}
