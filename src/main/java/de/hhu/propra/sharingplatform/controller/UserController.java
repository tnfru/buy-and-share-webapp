package de.hhu.propra.sharingplatform.controller;

import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.service.UserService;
import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserController extends BaseController {

    private final HttpServletRequest request;

    private final HttpServletRequest request;

    private final UserService userService;

    @Autowired
    public UserController(UserService userService, HttpServletRequest request) {
        super(userService);
        this.request = request;
    }

    @GetMapping("/user/register")
    public String registerPage(Model model) {
        if (!SecurityContextHolder.getContext()
            .getAuthentication()
            .getName()
            .equals("anonymousUser")) {
            return "redirect:/";
        }
        return "userForm";
    }

    @PostMapping("/user/register")
    public String registerNewUser(Model model, User user, String password, String confirm) {
        if (!SecurityContextHolder.getContext()
            .getAuthentication()
            .getName()
            .equals("anonymousUser")) {
            return "redirect:/";
        }
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

    @PostMapping("/user/edit/propay")
    public String editPropay(Principal principal, String propayAccount, String propayAmount) {
        User user = userService.fetchUserByAccountName(principal.getName());
        userService.updateProPay(user, propayAccount, propayAmount);
        return "redirect:/user/account";
    }

    @GetMapping("/user/{id}")
    public String userDetails(Model model, @PathVariable long id) {
        User user = userService.fetchUserById(id);
        model.addAttribute("user", user);
        return "userDetails";
    }

    @GetMapping("/user/propay")
    @ResponseBody
    public String currentPropayInfo(Principal principal) {
        User user = userService.fetchUserByAccountName(principal.getName());
        return userService.getCurrentPropayAmount(user.getPropayId()) + "";
    }
}
