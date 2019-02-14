package de.hhu.propra.sharingplatform.controller;

import de.hhu.propra.sharingplatform.dao.ItemRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SharingPlatformController {

    @Autowired
    private ItemRepo itemRepo;

    @GetMapping("/")
    public String mainPage(Model model) {
        model.addAttribute("authenticated", false);
        model.addAttribute("items", itemRepo.findAll());
        return "mainpage";
    }
}
