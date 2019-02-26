package de.hhu.propra.sharingplatform.service.validation;

import de.hhu.propra.sharingplatform.dao.ContractRepo;
import de.hhu.propra.sharingplatform.model.Contract;
import de.hhu.propra.sharingplatform.model.ItemRental;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.service.payment.IPaymentApi;
import de.hhu.propra.sharingplatform.service.payment.IPaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class OfferValidator {

    public static void validate(ItemRental itemRental, User requester, LocalDateTime start, LocalDateTime end,
                                IPaymentService paymentService, IPaymentApi apiService) {

        if ((start.until(end, ChronoUnit.DAYS) + 1) < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End date needs to be after"
                + " Start date");
        }
        int totalCost = (int)Math.ceil((start.until(end, ChronoUnit.DAYS) + 1) * itemRental.getDailyRate());
        int available = apiService.getAccountBalanceLiquid(requester.getPropayId());
        if (totalCost > available) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough money");
        }
        if (requester.isBan()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account currently "
                + "suspended");
        }
    }

    public static void periodIsAvailable(ContractRepo contractRepo, ItemRental itemRental, LocalDateTime start,
                                         LocalDateTime end) {
        List<Contract> contracts = contractRepo.findAllByItemRentalAndFinishedIsFalse(itemRental);
        for (Contract contract : contracts) {
            if (!(contract.getStart().isAfter(end) || contract.getExpectedEnd().isBefore(start))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid period");
            }
        }
        // todo case: real end shifts
    }
}
