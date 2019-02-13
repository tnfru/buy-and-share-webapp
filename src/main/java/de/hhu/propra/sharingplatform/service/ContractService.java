package de.hhu.propra.sharingplatform.service;

import de.hhu.propra.sharingplatform.model.Contract;
import de.hhu.propra.sharingplatform.model.Offer;
import de.hhu.propra.sharingplatform.modelDAO.ContractRepo;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Data
public class ContractService {

    @Autowired
    ContractRepo contractRepo;

    public void create(Offer offer) {
        contractRepo.save(new Contract(offer));
    }

}
