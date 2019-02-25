package de.hhu.propra.sharingplatform.service;

import de.hhu.propra.sharingplatform.dao.ContractRepo;
import de.hhu.propra.sharingplatform.model.Contract;
import de.hhu.propra.sharingplatform.model.Offer;
import de.hhu.propra.sharingplatform.service.payment.IPaymentService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Collection;

@Service
@Data
public class ContractService {

    final ContractRepo contractRepo;

    final IPaymentService paymentService;

    private ConflictService conflictService;

    @Autowired
    public ContractService(ContractRepo contractRepo, IPaymentService paymentService,
                           ConflictService conflictService) {
        this.contractRepo = contractRepo;
        this.paymentService = paymentService;
        this.conflictService = conflictService;
    }

    public void create(Offer offer) {
        Contract contract = new Contract(offer);
        // -> payment
        contract.setPayment(paymentService.createPayment(contract));
        contractRepo.save(contract);
    }

    public void returnItem(long contractId, String accountName) {
        Contract contract = contractRepo.findOneById(contractId);
        if (!userIsBorrower(contract, accountName)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "This contract does not involve you");
        }
        LocalDateTime current = LocalDateTime.now();
        contract.setRealEnd(current);
        paymentService.transferPayment(contract);
        contractRepo.save(contract);
    }

    public void acceptReturn(long contractId, String accountName) {
        Contract contract = contractRepo.findOneById(contractId);
        if (!userIsContractOwner(contract, accountName)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "This contract does not involve you");
        }
        paymentService.freeBailReservation(contract);
        contract.setFinished(true);
        contractRepo.save(contract);
    }


    public void openConflict(String description, String accountName, long contractId) {
        Contract contract = contractRepo.findOneById(contractId);
        contract.setConflict(conflictService.createConflict(contract, accountName, description));
        contractRepo.save(contract);
    }

    void calcPrice(long contractId) {
        Contract contract = contractRepo.findOneById(contractId);
        paymentService.createPayment(contract);
    }

    private boolean userIsBorrower(Contract contract, String accountName) {
        return contract.getBorrower().getAccountName().equals(accountName);
    }

    private boolean userIsContractOwner(Contract contract, String userName) {
        return contract.getItem().getOwner().getAccountName().equals(userName)
            || contract.getBorrower().getAccountName().equals(userName);
    }

    public Collection<Contract> getContractsWithOpenConflicts() {
        return conflictService.getAllContractsWithOpenConflict();
    }

    public void validateOwner(long contractId, String accountName) {
        Contract contract = contractRepo.findOneById(contractId);
        if(!userIsContractOwner(contract, accountName)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "This contract does not involve you");
        }
    }

    public void cancelContract(long conflictId) {
        Contract contract = conflictService.fetchConflictById(conflictId).getContract();
        contract.setFinished(true);
        contractRepo.save(contract);
    }
}
