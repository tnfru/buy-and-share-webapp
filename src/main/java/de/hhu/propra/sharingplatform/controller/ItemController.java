package de.hhu.propra.sharingplatform.web_controller;

import de.hhu.propra.sharingplatform.model.Item;
import de.hhu.propra.sharingplatform.modelDAO.UserRepo;
import de.hhu.propra.sharingplatform.service.ItemService;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ItemController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ItemService itemService;

    @GetMapping("/item/details/{itemId}/{userId}")
    public String detailPage(Model model, @PathVariable long itemId, @PathVariable long userId) {
        //TODO: add param do differentiate users
        model.addAttribute("item", itemService.forceGetItem(itemId));
        model.addAttribute("user", userRepo.findOneById(userId));
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
        itemService.persistItem(item, userId);
        return "redirect:/user/account/" + userId;
    }

    @GetMapping("/item/removeItem/{userId}")
    public String markItemAsRemoved(Model model,
        @RequestParam(value = "itemId", required = true) long itemId, @PathVariable long userId) {
        itemService.removeItem(userId, itemId);
        return "redirect:/user/account/" + userId;
    }

    @GetMapping("/item/editItem/{userId}/{itemId}")
    public String editItem(Model model, @PathVariable long itemId, @PathVariable long userId) {
        Item item = itemService.forceGetItem(itemId);
        model.addAttribute("item", item);
        model.addAttribute("itemId", itemId);
        model.addAttribute("userId", userId);
        return "itemForm";
    }
/*
    @PostMapping("/item/editItem/{userId}")
    public String editItemData(Model model, Item item, @PathVariable long userId) {
        itemService.editItem(item, itemId, userId);
        return "redirect:/user/account/" + userId;
    }*/
}
