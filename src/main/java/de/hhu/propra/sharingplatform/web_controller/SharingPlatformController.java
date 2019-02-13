package de.hhu.propra.sharingplatform.web_controller;

import de.hhu.propra.sharingplatform.form.UserForm;
import java.io.IOException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SharingPlatformController {

    public SharingPlatformController() {
    }

    @GetMapping("/")
    public String mainPage(Model model) throws IOException {
        return "mainpage";
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        return "login";
    }

    @GetMapping("/logout")
    public String logoutPage(Model model) {
        return "logout";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        UserForm form = new UserForm();
        model.addAttribute("userForm", form);
        return "register";
    }

    @PostMapping("/register")
    public String registerNewUser(Model model, @ModelAttribute("userForm") UserForm userForm) {
        System.out.println(userForm.parseToUser());
        return "mainpage";
    }


    @GetMapping("/account")
    public String accountPage(Model model, @RequestParam(value = "id", required = true) long id) {
        return "account";
    }

    @GetMapping("/details")
    public String detailPage(Model model,
        @RequestParam(value = "id", required = true) long id) {
        //TODO: add param do differentiate users
        return "details";
    }
}
