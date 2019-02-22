package de.hhu.propra.sharingplatform.service.validation;

import de.hhu.propra.sharingplatform.dao.ContractRepo;
import de.hhu.propra.sharingplatform.model.Contract;
import de.hhu.propra.sharingplatform.model.Item;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.service.IPaymentApi;
import de.hhu.propra.sharingplatform.service.IPaymentService;
import de.hhu.propra.sharingplatform.service.ApiService;
import de.hhu.propra.sharingplatform.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import de.hhu.propra.sharingplatform.service.Payment.IPaymentApi;
import de.hhu.propra.sharingplatform.service.Payment.IPaymentService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
  @Override
    public void createAccount(String proPayId, int amount) {
        createAccountOrAddMoney(proPayId, amount);
    }  @Override
    public void createPayment(User sender, User recipient, int amount, int bail) {
        Payment payment = new Payment(sender, recipient, amount, bail);
        paymentRepo.save(payment);
    }
public class OfferValidator {

    public static void validate(Item item, User requester, LocalDateTime start, LocalDateTime end,
                                IPaymentService paymentService, IPaymentApi apiService) {

        if ((start.until(end, ChronoUnit.DAYS) + 1) < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End date needs to be after"
                + " Start date");
        }
        int totalCost = paymentService.calculateTotalPrice(item, start, end) + item.getBail();
        if (!(apiService.isSolvent(requester, totalCost))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough money");
        }
        if (requester.isBan()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account currently "
                + "suspended");
        }
    }

    public static void periodIsAvailable(ContractRepo contractRepo, Item item, LocalDateTime start,
                                         LocalDateTime end) {
        List<Contract> contracts = contractRepo.findAllByItem(item);
        for (Contract contract : contracts) {
            if (contract.isFinished()) {
                continue;
            }
            if (!(contract.getStart().isAfter(end) || contract.getExpectedEnd().isBefore(start))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid period");
            }
        }
        // todo case: real end shifts
    }
}
