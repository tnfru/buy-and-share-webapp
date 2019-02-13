package de.hhu.propra.sharingplatform.web_controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SharingPlatformController {

    @GetMapping("/")
    public String mainPage(Model model) {
        model.addAttribute("authenticated", false);
        return "mainpage";
    }
}
