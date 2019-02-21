package de.hhu.propra.sharingplatform.service.validation;

import de.hhu.propra.sharingplatform.model.Item;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.service.ApiService;
import de.hhu.propra.sharingplatform.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class OfferValidator {

    public static void validate(Item item, User requester, LocalDateTime start, LocalDateTime end,
                                PaymentService paymentService, ApiService apiService) {
        double totalCost = paymentService.calculateTotalPrice(item, start, end) + item.getBail();

        if (start.until(end, ChronoUnit.DAYS) < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End date needs to be after"
                + " Start date");
        }
        if (!item.isAvailable()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item unavailable at given "
                + "time");
        }
        if (!(apiService.isSolventFake(requester, totalCost))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough money");
        }
        if (requester.isBan()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account currently "
                + "suspended");
        }
    }
}
