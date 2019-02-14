package de.hhu.propra.sharingplatform.form;

import static org.apache.commons.lang3.StringUtils.isAlphanumeric;

import de.hhu.propra.sharingplatform.model.User;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Data
public class UserForm {

    private String name;
    private String address;
    private String email;
    private String propayId;
    private String password;
    private String passwordConfirm;

    public User parseToUser() {
        if (!validateUserForm()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cant validate formdata!");
        }
        if(! password.equals(passwordConfirm)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwords did not match!");
        }
        if(password.length() < 8){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is too short!");
        }
        if(propayId == null) {
            propayId = "propay-" + email;
        }
        User user = new User();
        user.setName(name);
        user.setAddress(address);
        user.setEmail(email);
        user.setPropayId(propayId);
        user.setPassword(password);
        return user;
    }

    private boolean validateUserForm() {
        if (name == null || address == null || email == null || password == null) {
            return false;
        }
        if (name.length() == 0 || name.length() > 255 || !isAlphanumeric(name)) return false;
        if (address.length() == 0 || address.length() > 255) return false;
        if (email.length() == 0 || email.length() > 255 || !validateMail()) return false;
        if (password.length() == 0 || password.length() > 255) return false;
        return true;
    }

    private boolean validateMail() {
        Pattern pattern = Pattern.compile("^.+@.+\\..+$");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
