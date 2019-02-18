package de.hhu.propra.sharingplatform.controller;

import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.dao.UserRepo;
import de.hhu.propra.sharingplatform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@Controller
public class UserController {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private UserService userService;

    @GetMapping("/user/register")
    public String registerPage(Model model) {
        return "userForm";
    }

    @PostMapping("/user/register")
    public String registerNewUser(Model model, User user, String password, String confirm) {
        userService.persistUser(user, password, confirm);
        userService.loginUsingSpring(request, user.getAccountName(), password);
        return "redirect:/";
    }

    @GetMapping("/user/account")
    public String accountPage(Model model, Principal principal) {
        User user = userService.fetchUserByAccountName(principal.getName());
        model.addAttribute("user", user);
        return "account";
    }

    @GetMapping("/user/edit")
    public String editUserPage(Model model, Principal principal) {
        User user = userService.fetchUserByAccountName(principal.getName());
        model.addAttribute("user", user);
        return "userForm";
    }

    @PostMapping("/user/edit")
    public String editUser(Model model, Principal principal, User user) {
        User dbUser = userService.fetchUserByAccountName(principal.getName());
        userService.updateUser(dbUser, user);
        return "redirect:/user/account";
    }

    @GetMapping("/user/changePassword")
    public String changePasswordPage(Model model, Principal principal) {
        User user = userService.fetchUserByAccountName(principal.getName());
        model.addAttribute("user", user);
        return "changePassword";
    }

    @PostMapping("/user/changePassword")
    public String changePassword(Model model, Principal principal, String oldPassword,
        String newPassword, String confirm) {
        User user = userService.fetchUserByAccountName(principal.getName());
        userService.updatePassword(user, oldPassword, newPassword, confirm);
        return "redirect:/user/account";
    }
}
