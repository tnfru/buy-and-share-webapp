package de.hhu.propra.sharingplatform.controller;

import de.hhu.propra.sharingplatform.service.UserService;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;


@Controller
public class SharingPlatformErrorController extends BaseController implements ErrorController {

    public SharingPlatformErrorController(
        UserService userService) {
        super(userService);
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }

    @RequestMapping("/error")
    public String errorHandling(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        String errormsg = String.valueOf(request.getAttribute(RequestDispatcher.ERROR_MESSAGE));
        model.addAttribute("errorcode", Integer.valueOf(status.toString()));
        if (errormsg.length() <= 0) {
            errormsg = "No message provided";
        }
        model.addAttribute("errormsg", errormsg);
        return "error";
    }
}
