package de.hhu.propra.sharingplatform.form;

import de.hhu.propra.sharingplatform.model.User;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.isAlphanumeric;

@Data
public class UserForm {
    /*public UserForm() {}*/
    /*public UserForm(String name, String address, String email, String propayId) {

    }*/

    private String name;
    private String address;
    private String email;
    private String propayId;

    public User parseToUser() {
        if (!validateUserForm()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cant validate formdata!");
        }
        User user = new User();
        user.setName(name);
        user.setAddress(address);
        user.setEmail(email);
        user.setPropayId(propayId);
        return user;
    }

    //TODO default propayid = name
    private boolean validateUserForm() {
        if (name == null || address == null || email == null || propayId == null) {
            return false;
        }
        if (name.length() == 0 || name.length() > 255 || !isAlphanumeric(name)) {
            return false;
        }
        if (address.length() == 0 || address.length() > 255) {
            return false;
        }
        if (email.length() == 0 || email.length() > 255 || !validateMail()) {
            return false;
        }
        if (propayId.length() == 0 || propayId.length() > 255) {
            return false;
        }
        return true;
    }

    private boolean validateMail() {
        Pattern pattern = Pattern.compile("^.+@.+\\..+$");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
