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
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

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

    private String generatePassword(String password, String confirm) {
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

    public boolean checkPassword(String password, User user) {
        return user.getPasswordHash().equals(hashPassword(password));
    }

    private String hashPassword(String plainPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(plainPassword);
    }

    public void persistUser(User user, String password, String confirm) {
        validateUser(user);
        String hashPassword = generatePassword(password, confirm);
        user.setPasswordHash(hashPassword);
        userRepo.save(user);
    }

    private void validateUser(User user) {
        validateMail(user);
        validateAdress(user);
        validateName(user);
    }

    public void loginUsingSpring(HttpServletRequest request, String accountName, String password) {
        try {
            request.login(accountName, password);
        } catch (ServletException except) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Auto login went wrong");
        }
    }

}

