package de.hhu.propra.sharingplatform.service.Payment;

import de.hhu.propra.sharingplatform.dao.PaymentRepo;
import de.hhu.propra.sharingplatform.model.Contract;
import de.hhu.propra.sharingplatform.model.Item;
import de.hhu.propra.sharingplatform.model.Payment;
import de.hhu.propra.sharingplatform.model.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class PaymentService implements IPaymentService {

    private final PaymentRepo paymentRepo;
    private final IPaymentApi apiService;

    public PaymentService(PaymentRepo paymentRepo, IPaymentApi apiService) {
        this.paymentRepo = paymentRepo;
        this.apiService = apiService;
    }

    @Override
    public Payment createPayment(Contract contract) {
        int totalPrice = calculateTotalPrice(contract);
        User sender = contract.getBorrower();
        User recipient = contract.getItem().getOwner();
        Payment payment = new Payment(sender, recipient, totalPrice, contract.getItem().getBail());
        payment.setContract(contract);
        paymentRepo.save(payment);
        apiService.enforcePayment(payment, calculateTotalPrice(contract));
        return payment;
    }

    int calculateTotalPrice(Contract contract) {
        long timePassed = contract.getStart().until(contract.getExpectedEnd(), ChronoUnit.DAYS) + 1;
        return (int)Math.ceil(timePassed * contract.getItem().getPrice());
    }

    @Override
    public int calculateTotalPrice(Item item, LocalDateTime start, LocalDateTime end) {
        long timePassed = start.until(end, ChronoUnit.DAYS) + 1;
        return (int)Math.ceil(Math.max(timePassed * item.getPrice(), 0));
    }

    @Override
    public boolean recipientSolvent(Contract contract) {
        int totalAmount = contract.getItem().getBail() + calculateTotalPrice(contract);
        return apiService.isSolvent(contract.getBorrower(), totalAmount);
    }

    @Override
    public void transferPayment(Contract contract) {
        Payment paymentInfo = contract.getPayment();
        apiService.freeReservation(paymentInfo.getAmountProPayId(),
            paymentInfo.getProPayIdSender());
        paymentInfo.setAmount(calculateTotalPrice(contract.getItem(), contract.getStart(),
            contract.getRealEnd()));
        apiService.transferMoney(paymentInfo);
    }

    @Override
    public void freeBailReservation(Contract contract) {
        Payment paymentInfo = contract.getPayment();
        apiService.freeReservation(paymentInfo.getBailProPayId(), paymentInfo.getProPayIdSender());
    }

    @Override
    public void punishBailReservation(Contract contract) {
        Payment paymentInfo = contract.getPayment();
        apiService.punishReservation(paymentInfo.getBailProPayId(),
            paymentInfo.getProPayIdSender());
    }
}
