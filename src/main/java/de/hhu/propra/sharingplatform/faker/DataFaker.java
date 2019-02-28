package de.hhu.propra.sharingplatform.faker;

import com.github.javafaker.Faker;
import de.hhu.propra.sharingplatform.dao.ItemRepo;
import de.hhu.propra.sharingplatform.dao.OfferRepo;
import de.hhu.propra.sharingplatform.dao.UserRepo;
import de.hhu.propra.sharingplatform.model.Offer;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.model.items.ItemRental;
import de.hhu.propra.sharingplatform.model.items.ItemSale;
import de.hhu.propra.sharingplatform.service.OfferService;
import de.hhu.propra.sharingplatform.service.payment.IPaymentApi;
import de.hhu.propra.sharingplatform.service.payment.ProPayApi;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.logging.Logger;
import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Component
public class DataFaker implements ServletContextInitializer {

    private final UserRepo userRepo;

    private final OfferService offerService;

    private final ItemRepo itemRepo;

    private final OfferRepo offerRepo;

    private final IPaymentApi apiService;

    private Logger log = Logger.getLogger(DataFaker.class.getName());

    private Faker faker;

    @Autowired
    public DataFaker(UserRepo userRepo,
                     ItemRepo itemRepo, OfferRepo offerRepo,
                     OfferService offerService,
                     IPaymentApi apiService) {
        this.userRepo = userRepo;
        this.itemRepo = itemRepo;
        this.offerRepo = offerRepo;
        this.offerService = offerService;
        this.apiService = apiService;
        Random rnd = new Random();
        rnd.setSeed(1337);
        this.faker = new Faker(Locale.ENGLISH, rnd);
    }

    public DataFaker(long seed, UserRepo userRepo, ItemRepo itemRepo,
                     OfferRepo offerRepo, OfferService offerService,
                     ProPayApi proPayApi) {
        this.userRepo = userRepo;
        this.itemRepo = itemRepo;
        this.offerRepo = offerRepo;
        this.offerService = offerService;
        this.apiService = proPayApi;
        Random rnd = new Random();
        rnd.setSeed(seed);
        this.faker = new Faker(Locale.ENGLISH, rnd);
    }

    @Override
    @Transactional
    public void onStartup(ServletContext servletContext) {
        if (userRepo.count() != 0) {
            log.info("Database already initialized.");
            log.info("Skipping database faking");
            return;
        }
        int dataSize = 75;

        log.info("Generating Database");
        UserFaker userFaker = new UserFaker(faker);
        ItemRentalFaker itemRentalFaker = new ItemRentalFaker(faker);
        ItemSaleFaker itemSaleFaker = new ItemSaleFaker(faker);
        TimeFaker timeFaker = new TimeFaker(faker);

        log.info("    Creating User...");
        List<User> users = new ArrayList<>();
        userFaker.createUsers(users, dataSize / 5);

        users.add(userFaker.createAdmin());

        log.info("    Creating ItemsRental...");
        List<ItemRental> itemRentals = new ArrayList<>();
        for (int i = 0; i < (dataSize / 8); i++) {
            User user = getRandomUser(users);
            itemRentalFaker.createItems(itemRentals, user, dataSize / 15);
        }

        log.info("    Creating ItemsSale...");
        List<ItemSale> itemSales = new ArrayList<>();
        for (int i = 0; i < (dataSize / 8); i++) {
            User user = getRandomUser(users);
            itemSaleFaker.createItems(itemSales, user, dataSize / 15);
        }

        log.info("    Persist ItemsRental...");
        itemRepo.saveAll(itemRentals);
        log.info("    Persist ItemsSale...");
        itemRepo.saveAll(itemSales);
        log.info("    Persist Users...");
        userRepo.saveAll(users);

        log.info("    Create ProPay...");
        for (User user : users) {
            try {
                apiService.addMoney(user.getPropayId(), 10000000);
            } catch (ResponseStatusException respException) {
                log.warning("Could not reach Propay Server");
                log.warning("Database may be incomplete");
                return;
            }
        }

        log.info("    Creating Offers...");
        for (int i = 0; i < (dataSize / 3); i++) {
            User user = getRandomUser(users);
            ItemRental itemRental = getRandomItem(itemRentals);

            LocalDateTime start = timeFaker.rndTime();
            LocalDateTime end = timeFaker.rndTimeAfter(start);

            if (!itemRental.getOwner().getId().equals(user.getId())) {
                try {
                    offerService.create(itemRental.getId(), user, start, end);
                } catch (ResponseStatusException respException) {
                    log.warning("Could not reach Propay Server");
                    log.warning("Database may be incomplete");
                    return;
                }
            } else {
                i--;
            }
        }

        log.info("    Interact with Offers...");
        List<Offer> offers = (List<Offer>) offerRepo.findAll();
        for (int i = 0; i < (dataSize / 6); i++) {
            Offer offer = getRandomOffer(offers);
            if (!(offer.isAccept() || offer.isDecline())) {
                try {
                    if (faker.number().numberBetween(0, 1) == 1) {
                        offerService.acceptOffer(offer.getId(), offer.getItemRental().getOwner());
                    } else {
                        offerService.declineOffer(offer.getId(), offer.getItemRental().getOwner());
                    }
                } catch (ResponseStatusException respException) {
                    log.warning("Could not reach Propay Server");
                    log.warning("Database may be incomplete");
                    return;
                }
            } else {
                i--;
            }
        }

        log.info("Done!");
    }

    private User getRandomUser(List<User> users) {
        return users.get(faker.number().numberBetween(0, users.size() - 1));
    }

    private ItemRental getRandomItem(List<ItemRental> itemRentals) {
        return itemRentals.get(faker.number().numberBetween(0, itemRentals.size() - 1));
    }

    private Offer getRandomOffer(List<Offer> offers) {
        return offers.get(faker.number().numberBetween(0, offers.size() - 1));
    }
}
