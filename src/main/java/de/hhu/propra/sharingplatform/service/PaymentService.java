package de.hhu.propra.sharingplatform.service;

import de.hhu.propra.sharingplatform.dao.PaymentRepo;
import de.hhu.propra.sharingplatform.model.Contract;
import de.hhu.propra.sharingplatform.model.Item;
import de.hhu.propra.sharingplatform.model.Payment;
import de.hhu.propra.sharingplatform.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class PaymentService {

    private final PaymentRepo paymentRepo;
    private final ApiService apiService;

    @Autowired
    public PaymentService(PaymentRepo paymentRepo, ApiService apiService) {
        this.paymentRepo = paymentRepo;
        this.apiService = apiService;
    }

    public void create(User sender, User recipient, int amount, int bail) {
        Payment payment = new Payment(sender, recipient, amount, bail);
        paymentRepo.save(payment);
    }

    public Payment create(Contract contract) {
        int totalPrice = calculateTotalPrice(contract);
        User sender = contract.getBorrower();
        User recipient = contract.getItem().getOwner();
        Payment payment = new Payment(sender, recipient, totalPrice, contract.getItem().getBail());
        payment.setContract(contract);
        paymentRepo.save(payment);
        apiService.enforcePayment(payment, calculateTotalPrice(contract));
        return payment;
    }

    //plus 1 because the last day is not included in the until since it is still ongoing
    int calculateTotalPrice(Contract contract) {
        long timePassed = contract.getStart().until(contract.getExpectedEnd(), ChronoUnit.DAYS) + 1;
        return (int)Math.ceil(timePassed * contract.getItem().getPrice());
    }

    //See comment above
    //TODO: Maybe calculate by hours and only count a day after 12 hours+
    public int calculateTotalPrice(Item item, LocalDateTime start, LocalDateTime end) {
        long timePassed = start.until(end, ChronoUnit.DAYS) + 1;
        return (int)Math.ceil(Math.max(timePassed * item.getPrice(), 0));
    }

    public boolean recipientSolvent(Contract contract) {
        int totalAmount = contract.getItem().getBail() + calculateTotalPrice(contract);
        return apiService.isSolvent(contract.getBorrower(), totalAmount);
    }

    public void transferPayment(Contract contract) {
        Payment paymentInfo = contract.getPayment();
        apiService.freeReservation(paymentInfo.getAmountProPayId(),
            paymentInfo.getProPayIdSender());
        paymentInfo.setAmount(calculateTotalPrice(contract.getItem(), contract.getStart(),
            contract.getRealEnd()));
        apiService.transferMoney(paymentInfo);
    }

    public void freeBailReservation(Contract contract) {
        Payment paymentInfo = contract.getPayment();
        apiService.freeReservation(paymentInfo.getBailProPayId(), paymentInfo.getProPayIdSender());
    }

    public void punishBailReservation(Contract contract) {
        Payment paymentInfo = contract.getPayment();
        apiService.punishReservation(paymentInfo.getBailProPayId(),
            paymentInfo.getProPayIdSender());
    }
}
