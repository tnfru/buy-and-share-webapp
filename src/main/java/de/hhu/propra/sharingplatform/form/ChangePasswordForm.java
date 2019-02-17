package de.hhu.propra.sharingplatform.form;

import de.hhu.propra.sharingplatform.model.User;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Data
public class ChangePasswordForm {

    private String newPw;
    private String repeatPw;

    public void applyToUser(User user) {
        validatePasswords();
        user.setPassword(newPw);
    }

    private void validatePasswords() {
        if (newPw == null || newPw.length() == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password was empty");
        }
        if (newPw.length() > 255) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is too long");
        }
        if (newPw.length() < 8) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is too short");
        }
        if (!newPw.equals(repeatPw)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwords do not match");
        }
    }
}
