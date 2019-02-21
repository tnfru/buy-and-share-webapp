package de.hhu.propra.sharingplatform.service.validation;

import de.hhu.propra.sharingplatform.dao.ContractRepo;
import de.hhu.propra.sharingplatform.model.Contract;
import de.hhu.propra.sharingplatform.model.Item;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.service.ApiService;
import de.hhu.propra.sharingplatform.service.PaymentService;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class OfferValidator {

    public static void validate(Item item, User requester, LocalDateTime start, LocalDateTime end,
        PaymentService paymentService, ApiService apiService) {
        double totalCost = paymentService.calculateTotalPrice(item, start, end) + item.getBail();

        if (start.until(end, ChronoUnit.DAYS) < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End date needs to be after"
                + " Start date");
        }
        if (!item.isAvailable()) { // todo available not required anymore
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item unavailable at given "
                + "time");
        }
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
        for (Contract contract : contracts) { // todo dont compare contracts already closed
            if (!(contract.getStart().isAfter(end) || contract.getExpectedEnd().isBefore(start))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid period");
            }
        }
        // todo case: real end shifts
    }
}
