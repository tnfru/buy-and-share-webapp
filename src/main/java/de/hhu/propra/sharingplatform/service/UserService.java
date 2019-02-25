package de.hhu.propra.sharingplatform.service;

import de.hhu.propra.sharingplatform.dao.UserRepo;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.service.payment.IBankAccountService;
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

    private final IBankAccountService bank;

    @Autowired
    public UserService(UserRepo userRepo, PasswordEncoder encoder,
                       IBankAccountService bankAccountService) {
        this.userRepo = userRepo;
        this.encoder = encoder;
        this.bank = bankAccountService;
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
        newUser.setId(oldUser.getId());
        newUser.setAccountName(oldUser.getAccountName());
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
        return isPresent(userRepo.findByAccountName(accountName));
    }

    public User fetchUserById(Long userId) {
        return isPresent(userRepo.findById(userId));
    }

    public long fetchUserIdByAccountName(String accountName) {
        return isPresent(userRepo.findByAccountName(accountName)).getId();
    }

    private User isPresent(Optional<User> user) {
        if (!user.isPresent()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Something went wrong.");
        }
        return user.get();
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

    public Integer getCurrentPropayAmount(String accountName) {
        return bank.getAccountBalance(accountName);
    }

    public void updateProPay(User user, String account, String inputAmount) {
        if (account.length() > 0) {
            user.setPropayId(account);
            //UserValidator.validateUser(user, userRepo);
            userRepo.save(user);
        }
        if (inputAmount.length() > 0) {
            Integer amount;
            try {
                amount = Integer.parseInt(inputAmount);
                bank.transferMoney(amount, user.getPropayId());
            } catch (NumberFormatException nfException) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Propay amount have to be a number.");
            }
        }
    }
}

