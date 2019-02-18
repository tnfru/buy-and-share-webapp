package de.hhu.propra.sharingplatform.controller;

import de.hhu.propra.sharingplatform.dao.ItemRepo;
import de.hhu.propra.sharingplatform.model.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class OfferController {

    @Autowired
    private ItemRepo itemRepo;

    @GetMapping("/offer/request/{itemId}")
    public String createOffer(@PathVariable long itemId, Model model) {
        Item item = itemRepo.findOneById(itemId);
        model.addAttribute(item);
        item.getOwner().getAccountName();
        return "offerReguest";
    }
}
