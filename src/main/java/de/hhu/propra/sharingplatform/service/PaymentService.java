package de.hhu.propra.sharingplatform.service;

import de.hhu.propra.sharingplatform.dao.PaymentRepo;
import de.hhu.propra.sharingplatform.model.Contract;
import de.hhu.propra.sharingplatform.model.Payment;
import de.hhu.propra.sharingplatform.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private final PaymentRepo paymentRepo;
    private final ApiService apiService;

    @Autowired
    public PaymentService(PaymentRepo paymentRepo, ApiService apiService) {
        this.paymentRepo = paymentRepo;
        this.apiService = apiService;
    }

    public void create(User sender, User recipient, long amount, long bail) {
        Payment payment = new Payment(sender, recipient, amount, bail);
        paymentRepo.save(payment);
    }

    public void create(Contract contract) {
        double totalPrice = calculateTotalPrice(contract);
        User sender = contract.getBorrower();
        User recipient = contract.getItem().getOwner();
        Payment payment = new Payment(sender, recipient, totalPrice, contract.getItem().getBail());
        payment.setContract(contract);
        paymentRepo.save(payment);
        apiService.enforcePayment(payment);
    }

    double calculateTotalPrice(Contract contract) {
        long startTime = contract.getStart().getTime();
        long endTime = contract.getRealEnd().getTime();
        //time passed in full days
        double timePassed = Math.ceil(((double) endTime - startTime) / (1000.0 * 60 * 60 * 24));
        return timePassed * contract.getItem().getPrice();
    }

    public boolean recipientSolvent(Contract contract) {
        double totalAmount = contract.getItem().getBail() + calculateTotalPrice(contract);
        return apiService.checkSolvent(contract.getBorrower(), totalAmount);
    }
}
