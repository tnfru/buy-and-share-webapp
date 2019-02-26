package de.hhu.propra.sharingplatform.controller;

import de.hhu.propra.sharingplatform.dao.UserRepo;
import de.hhu.propra.sharingplatform.model.Conflict;
import de.hhu.propra.sharingplatform.model.Contract;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.service.UserService;
import org.hibernate.validator.internal.util.Contracts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

import javax.swing.text.html.Option;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Controller
public class BaseController {

    final UserService userService;

    @Autowired
    public BaseController(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute("overdueMsg")
    public String overdueMsg(Principal principal) {

        String message = "";
        if (principal != null) {

            List<Contract> contracts = userService.fetchUserByAccountName(principal.getName())
                .getChosenContracts(false);
            LocalDateTime now = LocalDateTime.now();

            for (Contract contract : contracts) {
                if (contract.getRealEnd() == null) {
                    if (now.isAfter(contract.getExpectedEnd())) {
                        message += "Your Contract with User "
                            + contract.getItem().getOwner().getAccountName()
                            + " regarding Item "
                            + contract.getItem().getName()
                            + " is overdue \n";
                    }
                }
            }
        }
        return message;
    }

    @ModelAttribute("conflictMsg")
    public String conflictMsg(Principal principal) {

        String message = "";
        if (principal != null) {

            User user = userService.fetchUserByAccountName(principal.getName());
            List<Contract> contracts = user.getChosenContracts(false);


            for (Contract contract : contracts) {
                List<Conflict> conflicts = contract.getOpenConflicts();
                if (!conflicts.isEmpty()) {
                    if (user != conflicts.get(0).getRequester()) {
                        message += "You have a conflict regarding Item "
                            + contract.getItem().getName()
                            + "\n";
                    }
                }

            }
        }

        return message;

    }


}
