package de.hhu.propra.sharingplatform.service;

import de.hhu.propra.sharingplatform.dao.ContractRepo;
import de.hhu.propra.sharingplatform.model.Contract;
import de.hhu.propra.sharingplatform.model.Offer;
import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Data
public class ContractService {

    final ContractRepo contractRepo;

    final PaymentService paymentService;

    @Autowired
    public ContractService(ContractRepo contractRepo, PaymentService paymentService) {
        this.contractRepo = contractRepo;
        this.paymentService = paymentService;
    }

    public void create(Offer offer) {
        Contract contract = new Contract(offer);
        if (paymentService.recipientSolvent(contract)) {
            contractRepo.save(contract);
            // -> Payment
            paymentService.create(contract);
        } else {
            //TODO
        }
    }

    public void endContract(long contractId) {
        Contract contract = contractRepo.findOneById(contractId);
        LocalDateTime current = LocalDateTime.now();

        contract.setRealEnd(current);
        contractRepo.save(contract);
    }

    public void calcPrice(long contractId) {
        Contract contract = contractRepo.findOneById(contractId);
        paymentService.create(contract);
    }
}
