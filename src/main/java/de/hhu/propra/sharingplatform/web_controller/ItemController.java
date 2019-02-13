package de.hhu.propra.sharingplatform.web_controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ItemController {
    @GetMapping("/item/details/{id}")
    public String detailPage(Model model,
                             @PathVariable(value = "id", required = true) long id) {
        //TODO: add param do differentiate users
        return "details";
    }
}
