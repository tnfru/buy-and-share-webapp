package de.hhu.propra.sharingplatform.controller;

import de.hhu.propra.sharingplatform.service.ConflictService;
import de.hhu.propra.sharingplatform.service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class ConflictController {

    @Autowired
    private ContractService contractService;

    @Autowired
    private ConflictService conflictService;


    /**
     * Page for admins to see open conflicts.
     * @param model the model.
     * @return admin-dashboard.html
     */
    @GetMapping("/conflicts/show")
    public String adminDashboard(Model model) {
        model.addAttribute("conflicts", conflictService.getOpenConflicts());
        return "admin-dashboard";
    }

    /**
     * Called when admin thinks the bail should go to item owner.
     * Important: This method can only be called by an administrator.
     *
     * @return admin page.
     */
    @PostMapping("/conflicts/{conflictId}/accept")
    public String acceptConflict(@PathVariable long conflictId, Principal principal) {
        conflictService.resolveOwnerConflict(true, conflictId);
        return "redirect:/conflicts/show";
    }

    /**
     * Called when admin thinks the conflict is not justified.
     * Important: This method can only be called by an administrator.
     *
     * @return admin page.
     */
    @PostMapping("/conflicts/{conflictId}/reject")
    public String rejectConflict(@PathVariable long conflictId, Principal principal) {
        contractService.resolveOwnerConflict(false, conflictId);
        return "redirect:/conflicts/show";
    }

}
