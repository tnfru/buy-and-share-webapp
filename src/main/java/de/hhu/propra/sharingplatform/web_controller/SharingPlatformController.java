package de.hhu.propra.sharingplatform.web_controller;

import de.hhu.propra.sharingplatform.model.Item;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.modelDAO.ContractRepo;
import de.hhu.propra.sharingplatform.modelDAO.ItemRepo;
import de.hhu.propra.sharingplatform.modelDAO.OfferRepo;
import de.hhu.propra.sharingplatform.modelDAO.UserRepo;
import de.hhu.propra.sharingplatform.form.UserForm;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class SharingPlatformController {

    @Autowired
    UserRepo userRepo;

    @Autowired
    ItemRepo itemRepo;

    @Autowired
    ContractRepo contractRepo;

    @Autowired
    OfferRepo offerRepo;

    @GetMapping("/")
    public String mainPage(Model model) throws IOException {
        model.addAttribute("authenticated", false);
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

    @GetMapping("/account/{id}")
    public String accountPage(Model model, @PathVariable(value = "id", required = true) long id) {
        return "account";
    }

    @GetMapping("/account/{id}/newItem")
    public String newItem(Model model, @PathVariable long id) {
        model.addAttribute("item", new Item());
        model.addAttribute("user", userRepo.findOneById(id));
        return "itemForm";
    }

    @PostMapping("/account/{id}/newItem")
    public String inputItemData(Model model, Item item, @PathVariable long id) {
        User user = userRepo.findOneById(id);
        item.setOwner(user);
        itemRepo.save(item);
        return "redirect:/account/" + id;
    }

    @GetMapping("/details/{id}")
    public String detailPage(Model model,
        @PathVariable(value = "id", required = true) long id) {
        //TODO: add param do differentiate users
        return "details";
    }


}
