package de.hhu.propra.sharingplatform.service;

import de.hhu.propra.sharingplatform.model.Contract;
import de.hhu.propra.sharingplatform.model.Offer;
import de.hhu.propra.sharingplatform.modelDAO.ContractRepo;
import java.util.Date;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Data
public class ContractService {

    final
    ContractRepo contractRepo;

    final
    PaymentService paymentService;

    @Autowired
    public ContractService(ContractRepo contractRepo, PaymentService paymentService) {
        this.contractRepo = contractRepo;
        this.paymentService = paymentService;
    }

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
        paymentService.create(contract);
    }
}
