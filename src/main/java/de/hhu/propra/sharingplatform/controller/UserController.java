package de.hhu.propra.sharingplatform.controller;

import de.hhu.propra.sharingplatform.form.UserForm;
import de.hhu.propra.sharingplatform.dao.ItemRepo;
import de.hhu.propra.sharingplatform.dao.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ItemRepo itemRepo;

    @GetMapping("/user/register")
    public String registerPage(Model model) {
        UserForm form = new UserForm();
        model.addAttribute("userForm", form);
        return "register";
    }

    @PostMapping("/user/register")
    public String registerNewUser(Model model, @ModelAttribute("userForm") UserForm userForm) {
        System.out.println(userForm.parseToUser());
        return "redirect:/";
    }

    @GetMapping("/user/account/{id}")
    public String accountPage(Model model, @PathVariable(value = "id", required = true) long id) {
        return "account";
    }

}
