package de.hhu.propra.sharingplatform.service.validation;

import static de.hhu.propra.sharingplatform.service.validation.Validator.validateName;

import de.hhu.propra.sharingplatform.model.items.Item;
import de.hhu.propra.sharingplatform.model.items.ItemRental;
import de.hhu.propra.sharingplatform.model.items.ItemSale;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ItemValidator {

    public static void validateItem(Item item) {

        if (!Validator.matchesDbGuidelines(item.getDescription())
            || !Validator.isPrintable(item.getDescription())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Description");
        }
        if (item.getClass().toString().contains("ItemRental")) {
            if (((ItemRental) item).getBail() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Bail");
            }
            if (((ItemRental) item).getDailyRate() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Price");
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
