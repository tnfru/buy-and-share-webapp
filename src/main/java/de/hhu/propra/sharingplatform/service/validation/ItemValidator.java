package de.hhu.propra.sharingplatform.service.validation;

import de.hhu.propra.sharingplatform.model.ItemRental;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static de.hhu.propra.sharingplatform.service.validation.Validator.validateName;

public class ItemValidator {
    public static void validateItem(ItemRental itemRental) {

        if (!Validator.matchesDbGuidelines(itemRental.getDescription())
            || !Validator.isPrintable(itemRental.getDescription())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Description");
        }
        if (itemRental.getBail() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Bail");
        }
        if (itemRental.getDailyRate() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Price");
        }
        validateName(itemRental.getLocation(), "Invalid Location");
        validateName(itemRental.getName(), "Invalid ItemRental Name");
    }
}
