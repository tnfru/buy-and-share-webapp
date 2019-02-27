package de.hhu.propra.sharingplatform.service.validation;

import de.hhu.propra.sharingplatform.dao.OfferRepo;
import de.hhu.propra.sharingplatform.dao.contractdao.ContractRepo;
import de.hhu.propra.sharingplatform.model.items.Item;
import de.hhu.propra.sharingplatform.model.items.ItemRental;
import de.hhu.propra.sharingplatform.model.items.ItemSale;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static de.hhu.propra.sharingplatform.service.validation.Validator.validateName;

public class ItemValidator {

    public static void validateItemIsFree(OfferRepo offerRepo, ContractRepo contractRepo, Item item) {
        if (item instanceof ItemRental
            && (contractRepo.findAllByItemAndFinishedIsFalse((ItemRental) item).size() > 0
            || offerRepo.findAllByItemRentalIdAndAcceptIsFalseAndDeclineIsFalse(item.getId()).size() > 0)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "There are active offers/contracts for this item");
        }
    }

    public static void validateItem(Item item) {

        if (!Validator.matchesDbGuidelines(item.getDescription())
            || !Validator.isPrintable(item.getDescription())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Description");
        }
        if (item instanceof ItemRental) {
            if (((ItemRental) item).getBail() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Bail");
            }
            if (((ItemRental) item).getDailyRate() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Daily Rate");
            }
        } else {
            if (((ItemSale) item).getPrice() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Price");
            }
        }
        validateName(item.getLocation(), "Invalid Location");
        validateName(item.getName(), "Invalid ItemRental Name");
    }
}
