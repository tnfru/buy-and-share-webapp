package de.hhu.propra.sharingplatform.service;

import de.hhu.propra.sharingplatform.dao.ConflictRepo;
import de.hhu.propra.sharingplatform.dao.ContractRepo;
import de.hhu.propra.sharingplatform.dto.Status;
import de.hhu.propra.sharingplatform.model.Conflict;
import de.hhu.propra.sharingplatform.model.Contract;
import de.hhu.propra.sharingplatform.model.Offer;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@Data
public class ContractService {

    final ContractRepo contractRepo;

    final PaymentService paymentService;

    final ConflictRepo conflictRepo;

    @Autowired
    public ContractService(ContractRepo contractRepo, PaymentService paymentService,
                           ConflictRepo conflictRepo) {
        this.contractRepo = contractRepo;
        this.paymentService = paymentService;
        this.conflictRepo = conflictRepo;
    }

    public void create(Offer offer) {
        Contract contract = new Contract(offer);
        // -> Payment
        contract.setPayment(paymentService.create(contract));
        contractRepo.save(contract);
    }

    public void returnItem(long contractId, String accountName) {
        Contract contract = contractRepo.findOneById(contractId);
        if (!userIsBorrower(contract, accountName)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "This contract does not involve you");
        }
        LocalDateTime current = LocalDateTime.now();
        paymentService.transferPayment(contract);
        contract.setRealEnd(current);
        contractRepo.save(contract);
    }

    public void acceptReturn(long contractId, String accountName) {
        Contract contract = contractRepo.findOneById(contractId);
        if (userIsContractOwner(contract, accountName)) {
            paymentService.freeBailReservation(contract);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "This contract does not involve you");
        }
    }

    public void punishBail(long contractId) {
        Contract contract = contractRepo.findOneById(contractId);
        paymentService.punishBailReservation(contract);
    }

    public void openConflict(long contractId, String accountName) {
        Contract contract = contractRepo.findOneById(contractId);
        if (userIsContractOwner(contract, accountName)) {
            contract.setRealEnd(LocalDateTime.now());
            Conflict conflict = new Conflict();
            conflict.setStatus(Status.PENDING);
            conflictRepo.save(conflict);
            contract.setConflict(conflict);
            contractRepo.save(contract);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "This contract does not involve you");
        }
    }

    public void calcPrice(long contractId) {
        Contract contract = contractRepo.findOneById(contractId);
        paymentService.create(contract);
    }

    private boolean userIsBorrower(Contract contract, String accountName) {
        return contract.getBorrower().getAccountName().equals(accountName);
    }

    public boolean userIsContractOwner(Contract contract, String userName) {
        return contract.getItem().getOwner().getAccountName().equals(userName);
    }
}
