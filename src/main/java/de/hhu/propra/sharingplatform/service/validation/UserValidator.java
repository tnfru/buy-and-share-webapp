package de.hhu.propra.sharingplatform.service.validation;

import static de.hhu.propra.sharingplatform.service.validation.Validator.validateAccountName;
import static de.hhu.propra.sharingplatform.service.validation.Validator.validateAdress;
import static de.hhu.propra.sharingplatform.service.validation.Validator.validateName;

import de.hhu.propra.sharingplatform.dao.UserRepo;
import de.hhu.propra.sharingplatform.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;


public class UserValidator {

    public static void validateUser(User user, UserRepo userRepo) {
        validateMail(user);

        validateName(user.getName(), "Invalid Name");
        validateAdress(user.getAddress(), "Invalid Address");
        validateAccountName(user.getAccountName(), "Invalid Account name");
        if (userRepo.findByAccountName(user.getAccountName()).isPresent()
            && !userRepo.findByAccountName(user.getAccountName()).get().getId()
            .equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Account Name already exists");
        }
    }

    public static void validatePassword(String password) {
        if (password == null || password.length() == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password was empty");
        }
        if (password.length() > 255) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is too long");
        }
        if (password.length() < 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is too short");
        }
    }

    private static void validateMail(User user) {
        if (!Validator.matchesDbGuidelines(user.getEmail())
            || !Validator.isValidMail(user.getEmail())
            || !Validator.isPrintable(user.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid E-Mail");
        }
    }
}
