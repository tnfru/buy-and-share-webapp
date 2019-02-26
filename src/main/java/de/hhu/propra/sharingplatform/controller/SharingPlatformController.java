package de.hhu.propra.sharingplatform.controller;

import de.hhu.propra.sharingplatform.dao.ItemRepo;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.service.ItemService;
import de.hhu.propra.sharingplatform.service.UserService;
import java.security.Principal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class SharingPlatformController extends BaseController {

    private final ItemRepo itemRepo;

    private final ItemService itemService;

    @Autowired
    public SharingPlatformController(UserService userService, ItemRepo itemRepo,
        ItemService itemService) {
        super(userService);
        this.itemRepo = itemRepo;
        this.itemService = itemService;
    }

    @GetMapping("/")
    public String mainPage(Model model, Principal principal) {
        User user = null;
        if (principal != null) {
            user = new User();
        }
        model.addAttribute("user", user);
        model.addAttribute("items", itemRepo.findAll());
        return "mainpage";
    }

    @PostMapping("/")
    public String mainPage(Model model, Principal principal, String search) {
        User user = null;
        if (principal != null) {
            user = new User();
        }
        List<String> keywords = itemService.searchKeywords(search);
        model.addAttribute("user", user);
        model.addAttribute("keywords", keywords);
        model.addAttribute("items", itemService.filter(keywords));
        return "mainpage";
    }
}
