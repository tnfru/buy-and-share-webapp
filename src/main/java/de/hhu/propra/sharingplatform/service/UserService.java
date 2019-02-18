package de.hhu.propra.sharingplatform.service;

import static org.apache.commons.lang3.StringUtils.isAlphanumeric;

import com.google.common.hash.Hashing;
import de.hhu.propra.sharingplatform.dao.UserRepo;
import de.hhu.propra.sharingplatform.model.User;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {

    final private UserRepo userRepo;

    @Autowired
    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    private void validateMail(User user) {
        Pattern pattern = Pattern.compile("^.+@.+\\..+$");
        Matcher matcher = pattern.matcher(user.getEmail());
        if (!matcher.matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not a valid E-Mail");
        }
    }

    private void validateName(User user) {
        if (user.getName() == null || user.getName().length() == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name was empty");
        }
        if (user.getName().length() > 255) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name is too long");
        }
        if (!isAlphanumeric(user.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name is invalid.");
        }
    }

    private void validateAdress(User user) {
        if (user.getAddress() == null || user.getAddress().length() == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Address was empty");
        }
        if (user.getAddress().length() > 255) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Address is too long");
        }
        if (!isAlphanumeric(user.getAddress())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Address is invalid.");
        }
    }

    public String generatePassword(String password, String confirm) {
        if (!(password.equals(confirm))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Passwörter müssen übereinstimmen");
        }

        validatePasswords(password);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    private void validatePasswords(String password) {
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

    /*
    public void setPassword(String password){
        salt = UUID.randomUUID().toString();
        passwordHash = hashPassword(password);
    }
    */

    public boolean checkPassword(String password, User user) {
        return user.getPasswordHash().equals(hashPassword(password, user));
    }

    private String hashPassword(String plainPassword, User user) {
        plainPassword += user.getSalt();
        plainPassword += user.getPepper();
        return Hashing.sha512().hashString(plainPassword, StandardCharsets.UTF_8).toString();
    }

    public void persistUser(User user){
        userRepo.save(user);
    }

    public void validate(User user){
        validateMail(user);
        validateAdress(user);
        validateName(user);
    }

}

