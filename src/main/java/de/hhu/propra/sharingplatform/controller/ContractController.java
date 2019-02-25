package de.hhu.propra.sharingplatform.controller;

import de.hhu.propra.sharingplatform.service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class ContractController {

    @Autowired
    private ContractService contractService;

    @PostMapping("/contract/{contractId}/acceptReturn")
    public String acceptReturn(@PathVariable long contractId, Principal principal) {
        contractService.acceptReturn(contractId, principal.getName());
        return "redirect:/user/account";
    }

    @PostMapping("/contract/{contractId}/returnItem")
    public String returnItem(@PathVariable long contractId, Principal principal) {
        contractService.returnItem(contractId, principal.getName());
        return "redirect:/user/account";
    }
}
