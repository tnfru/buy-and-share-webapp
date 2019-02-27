package de.hhu.propra.sharingplatform.service.validation;

import de.hhu.propra.sharingplatform.dao.contractdao.BorrowContractRepo;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.model.contracts.BorrowContract;
import de.hhu.propra.sharingplatform.model.items.ItemRental;
import de.hhu.propra.sharingplatform.service.payment.IPaymentApi;
import de.hhu.propra.sharingplatform.service.payment.IPaymentService;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class OfferValidator {

    public static void validate(ItemRental itemRental, User requester, LocalDateTime start,
        LocalDateTime end,
        IPaymentService paymentService, IPaymentApi apiService) {

        if (end.isBefore(start)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End date needs to be after"
                + " Start date");
        }
        int totalCost = (int) Math
            .ceil((start.until(end, ChronoUnit.DAYS) + 1) * itemRental.getDailyRate()
                + itemRental.getBail());
        int available = apiService.getAccountBalanceLiquid(requester.getPropayId());
        if (totalCost > available) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough money");
        }
        if (requester.isBan()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account currently "
                + "suspended");
        }
    }

    public static void periodIsAvailable(BorrowContractRepo contractRepo, ItemRental itemRental,
        LocalDateTime start, LocalDateTime end) {
        List<BorrowContract> contracts = contractRepo.findAllByItemAndFinishedIsFalse(itemRental);
        for (BorrowContract contract : contracts) {
            if (!(contract.getStart().isAfter(end) || contract.getExpectedEnd().isBefore(start))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid period");
            }
        }
    }
}
