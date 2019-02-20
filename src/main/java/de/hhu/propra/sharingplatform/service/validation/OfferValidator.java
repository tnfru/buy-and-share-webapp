package de.hhu.propra.sharingplatform.service.validation;

import de.hhu.propra.sharingplatform.model.Item;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.service.ApiService;
import de.hhu.propra.sharingplatform.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;

public class OfferValidator {

    public static void validate(Item item, User requester, Date start, Date end,
                                PaymentService paymentService, ApiService apiService) {
        //TODO dates valid?
        long millisecondsInDay = 1000 * 60 * 60 * 24;
        double totalCost = paymentService.calculateTotalPrice(item, start, end) + item.getBail();

        if ((end.getTime() - start.getTime()) / millisecondsInDay < 1) {
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
