package de.hhu.propra.sharingplatform.service;

import de.hhu.propra.sharingplatform.model.Offer;
import de.hhu.propra.sharingplatform.dao.OfferRepo;
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
