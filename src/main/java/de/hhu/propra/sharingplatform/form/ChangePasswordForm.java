package de.hhu.propra.sharingplatform.form;

import de.hhu.propra.sharingplatform.model.User;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Data
public class ChangePasswordForm {

    private String newPW;
    private String repeatPW;

    public void applyToUser(User user) {
        validatePasswords();
        user.setPassword(newPW);
    }

    private void validatePasswords() {
        if (newPW == null || newPW.length() == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password was empty");
        }
        if (newPW.length() > 255) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is too long");
        }
        if (newPW.length() < 8) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is too short");
        }
        if (!newPW.equals(repeatPW)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwords do not match");
        }
    }
}
