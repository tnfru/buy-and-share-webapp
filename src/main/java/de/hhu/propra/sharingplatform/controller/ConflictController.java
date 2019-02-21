package de.hhu.propra.sharingplatform.controller;

import de.hhu.propra.sharingplatform.service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ConflictController {

    @Autowired
    private ContractService contractService;


    @GetMapping("/conflicts/show")
    public String adminDashboard(Model model){
        model.addAttribute("conflicts", contractService.getOpenConflicts());
        return "admin-dashboard";
    }

}
