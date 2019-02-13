package de.hhu.propra.sharingplatform.service;

import de.hhu.propra.sharingplatform.model.Contract;
import de.hhu.propra.sharingplatform.model.Offer;
import de.hhu.propra.sharingplatform.model.Payment;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.modelDAO.ContractRepo;
import de.hhu.propra.sharingplatform.modelDAO.PaymentRepo;
import java.util.Date;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Data
public class ContractService {

    @Autowired
    ContractRepo contractRepo;

    @Autowired
    PaymentRepo paymentRepo;

    public void create(Offer offer) {
        contractRepo.save(new Contract(offer));
    }

    public void endContract(long contractId) {
        Contract contract = contractRepo.findOneById(contractId);
        Date current = new Date();

        contract.setRealEnd(current);
        contractRepo.save(contract);
    }

    public void calcPrice(long contractId) {
        Contract contract = contractRepo.findOneById(contractId);

        long startTime = contract.getStart().getTime();
        long endTime = contract.getRealEnd().getTime();

        long timePassed = (endTime - startTime) / (1000 * 60 * 60 * 24);
        User from = contract.getBorrower();
        User to = contract.getItem().getOwner();
        Payment payment = new Payment(from, to, timePassed * contract.getItem().getPrice());
        paymentRepo.save(payment);
    }
}
