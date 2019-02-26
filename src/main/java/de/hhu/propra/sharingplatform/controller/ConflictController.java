package de.hhu.propra.sharingplatform.controller;

import de.hhu.propra.sharingplatform.service.ConflictService;
import de.hhu.propra.sharingplatform.service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class ConflictController {

    @Autowired
    private ContractService contractService;

    @Autowired
    private ConflictService conflictService;


    /**
     * Page for admins to see open conflicts.
     *
     * @param model the model.
     * @return admin-dashboard.html
     */
    @GetMapping("/conflicts/show")
    public String adminDashboard(Model model) {
        model.addAttribute("openConflicts",
            conflictService.getOpenConflicts());
        return "admin-dashboard";
    }

    @GetMapping("/openConflict/{contractId}")
    public String getConflictForm(@PathVariable long contractId, Principal principal) {
        contractService.validateOwner(contractId, principal.getName());
        return "conflictForm";
    }

    @PostMapping("/openConflict/{contractId}")
    public String openConflict(@RequestParam(value = "description") String description,
                               Principal principal, @PathVariable long contractId) {
        contractService.validateOwner(contractId, principal.getName());
        contractService.openConflict(description, principal.getName(), contractId);
        return "redirect:/user/account";
    }

    @GetMapping("/conflicts/{conflictId}/details")
    public String conflictDetails(@PathVariable long conflictId, Model model) {
        model.addAttribute("conflict", conflictService.fetchConflictById(conflictId));
        return "conflictDetails";
    }

    @PostMapping("/conflicts/{conflictId}/punishBail")
    public String punishBail(@PathVariable long conflictId,
                             @RequestParam(name = "punishPercent") long percent) {
        conflictService.punish(conflictId, percent);
        conflictService.close(conflictId);
        return "redirect:/conflicts/show";
    }

    @PostMapping("/conflicts/{conflictId}/cancel")
    public String cancelContract(@PathVariable long conflictId) {
        contractService.cancelContract(conflictId);
        conflictService.close(conflictId);
        return "redirect:/conflicts/show";
    }

    @PostMapping("/conflicts/{conflictId}/continue")
    public String continueContract(@PathVariable long conflictId) {
        conflictService.close(conflictId);
        return "redirect:/conflicts/show";
    }

}
