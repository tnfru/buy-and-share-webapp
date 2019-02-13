package de.hhu.propra.sharingplatform.web_controller;

import de.hhu.propra.sharingplatform.model.Item;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.modelDAO.ContractRepo;
import de.hhu.propra.sharingplatform.modelDAO.ItemRepo;
import de.hhu.propra.sharingplatform.modelDAO.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import sun.security.krb5.internal.ccache.CredentialsCache;

@Controller
public class ItemController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ItemRepo itemRepo;

    @GetMapping("/item/details/{id}")
    public String detailPage(Model model,
        @PathVariable(value = "id", required = true) long id) {
        //TODO: add param do differentiate users
        return "details";
    }

    @GetMapping("/item/newItem/{userId}")
    public String newItem(Model model, @PathVariable long userId) {
        model.addAttribute("item", new Item());
        model.addAttribute("user", userRepo.findOneById(userId));
        return "itemForm";
    }

    @PostMapping("/item/newItem/{userId}")
    public String inputItemData(Model model, Item item, @PathVariable long userId) {
        User user = userRepo.findOneById(userId);
        item.setOwner(user);
        itemRepo.save(item);
        return "redirect:/user/account/" + userId;
    }
}
