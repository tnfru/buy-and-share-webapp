package de.hhu.propra.sharingplatform.controller;

import de.hhu.propra.sharingplatform.dao.ItemRepo;
import de.hhu.propra.sharingplatform.dao.UserRepo;
import de.hhu.propra.sharingplatform.model.User;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class SharingPlatformController {

    @Autowired
    private ItemRepo itemRepo;

    @Autowired
    private UserRepo userRepo;

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
}
