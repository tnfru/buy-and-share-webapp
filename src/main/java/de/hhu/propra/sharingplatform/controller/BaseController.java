package de.hhu.propra.sharingplatform.controller;

import de.hhu.propra.sharingplatform.model.Conflict;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.model.contracts.BorrowContract;
import de.hhu.propra.sharingplatform.model.items.ItemRental;
import de.hhu.propra.sharingplatform.service.UserService;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;


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
            message = generateOverdueMessage(principal, message);
        }
        return message;
    }

    private String generateOverdueMessage(Principal principal, String message) {
        List<BorrowContract> contracts = userService.fetchUserByAccountName(principal.getName())
            .getChosenContracts(false);
        LocalDateTime now = LocalDateTime.now();

        message = writeOverdueMessage(message, contracts, now);
        return message;
    }

    private String writeOverdueMessage(String message, List<BorrowContract> contracts,
        LocalDateTime now) {
        for (BorrowContract contract : contracts) {
            if (contract.getRealEnd() == null && now.isAfter(contract.getExpectedEnd())) {
                message += "Your Contract with User "
                    + contract.getItem().getOwner().getAccountName()
                    + " regarding Item "
                    + contract.getItem().getName()
                    + " is overdue \n";
            }
        }
        return message;
    }

    @ModelAttribute("conflictMsg")
    public String conflictMsg(Principal principal) {

        String message = "";
        if (principal != null) {
            message = generateConflictMessage(principal, message);
        }
        return message;
    }

    private String generateConflictMessage(Principal principal, String message) {
        User user = userService.fetchUserByAccountName(principal.getName());
        Collection<BorrowContract> contracts = user.getChosenContracts(false);
        Collection<ItemRental> items = user.getItemRentals();
        //Collection<Item> items = user.getNotRemovedItems(user.getItemRentals());
        for (ItemRental item : items) {
            contracts.addAll(item.getChosenContracts(false));
        }

        message = writeConflictMessage(message, user, contracts);
        return message;
    }

    private String writeConflictMessage(String message, User user,
        Collection<BorrowContract> contracts) {
        for (BorrowContract contract : contracts) {
            List<Conflict> conflicts = contract.getOpenConflicts();
            if (!conflicts.isEmpty() && user != conflicts.get(0).getRequester()) {
                message += "You have a conflict regarding Item "
                    + contract.getItem().getName()
                    + "\n";
            }
        }
        return message;
    }


}
