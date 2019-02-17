package de.hhu.propra.sharingplatform.form;

import de.hhu.propra.sharingplatform.model.User;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public class EditUserForm {

    private String name;
    private String propayId;
    private String address;

    public EditUserForm() {
    }

    public EditUserForm(User user) {
        name = user.getName();
        address = user.getAddress();
        propayId = user.getPropayId();
    }

    public void applyToUser(User user) {
        validateAdress();
        validateName();
        user.setName(name);
        user.setAddress(address);
        user.setPropayId(propayId);
    }

    private void validateName() {
        if (name == null || name.length() == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name was empty");
        }
        if (name.length() > 255) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name is too long");
        }
        if (hastSpecialChars(name)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name is invalid.");
        }
    }

    private void validateAdress() {
        if (address == null || address.length() == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Address was empty");
        }
        if (address.length() > 255) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Address is too long");
        }
        if (hastSpecialChars(address)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Address is invalid.");
        }
    }

    private boolean hastSpecialChars(String string) {
        Pattern pattern = Pattern.compile("[^a-z0-9 -]", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(string);
        return matcher.find();
    }
}
