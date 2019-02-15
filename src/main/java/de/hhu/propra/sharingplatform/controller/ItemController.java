package de.hhu.propra.sharingplatform.controller;

import de.hhu.propra.sharingplatform.dao.UserRepo;
import de.hhu.propra.sharingplatform.model.Item;
import de.hhu.propra.sharingplatform.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class ItemController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ItemService itemService;

    @GetMapping("/item/details/{itemId}")
    public String detailPage(Model model, @PathVariable long itemId, Principal principal) {
        model.addAttribute("item", itemService.findItem(itemId));
        model.addAttribute("user", userRepo.findByAccountName(principal.getName()));
        return "details";
    }

    @GetMapping("/item/newItem")
    public String newItem(Model model, Principal principal) {
        model.addAttribute("item", new Item());
        model.addAttribute("user", userRepo.findByAccountName(principal.getName()));
        return "itemForm";
    }

    @PostMapping("/item/newItem")
    public String inputItemData(Model model, Item item, Principal principal) {
        itemService.persistItem(item, itemService.getUserIdFromAccountName(principal.getName()));
        return "redirect:/user/account/";
    }

    @GetMapping("/item/removeItem/")
    public String markItemAsRemoved(Model model,
                                    @RequestParam(value = "itemId", required = true) long itemId, Principal principal) {
        itemService.removeItem(itemService.getUserIdFromAccountName(principal.getName()), itemId);
        return "redirect:/user/account/";
    }

    @GetMapping("/item/editItem/{itemId}")
    public String editItem(Model model, @PathVariable long itemId, Principal principal) {
        Item item = itemService.findItem(itemId);
        model.addAttribute("item", item);
        model.addAttribute("itemId", itemId);
        model.addAttribute("userId", itemService.getUserIdFromAccountName(principal.getName()));
        return "itemForm";
    }
/*
    @PostMapping("/item/editItem/{userId}")
    public String editItemData(Model model, Item item, @PathVariable long userId) {
        itemService.editItem(item, itemId, userId);
        return "redirect:/user/account/" + userId;
    }*/
}
