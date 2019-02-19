package de.hhu.propra.sharingplatform.service.validation;

import de.hhu.propra.sharingplatform.dao.UserRepo;
import de.hhu.propra.sharingplatform.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static de.hhu.propra.sharingplatform.service.validation.Validator.validateName;


public class UserValidator {

    public static void validateUser(User user, UserRepo userRepo) {
        validateMail(user);
        validateName(user.getAddress(), "Invalid Address");
        validateName(user.getName(), "Invalid Name");

        validateName(user.getAccountName(), "Invalid Account name");
        if (userRepo.findByAccountName(user.getAccountName()).isPresent()) {
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

    //TODO is this correct?
    private static void validateMail(User user) {
        if (!Validator.matchesDbGuidlines(user.getEmail())
            || !Validator.isValidMail(user.getEmail())
            || !Validator.isPrintable(user.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid E-Mail");
        }
    }
}
