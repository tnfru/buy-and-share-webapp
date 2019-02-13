package de.hhu.propra.sharingplatform.web_controller;

import java.io.IOException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SharingPlatformController {

    public SharingPlatformController() {
    }

    @GetMapping("/")
    public String mainPage(Model model) throws IOException {
        model.addAttribute("authenticated", false);
        return "mainpage";
    }

}
