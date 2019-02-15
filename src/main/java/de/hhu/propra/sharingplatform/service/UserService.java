package de.hhu.propra.sharingplatform.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

@Service
public class UserService {

    public void loginUsingSpring(HttpServletRequest request, String accountName, String password) {
        try {
            request.login(accountName, password);
        } catch (ServletException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Auto login went wrong");
        }
    }
}
