package de.hhu.propra.sharingplatform.service;

import de.hhu.propra.sharingplatform.dao.ConflictRepo;
import de.hhu.propra.sharingplatform.dto.Status;
import de.hhu.propra.sharingplatform.model.Conflict;
import de.hhu.propra.sharingplatform.model.contracts.BorrowContract;
import de.hhu.propra.sharingplatform.model.contracts.Contract;
import de.hhu.propra.sharingplatform.service.payment.IPaymentService;
import java.util.ArrayList;
import java.util.Collection;
import org.springframework.stereotype.Service;

@Service
public class ConflictService {

    private ConflictRepo conflictRepo;
    private IPaymentService paymentService;
    private UserService userService;

    public ConflictService(ConflictRepo conflictRepo, IPaymentService paymentService,
                           UserService userService) {
        this.conflictRepo = conflictRepo;
        this.paymentService = paymentService;
        this.userService = userService;
    }

    public Collection<Conflict> getOpenConflicts() {
        return conflictRepo.findAllByStatus(Status.PENDING);
    }

    Conflict createConflict(Contract contract, String accountName, String description) {
        Conflict conflict = new Conflict();
        conflict.setStatus(Status.PENDING);
        conflict.setContract(contract);
        conflict.setRequester(userService.fetchUserByAccountName(accountName));
        conflict.setDescription(description);
        conflictRepo.save(conflict);
        return conflict;
    }

    public Conflict fetchConflictById(long conflictId) {
        return conflictRepo.findOneById(conflictId);
    }

    public void punish(long conflictId) {
        setStatus(Status.PUNISHED_BAIL, conflictId);
        Conflict conflict = conflictRepo.findOneById(conflictId);
        paymentService.punishBailReservation((BorrowContract) conflict.getContract());
    }

    public void setStatus(Status canceled, long conflictId) {
        Conflict conflict = conflictRepo.findOneById(conflictId);
        conflict.setStatus(canceled);
        conflictRepo.save(conflict);
    }
}
