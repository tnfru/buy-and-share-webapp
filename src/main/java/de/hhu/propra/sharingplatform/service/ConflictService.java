package de.hhu.propra.sharingplatform.service;

import de.hhu.propra.sharingplatform.dao.ConflictRepo;
import de.hhu.propra.sharingplatform.dto.Status;
import de.hhu.propra.sharingplatform.model.Conflict;
import de.hhu.propra.sharingplatform.model.Contract;
import de.hhu.propra.sharingplatform.service.payment.IPaymentService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class ConflictService {

    private ConflictRepo conflictRepo;
    private IPaymentService paymentService;

    public ConflictService(ConflictRepo conflictRepo, IPaymentService paymentService) {
        this.conflictRepo = conflictRepo;
        this.paymentService = paymentService;
    }

    public Collection<Conflict> getOpenConflicts() {
        return conflictRepo.findAllByStatus(Status.PENDING);
    }

    public Contract resolveOwnerConflict(boolean accepted, long conflictId) {
        Conflict conflict = conflictRepo.findOneById(conflictId);
        if (accepted) {
            conflict.setStatus(Status.ACCEPTED);
            paymentService.punishBailReservation(conflict.getContract());
        } else {
            conflict.setStatus(Status.REJECTED);
            paymentService.freeBailReservation(conflict.getContract());
        }
        conflictRepo.save(conflict);
        return conflict.getContract();
    }

    public ArrayList<Contract> getAllContractsWithOpenConflict() {
        Collection<Conflict> conflictsPending = conflictRepo.findAllByStatus(Status.PENDING);
        ArrayList<Contract> contractsWithOpenConflict = new ArrayList<>();
        for (Conflict conflict : conflictsPending) {
            contractsWithOpenConflict.add(conflict.getContract());
        }
        return contractsWithOpenConflict;
    }


    public Conflict createConflict(Contract contract) {
        Conflict conflict = new Conflict();
        conflict.setStatus(Status.PENDING);
        conflict.setContract(contract);
        conflictRepo.save(conflict);
        return conflict;
    }
}
