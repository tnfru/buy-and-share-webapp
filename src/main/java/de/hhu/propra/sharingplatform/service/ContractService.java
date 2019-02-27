package de.hhu.propra.sharingplatform.service;

import de.hhu.propra.sharingplatform.dao.contractdao.BorrowContractRepo;
import de.hhu.propra.sharingplatform.dao.contractdao.SellContractRepo;
import de.hhu.propra.sharingplatform.dto.Status;
import de.hhu.propra.sharingplatform.model.Conflict;
import de.hhu.propra.sharingplatform.model.Offer;
import de.hhu.propra.sharingplatform.model.contracts.BorrowContract;
import de.hhu.propra.sharingplatform.model.contracts.Contract;
import de.hhu.propra.sharingplatform.model.contracts.SellContract;
import de.hhu.propra.sharingplatform.service.payment.IPaymentService;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@Data
public class ContractService {

    final BorrowContractRepo borrowContractRepo;

    final SellContractRepo sellContractRepo;

    final IPaymentService paymentService;

    private ConflictService conflictService;

    @Autowired
    public ContractService(BorrowContractRepo borrowContractRepo, SellContractRepo sellContractRepo,
        IPaymentService paymentService, ConflictService conflictService) {
        this.borrowContractRepo = borrowContractRepo;
        this.sellContractRepo = sellContractRepo;
        this.paymentService = paymentService;
        this.conflictService = conflictService;
    }

    public void create(Offer offer) {
        BorrowContract contract = new BorrowContract(offer);
        // -> payment
        paymentService.createPayment(contract);
        borrowContractRepo.save(contract);
    }

    public void returnItem(long contractId, String accountName) {
        BorrowContract contract = borrowContractRepo.findOneById(contractId);
        if (!userIsBorrower(contract, accountName)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "This contract does not involve you");
        }
        LocalDateTime current = LocalDateTime.now();
        contract.setRealEnd(current);
        paymentService.transferPayment(contract);
        borrowContractRepo.save(contract);
    }

    public void acceptReturn(long contractId, String accountName) {
        BorrowContract contract = borrowContractRepo.findOneById(contractId);
        if (!(userIsContractOwner(contract, accountName) || accountName.equals("admin"))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "This contract does not involve you");
        }
        paymentService.freeBailReservation(contract);
        contract.setFinished(true);
        borrowContractRepo.save(contract);
    }


    public void openConflict(String description, String accountName, long contractId) {
        BorrowContract contract = borrowContractRepo.findOneById(contractId);
        List<Conflict> conflicts = contract.getConflicts();
        for (Conflict conflict : conflicts) {
            if (conflict.getStatus().equals(Status.PENDING)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "There can only be one open conflict at a time");
            }
        }
        conflicts.add(conflictService.createConflict(contract, accountName, description));
        contract.setConflicts(conflicts);
        contract.setRealEnd(LocalDateTime.now());
        borrowContractRepo.save(contract);
    }

    void calcPrice(long contractId) {
        BorrowContract contract = borrowContractRepo.findOneById(contractId);
        paymentService.createPayment(contract);
    }

    private boolean userIsBorrower(BorrowContract contract, String accountName) {
        return contract.getBorrower().getAccountName().equals(accountName);
    }

    private boolean userIsContractOwner(BorrowContract contract, String userName) {
        return contract.getItem().getOwner().getAccountName().equals(userName)
            || contract.getBorrower().getAccountName().equals(userName);
    }

    public Collection<Contract> getContractsWithOpenConflicts() {
        return conflictService.getAllContractsWithOpenConflict();
    }

    public void validateOwner(long contractId, String accountName) {
        BorrowContract contract = borrowContractRepo.findOneById(contractId);
        if (!userIsContractOwner(contract, accountName)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "This contract does not involve you");
        }
    }

    public void cancelContract(long conflictId) {
        BorrowContract contract =
            (BorrowContract) conflictService.fetchConflictById(conflictId).getContract();
        paymentService.freeBailReservation(contract);
        paymentService.freeChargeReservation(contract);
        contract.setFinished(true);
        borrowContractRepo.save(contract);
    }

    public void continueContract(long conflictId) {
        BorrowContract contract =
            (BorrowContract) conflictService.fetchConflictById(conflictId).getContract();
        contract.setRealEnd(null);
        borrowContractRepo.save(contract);
    }

    public void endContract(long conflictId) {
        BorrowContract contract =
            (BorrowContract) conflictService.fetchConflictById(conflictId).getContract();
        contract.setFinished(true);
    }

    public Contract fetchContractById(long contractId) {
        return borrowContractRepo.findOneById(contractId);
    }

    public BorrowContract fetchBorrowContractById(long contractId) {
        return borrowContractRepo.findOneById(contractId);
    }

    public SellContract fetchSellContractById(long contractId) {
        return sellContractRepo.findOneById(contractId);
    }
}
