package de.hhu.propra.sharingplatform.controller;

import de.hhu.propra.sharingplatform.dao.UserRepo;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

import javax.swing.text.html.Option;
import java.security.Principal;
import java.util.Optional;


@Controller
public  class BaseController {

    @Autowired
    private UserRepo userRepo;

    @ModelAttribute("baseUser")
    public String getUser(Principal principal){

        return "test";
    }
}
