package de.hhu.propra.sharingplatform.controller;

import de.hhu.propra.sharingplatform.service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class ConflictController {

    @Autowired
    private ContractService contractService;


}
