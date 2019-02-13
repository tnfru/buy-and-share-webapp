package de.hhu.propra.sharingplatform.web_controller;

import de.hhu.propra.sharingplatform.form.UserForm;
import de.hhu.propra.sharingplatform.model.Item;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.modelDAO.ItemRepo;
import de.hhu.propra.sharingplatform.modelDAO.UserRepo;
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

    @GetMapping("/user/account/{id}/newItem")
    public String newItem(Model model, @PathVariable long id) {
        model.addAttribute("item", new Item());
        model.addAttribute("user", userRepo.findOneById(id));
        return "itemForm";
    }

    @PostMapping("/user/account/{id}/newItem")
    public String inputItemData(Model model, Item item, @PathVariable long id) {
        User user = userRepo.findOneById(id);
        item.setOwner(user);
        itemRepo.save(item);
        return "redirect:/account/" + id;
    }
}
