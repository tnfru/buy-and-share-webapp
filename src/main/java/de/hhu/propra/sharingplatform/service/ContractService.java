package de.hhu.propra.sharingplatform.service;

import de.hhu.propra.sharingplatform.dao.ContractRepo;
import de.hhu.propra.sharingplatform.model.Conflict;
import de.hhu.propra.sharingplatform.model.Contract;
import de.hhu.propra.sharingplatform.model.Offer;
import java.time.LocalDateTime;

import de.hhu.propra.sharingplatform.model.Status;
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

    public void returnItem(long contractId) {
        Contract contract = contractRepo.findOneById(contractId);
        LocalDateTime current = LocalDateTime.now();
        paymentService.transferPayment(contract);
        contract.setRealEnd(current);
        contractRepo.save(contract);
    }

    public void acceptReturn(long contractId) {
        Contract contract = contractRepo.findOneById(contractId);
        paymentService.freeBailReservation(contract);
    }

    public void punishBail(long contractId) {
        Contract contract = contractRepo.findOneById(contractId);
        paymentService.punishBailReservation(contract);
    }

    public void openConflict(long contractId) {
        Contract contract = contractRepo.findOneById(contractId);
        contract.setRealEnd(LocalDateTime.now());
        Conflict conflict = new Conflict();
        conflict.setStatus(Status.PENDING);
        contract.setConflict(conflict);
        contractRepo.save(contract);
    }

    public void calcPrice(long contractId) {
        Contract contract = contractRepo.findOneById(contractId);
        paymentService.create(contract);
    }
}
