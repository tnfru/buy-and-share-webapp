package de.hhu.propra.sharingplatform.controller;

import de.hhu.propra.sharingplatform.dao.ItemRepo;
import de.hhu.propra.sharingplatform.dao.UserRepo;
import de.hhu.propra.sharingplatform.model.Item;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.service.OfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class OfferController {

    @Autowired
    private ItemRepo itemRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    OfferService offerService;

    @GetMapping("/offer/request/{itemId}")
    public String gotOfferForm(@PathVariable long itemId, Model model) {
        Item item = itemRepo.findOneById(itemId);
        model.addAttribute(item);
        item.getOwner().getAccountName();
        return "offerReguest";
    }

    @PostMapping("/offer/request/{itemId}")
    public String createOffer(@PathVariable long itemId, @RequestParam(name = "daterange") String dateRange, Principal principal) {
        User user = userRepo.findByAccountName(principal.getName()).get();
        Date start = getStart(dateRange);
        Date end = getEnd(dateRange);
        //offerService.create(itemId, user, start, end);
        return "redirect:/";
    }


    //TODO: simplify/remove redundant code
    private Date getStart(String formattedDateRange) {
        String[] dates = formattedDateRange.split(" - ");
        DateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        try {
            return format.parse(dates[0]);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Wrong dateformat");
        }
    }
    private Date getEnd(String formattedDateRange) {
        String[] dates = formattedDateRange.split(" - ");
        DateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        try {
            Date end =format.parse(dates[1]);
            end.setHours(23);
            end.setMinutes(59);
            end.setSeconds(59);
            return end;
        } catch (ParseException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Wrong dateformat");
        }
    }
}
