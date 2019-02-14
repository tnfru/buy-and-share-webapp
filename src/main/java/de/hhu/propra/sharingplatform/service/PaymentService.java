package de.hhu.propra.sharingplatform.service;

import de.hhu.propra.sharingplatform.model.Contract;
import de.hhu.propra.sharingplatform.model.Payment;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.dao.PaymentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepo paymentRepo;

    public void create(User from, User to, long amount) {
        Payment payment = new Payment(from, to, amount);
        paymentRepo.save(payment);
    }

    public void create(Contract contract) {
        long startTime = contract.getStart().getTime();
        long endTime = contract.getRealEnd().getTime();

        long timePassed = (endTime - startTime) / (1000 * 60 * 60 * 24);
        User from = contract.getBorrower();
        User to = contract.getItem().getOwner();
        Payment payment = new Payment(from, to, timePassed * contract.getItem().getPrice());
        paymentRepo.save(payment);
    }

}
