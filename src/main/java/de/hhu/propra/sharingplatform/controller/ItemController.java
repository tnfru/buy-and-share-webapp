package de.hhu.propra.sharingplatform.controller;

import de.hhu.propra.sharingplatform.dao.ImageService;
import de.hhu.propra.sharingplatform.model.Item;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.service.ItemService;
import de.hhu.propra.sharingplatform.service.UserService;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class ItemController {

    private final ItemService itemService;

    private final UserService userService;

    private final ImageService storageService;

    @Autowired
    public ItemController(ItemService itemService, UserService userService,
                          ImageService storageService) {
        this.itemService = itemService;
        this.userService = userService;
        this.storageService = storageService;
    }

    @GetMapping("/item/details/{itemId}")
    public String detailPage(Model model, @PathVariable long itemId, Principal principal) {
        Item item = itemService.findItem(itemId);
        model.addAttribute("item", item);
        model.addAttribute("user", userService.fetchUserByAccountName(principal.getName()));
        boolean ownItem = itemService.userIsOwner(item,
            userService.fetchUserIdByAccountName(principal.getName()));
        model.addAttribute("ownItem", ownItem);
        return "itemDetails";
    }

    @GetMapping("/item/new")
    public String newItem(Model model, Principal principal) {
        User user = userService.fetchUserByAccountName(principal.getName());
        model.addAttribute("item", new Item(user));
        model.addAttribute("user", user);
        return "itemForm";
    }

    @PostMapping("/item/new")
    public String inputItemData(Model model, Item item, Principal principal,
                                @RequestParam("file") MultipartFile file) {
        itemService.persistItem(item, userService.fetchUserIdByAccountName(principal.getName()));
        String imagefilename = "item-" + item.getId();
        item.setImageFileName(imagefilename);
        storageService.store(file, imagefilename);
        itemService.persistItem(item, userService.fetchUserIdByAccountName(principal.getName()));
        return "redirect:/user/account/";
    }

    @GetMapping("/item/remove/{itemId}")
    public String markItemAsRemoved(Model model, @PathVariable long itemId,
                                    Principal principal) {
        itemService.removeItem(itemId, userService.fetchUserIdByAccountName(principal.getName()));
        return "redirect:/user/account/";
    }

    @GetMapping("/item/edit/{itemId}")
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

    @PostMapping("/item/edit/{itemId}")
    public String editItemData(Model model, Item item,
                               @PathVariable long itemId,
                               Principal principal) {
        long userId = userService.fetchUserIdByAccountName(principal.getName());
        itemService.editItem(item, itemId, userId);
        return "redirect:/user/account";
    }
}
