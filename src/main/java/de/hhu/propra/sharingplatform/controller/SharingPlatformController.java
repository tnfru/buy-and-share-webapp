package de.hhu.propra.sharingplatform.controller;

import de.hhu.propra.sharingplatform.dao.ItemRepo;
import org.springframework.beans.factory.annotation.Autowired;
import java.security.Principal;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SharingPlatformController {

    @Autowired
    private ItemRepo itemRepo;

    @GetMapping("/")
    public String mainPage(Model model) {
        boolean authenticate = false;
        if (!(SecurityContextHolder.getContext().getAuthentication()
            instanceof AnonymousAuthenticationToken)) {
            authenticate = true;
        }
        model.addAttribute("items", itemRepo.findAll());
        model.addAttribute("authenticated", authenticate);
        return "mainpage";
    }
}
