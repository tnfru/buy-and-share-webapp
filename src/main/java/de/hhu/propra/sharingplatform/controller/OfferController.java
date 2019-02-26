package de.hhu.propra.sharingplatform.controller;

import de.hhu.propra.sharingplatform.model.Item;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.service.ItemService;
import de.hhu.propra.sharingplatform.service.OfferService;
import de.hhu.propra.sharingplatform.service.UserService;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class OfferController extends BaseController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private OfferService offerService;

    @GetMapping("/offer/request/{itemId}")
    public String gotOfferForm(@PathVariable long itemId, Model model) {
        Item item = itemService.findItem(itemId);
        model.addAttribute(item);
        item.getOwner().getAccountName();
        return "offerRequest";
    }

    @PostMapping("/offer/request/{itemId}")
    public String createOffer(@PathVariable long itemId,
                              @RequestParam(name = "daterange") String dateRange,
                              Principal principal) {
        User user = userService.fetchUserByAccountName(principal.getName());
        offerService.create(itemId, user, getStart(dateRange), getEnd(dateRange));
        return "redirect:/";
    }

    @GetMapping("/offer/show/{itemId}")
    public String showAllOffers(@PathVariable long itemId, Principal principal, Model model) {
        User user = userService.fetchUserByAccountName(principal.getName());
        model.addAttribute("item", itemService.findItem(itemId));
        model.addAttribute("closedOffers",
            offerService.getItemOffers(itemId, user, true));
        model.addAttribute("openOffers",
            offerService.getItemOffers(itemId, user, false));
        return "showOffers";
    }

    @GetMapping("/offer/remove/{offerId}")
    public String deleteOwnOffer(@PathVariable long offerId, Principal principal) {
        User user = userService.fetchUserByAccountName(principal.getName());
        offerService.deleteOffer(offerId, user);
        return "redirect:/user/account";
    }

    @GetMapping("/offer/show/{offerId}/accept")// todo change to offer/accept/{id}
    public String acceptOffer(@PathVariable long offerId, Principal principal) {
        User user = userService.fetchUserByAccountName(principal.getName());
        offerService.acceptOffer(offerId, user);
        return "redirect:/user/account";
    }

    @GetMapping("/offer/show/{offerId}/decline")// todo change to offer/accept/{id}
    public String declineOffer(@PathVariable long offerId, Principal principal) {
        User user = userService.fetchUserByAccountName(principal.getName());
        offerService.declineOffer(offerId, user);
        return "redirect:/user/account";
    }

    LocalDateTime getStart(String formattedDateRange) {
        return readTime(formattedDateRange, 0);
    }

    LocalDateTime getEnd(String formattedDateRange) {
        LocalDateTime end = readTime(formattedDateRange, 1);
        end = end.plusHours(23);
        end = end.plusMinutes(59);
        return end.plusSeconds(59);
    }

    LocalDateTime readTime(String formattedDateRange, int index) {
        try {
            String[] dates = formattedDateRange.split(" - ");
            DateTimeFormatter format = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            LocalDate date = LocalDate.parse(dates[index], format);
            LocalDateTime dateTime = date.atStartOfDay();
            return dateTime;
        } catch (DateTimeParseException parseException) {
            parseException.printStackTrace();
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Wrong dateformat");
        } catch (ArrayIndexOutOfBoundsException arrayBoundException) {
            arrayBoundException.printStackTrace();
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Wrong dateformat");
        }
    }
}
