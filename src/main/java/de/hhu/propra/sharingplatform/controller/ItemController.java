package de.hhu.propra.sharingplatform.controller;

import de.hhu.propra.sharingplatform.dao.UserRepo;
import de.hhu.propra.sharingplatform.model.Item;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.service.ItemService;
import java.security.Principal;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ItemController {

    private final UserRepo userRepo;

    private final ItemService itemService;

    public ItemController(ItemService itemService, UserRepo userRepo) {
        this.itemService = itemService;
        this.userRepo = userRepo;
    }

    @GetMapping("/item/details/{itemId}")
    public String detailPage(Model model, @PathVariable long itemId, Principal principal) {
        Item item = itemService.findItem(itemId);
        model.addAttribute("item", item);
        model.addAttribute("user", userRepo.findByAccountName(principal.getName()));
        boolean ownItem = itemService.userIsOwner(item,
            itemService.getUserIdFromAccountName(principal.getName()));
        model.addAttribute("ownItem", ownItem);
        return "itemDetails";
    }

    @GetMapping("/item/new")
    public String newItem(Model model, Principal principal) {
        Optional<User> optionalUser = userRepo.findByAccountName(principal.getName());
        model.addAttribute("item", new Item(optionalUser.get()));
        model.addAttribute("user", optionalUser);
        return "itemForm";
    }

    @PostMapping("/item/new")
    public String inputItemData(Model model, Item item, Principal principal) {
        itemService.persistItem(item, itemService.getUserIdFromAccountName(principal.getName()));
        return "redirect:/user/account/";
    }

    @GetMapping("/item/remove/{itemId}")
    public String markItemAsRemoved(Model model, @PathVariable long itemId,
        Principal principal) {
        itemService.removeItem(itemService.getUserIdFromAccountName(principal.getName()), itemId);
        return "redirect:/user/account/";
    }

    @GetMapping("/item/edit/{itemId}")
    public String editItem(Model model, @PathVariable long itemId, Principal principal) {
        Item item = itemService.findItem(itemId);
        model.addAttribute("item", item);
        model.addAttribute("itemId", itemId);
        long userId = itemService.getUserIdFromAccountName(principal.getName());
        model.addAttribute("userId", userId);
        if (itemService.userIsOwner(item, userId)) {
            return "itemForm";
        }
        return "error";
    }

    @PostMapping("/item/edit/{itemId}")
    public String editItemData(Model model, Item item, @PathVariable long itemId,
        Principal principal) {
        long userId = itemService.getUserIdFromAccountName(principal.getName());
        itemService.editItem(item, itemId, userId);
        return "redirect:/user/account";
    }
}
