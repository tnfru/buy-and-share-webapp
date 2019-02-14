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

    private String name;
    private String address;
    private String email;
    private String propayId;
    private String password;
    private String passwordConfirm;

    public User parseToUser() {
        validateAdress();
        validateMail();
        validateName();
        validatePasswords();
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

    private void validateMail() {
        Pattern pattern = Pattern.compile("^.+@.+\\..+$");
        Matcher matcher = pattern.matcher(email);
        if(! matcher.matches()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not a valid E-Mail");
        }
    }

    private void validateName(){
        if(name == null || name.length() == 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name was empty");
        }
        if(name.length() > 255){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name is too long");
        }
        if(!isAlphanumeric(name)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name is invalid.");
        }
    }
    private void validateAdress(){
        if(address == null || address.length() == 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Address was empty");
        }
        if(address.length() > 255){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Address is too long");
        }
        if(!isAlphanumeric(address)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Address is invalid.");
        }
    }
    private void validatePasswords(){
        if(password == null || name.length() == 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password was empty");
        }
        if(password.length() > 255){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is too long");
        }
        if(password.length() < 8){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is too short");
        }
    }
}
