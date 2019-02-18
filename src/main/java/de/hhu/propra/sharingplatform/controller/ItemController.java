package de.hhu.propra.sharingplatform.controller;

import de.hhu.propra.sharingplatform.model.Item;
import de.hhu.propra.sharingplatform.service.ItemService;
import de.hhu.propra.sharingplatform.service.UserService;
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

    private final ItemService itemService;

    private final UserService userService;

    @Autowired
    public ItemController(ItemService itemService, UserService userService) {
        this.itemService = itemService;
        this.userService = userService;
    }

    @GetMapping("/item/details/{itemId}")
    public String detailPage(Model model, @PathVariable long itemId, Principal principal) {
        Item item = itemService.findItem(itemId);
        model.addAttribute("item", item);
        model.addAttribute("user", userService.fetchUserByAccountName(principal.getName()));
        boolean ownItem = itemService.userIsOwner(item,
            userService.fetchUserIdByAccountName(principal.getName()));
        model.addAttribute("ownItem", ownItem);
        return "details";
    }

    @GetMapping("/item/newItem")
    public String newItem(Model model, Principal principal) {
        model.addAttribute("item", new Item());
        model.addAttribute("user", userService.fetchUserByAccountName(principal.getName()));
        return "itemForm";
    }

    @PostMapping("/item/newItem")
    public String inputItemData(Model model, Item item, Principal principal) {
        itemService.persistItem(item, userService.fetchUserIdByAccountName(principal.getName()));
        return "redirect:/user/account/";
    }

    @GetMapping("/item/removeItem/")
    public String markItemAsRemoved(Model model,
                                    @RequestParam(value = "itemId", required = true) long itemId,
                                    Principal principal) {
        itemService.removeItem(userService.fetchUserIdByAccountName(principal.getName()), itemId);
        return "redirect:/user/account/";
    }

    @GetMapping("/item/editItem/{itemId}")
    public String editItem(Model model, @PathVariable long itemId, Principal principal) {
        Item item = itemService.findItem(itemId);
        model.addAttribute("item", item);
        model.addAttribute("itemId", itemId);
        long userId = userService.fetchUserIdByAccountName(principal.getName());
        model.addAttribute("userId", userId);
        if (itemService.userIsOwner(item, userId)) {
            return "itemForm";
        }
        return "error";
    }

    @PostMapping("/item/editItem/{itemId}")
    public String editItemData(Model model, Item item,
                               @PathVariable long itemId,
                               Principal principal) {
        long userId = userService.fetchUserIdByAccountName(principal.getName());
        itemService.editItem(item, itemId, userId);
        return "redirect:/user/account";
    }
}
