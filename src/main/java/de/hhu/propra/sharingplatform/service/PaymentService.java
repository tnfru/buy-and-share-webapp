package de.hhu.propra.sharingplatform.service;

import de.hhu.propra.sharingplatform.dao.PaymentRepo;
import de.hhu.propra.sharingplatform.model.Contract;
import de.hhu.propra.sharingplatform.model.Item;
import de.hhu.propra.sharingplatform.model.Payment;
import de.hhu.propra.sharingplatform.model.User;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
        apiService.enforcePayment(payment, calculateTotalPrice(contract));
    }

    double calculateTotalPrice(Contract contract) {
        long timePassed = contract.getStart().until(contract.getRealEnd(), ChronoUnit.DAYS);
        return timePassed * contract.getItem().getPrice();
    }

    public double calculateTotalPrice(Item item, LocalDateTime start, LocalDateTime end) {
        long timePassed = start.until(end, ChronoUnit.DAYS);
        return timePassed * item.getPrice();
    }

    public boolean recipientSolvent(Contract contract) {
        double totalAmount = contract.getItem().getBail() + calculateTotalPrice(contract);
        return apiService.isSolvent(contract.getBorrower(), totalAmount);
    }
}
