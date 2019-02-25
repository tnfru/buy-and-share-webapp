package de.hhu.propra.sharingplatform.service.payment;

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
        int totalPrice = calculateTotalExpectedPrice(contract);
        User sender = contract.getBorrower();
        User recipient = contract.getItem().getOwner();
        Payment payment = new Payment(sender, recipient, totalPrice, contract.getItem().getBail());

        long id = apiService.reserveMoney(payment.getProPayIdSender(), payment.getProPayIdRecipient(),
            payment.getBail());
        payment.setBailProPayId(id);
        id = apiService.reserveMoney(payment.getProPayIdSender(), payment.getProPayIdRecipient(),
            totalPrice);
        payment.setAmountProPayId(id);
        paymentRepo.save(payment);
        return payment;
    }

    private int calculateTotalExpectedPrice(Contract contract) {
        long timePassed = contract.getStart().until(contract.getExpectedEnd(), ChronoUnit.DAYS) + 1;
        return (int)Math.ceil(timePassed * contract.getItem().getPrice());
    }

    private int calculateTotalActualPrice(Contract contract) {
        long timePassed = contract.getStart().until(contract.getRealEnd(), ChronoUnit.DAYS) + 1;
        return (int)Math.ceil(timePassed * contract.getItem().getPrice());
    }

    @Override
    public boolean recipientSolvent(Contract contract) {
        int totalAmount = contract.getItem().getBail() + calculateTotalExpectedPrice(contract);
        int available = apiService.getAccountBalanceLiquid(contract.getBorrower().getPropayId());
        return available >= totalAmount;
    }

    @Override
    public void transferPayment(Contract contract) {
        Payment paymentInfo = contract.getPayment();
        apiService.freeReservation(paymentInfo.getAmountProPayId(),
            paymentInfo.getProPayIdSender());
        int amount = calculateTotalActualPrice(contract);
        apiService.transferMoney(amount, paymentInfo.getProPayIdSender(),
            paymentInfo.getProPayIdRecipient());
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
