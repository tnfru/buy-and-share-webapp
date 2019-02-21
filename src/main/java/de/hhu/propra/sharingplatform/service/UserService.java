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

@Service
public class UserService {

    private final UserRepo userRepo;

    private final PasswordEncoder encoder;

    private final ApiService apiService;

    @Autowired
    public UserService(UserRepo userRepo, PasswordEncoder encoder, ApiService apiService) {
        this.userRepo = userRepo;
        this.encoder = encoder;
        this.apiService = apiService;
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
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Auto login went wrong");
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
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Could not authenticate User.");
        }
        return search.get();
    }

    public User fetchUserById(Long userId) {
        Optional<User> search = Optional.ofNullable(userRepo.findOneById(userId));
        if (!search.isPresent()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Could not authenticate User.");
        }
        return userRepo.findOneById(userId);
    }

    public long fetchUserIdByAccountName(String accountName) {
        Optional<User> search = userRepo.findByAccountName(accountName);
        if (!search.isPresent()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Could not authenticate User.");
        }
        return (search.get().getId());
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
        validatePassword(password);
        return hashPassword(password);
    }

    private void validateUser(User user) {
        UserValidator.validateUser(user, userRepo);
    }

    private void validatePassword(String password) {
        UserValidator.validatePassword(password);
    }

    public Double getCurrentPropayAmount(String accountName) {
        return apiService.mapJson(accountName).getAmount();
    }

    public void updateProPay(User user, String account, String inputAmount) {
        if (account.length() > 0) {
            user.setPropayId(account);
            //UserValidator.validateUser(user, userRepo);
            userRepo.save(user);
        }
        if (inputAmount.length() > 0) {
            Double amount;
            try {
                amount = Double.parseDouble(inputAmount);
                apiService.addAmount(user.getPropayId(), amount);
            } catch (NumberFormatException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Propay amount have to be a number.");
            }
        }
    }
}

