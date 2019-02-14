package de.hhu.propra.sharingplatform.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Controller
public class SharingPlatformErrorController  implements ErrorController {

    @Override
    public String getErrorPath() {
        return "/error";
    }

    @RequestMapping("/error")
    public String errorHandling(HttpServletRequest request, Model model){
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object errormsg = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        int statusCode = 0;
        if (status != null) {
            statusCode = Integer.valueOf(status.toString());
            model.addAttribute("errorcode", statusCode);
        }
        if(errormsg != null){
            model.addAttribute("errormsg", (String) errormsg);
        }
        return "error";
    }
}
