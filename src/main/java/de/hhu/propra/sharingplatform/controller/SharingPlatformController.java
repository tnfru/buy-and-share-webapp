package de.hhu.propra.sharingplatform.controller;

import java.security.Principal;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SharingPlatformController {

    @GetMapping("/")
    public String mainPage(Model model, Principal p) {
        boolean authenticate = false;
        if (!(SecurityContextHolder.getContext().getAuthentication()
            instanceof AnonymousAuthenticationToken)) {
            authenticate = true;
        }
        model.addAttribute("authenticated", authenticate);
        return "mainpage";
    }
}
