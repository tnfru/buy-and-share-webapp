package de.hhu.propra.sharingplatform.service;

import de.hhu.propra.sharingplatform.model.Contract;
import de.hhu.propra.sharingplatform.model.Offer;
import org.springframework.stereotype.Service;

@Service
public class ContractService {

    public Contract offerAccepted(Offer offer) {
        return offer.isAccept() ? new Contract(offer) : null;
    }

}
