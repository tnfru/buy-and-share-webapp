package de.hhu.propra.sharingplatform.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OfferController {

    @GetMapping("/offer/request")
    public String createOffer() {
        return "offerReguest";
    }
}
