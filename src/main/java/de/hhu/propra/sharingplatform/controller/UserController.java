package de.hhu.propra.sharingplatform.controller;

import de.hhu.propra.sharingplatform.dao.ItemRepo;
import de.hhu.propra.sharingplatform.dao.UserRepo;
import de.hhu.propra.sharingplatform.dto.UserForm;
import de.hhu.propra.sharingplatform.form.ChangePasswordForm;
import de.hhu.propra.sharingplatform.form.EditUserForm;
import de.hhu.propra.sharingplatform.model.User;
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
import java.util.Optional;

@Controller
public class UserController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ItemRepo itemRepo;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private UserService userService;

    @GetMapping("/user/register")
    public String registerPage(Model model) {
        UserForm form = new UserForm();
        model.addAttribute("userForm", form);
        return "register";
    }

    @PostMapping("/user/register")
    public String registerNewUser(Model model, @ModelAttribute("userForm") UserForm userForm) {
        User user = userForm.parseToUser();
        if (userRepo.findByEmail(user.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "E-Mail exists.");
        }
        userRepo.save(user);
        userService.loginUsingSpring(request, userForm.getAccountName(), userForm.getPassword());
        return "redirect:/";
    }


    @GetMapping("/user/account")
    public String accountPage(Model model, Principal principal) {
        Optional<User> search = userRepo.findByAccountName(principal.getName());
        if (!search.isPresent()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not Authenticated");
        }
        User user = search.get();
        model.addAttribute("user", user);
        return "account";
    }

    @GetMapping("/user/edit")
    public String editUserPage(Model model, Principal principal) {
        Optional<User> search = userRepo.findByEmail(principal.getName());
        if (!search.isPresent()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not Authenticated");
        }
        User user = search.get();
        EditUserForm form = new EditUserForm(user);
        model.addAttribute("edituser", form);
        return "editUser";
    }

    @PostMapping("/user/edit")
    public String editUser(Model model, Principal principal,
                           @ModelAttribute("edituser") EditUserForm form) {
        Optional<User> search = userRepo.findByEmail(principal.getName());
        if (!search.isPresent()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not Authenticated");
        }
        User user = search.get();
        form.applyToUser(user);
        userRepo.save(user);
        return "redirect:/user/account";
    }

    @GetMapping("/user/changePassword")
    public String changePasswordPage(Model model, Principal principal) {
        ChangePasswordForm form = new ChangePasswordForm();
        model.addAttribute("passwordForm", form);
        return "changePassword";
    }

    @PostMapping("/user/changePassword")
    public String changePassword(Model model, Principal principal,
                                 @ModelAttribute("passwordForm") ChangePasswordForm form) {
        Optional<User> search = userRepo.findByEmail(principal.getName());
        if (!search.isPresent()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not Authenticated");
        }
        User user = search.get();
        form.applyToUser(user);
        userRepo.save(user);
        return "redirect:/user/account";
    }
}
