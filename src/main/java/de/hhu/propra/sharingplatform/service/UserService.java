package de.hhu.propra.sharingplatform.service;

import de.hhu.propra.sharingplatform.dao.UserRepo;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.service.validation.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserService {

    final UserRepo userRepo;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public void persistUser(User user, String password, String confirm) {
        validateUser(user);
        String hashPassword = generatePassword(password, confirm);
        user.setPasswordHash(hashPassword);
        userRepo.save(user);
    }

    public void loginUsingSpring(HttpServletRequest request, String accountName, String password) {
        try {
            request.login(accountName, password);
        } catch (ServletException except) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Auto login went wrong");
        }
    }

    public void updateUser(User oldUser, User newUser) {
        validateUser(newUser);
        oldUser.setName(newUser.getName());
        oldUser.setAddress(newUser.getAddress());
        oldUser.setEmail(newUser.getEmail());
        oldUser.setPropayId(newUser.getPropayId());
        userRepo.save(oldUser);
    }

    public void updatePassword(User oldUser, String oldPassword, String newPassword,
                               String confirm) {
        if (!encoder.matches(oldPassword, oldUser.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect Password");
        }
        oldUser.setPasswordHash(generatePassword(newPassword, confirm));
        userRepo.save(oldUser);
    }

    public User fetchUserByAccountName(String accountName) {
        Optional<User> search = userRepo.findByAccountName(accountName);
        if (!search.isPresent()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not Authenticated");
        }
        return search.get();
    }

    public User fetchUserById(Long userId) {
        /*Optional<User> search = userRepo.findOneById(userId);
        if (!search.isPresent()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not Authenticated");
        }*/
        return userRepo.findOneById(userId);
    }

    public long fetchUserIdByAccountName(String accountName) {
        Optional<User> search = userRepo.findByAccountName(accountName);
        if (!search.isPresent()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not Authenticated");
        }
        return (search.get().getId());
    }

    public boolean checkPassword(String password, User user) {
        return user.getPasswordHash().equals(hashPassword(password));
    }

    private String hashPassword(String plainPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(plainPassword);
    }

    private String generatePassword(String password, String confirm) {
        if (!(password.equals(confirm))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Passwords need to be the same.");
        }
        validatePasswords(password);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    private void validateUser(User user) {
        UserValidator.validateUser(user, userRepo);
    }

    private void validatePasswords(String password) {
        UserValidator.validatePassword(password);
    }

}

