package de.hhu.propra.sharingplatform.service;

import de.hhu.propra.sharingplatform.dao.ContractRepo;
import de.hhu.propra.sharingplatform.dao.ItemRentalRepo;
import de.hhu.propra.sharingplatform.model.Contract;
import de.hhu.propra.sharingplatform.model.ItemRental;
import de.hhu.propra.sharingplatform.model.Offer;
import de.hhu.propra.sharingplatform.model.User;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class RecommendationServiceTest {
    @MockBean
    ContractRepo contractRepo;

    @MockBean
    ItemRentalRepo itemRentalRepo;

    private RecommendationService recommendationService;

    @Before
    public void setUp() {
        this.recommendationService = new RecommendationService(contractRepo, itemRentalRepo);
    }

    @Test
    public void findBorrowedItem() {
        List<Contract> contracts = createFakeContracts();
        when(contractRepo.findAll()).thenReturn(contracts);
        List<ItemRental> itemRentals = recommendationService.findBorrowedItems(5L);

        assertEquals(1, itemRentals.size());
        assertEquals(contracts.get(5).getItemRental(), itemRentals.get(0));
    }


    @Ignore
    @Test
    public void findGreatest() {
        this.recommendationService.setNumberOfItems(1);
        Map<ItemRental, Integer> map = new HashMap<>();
        ItemRental itemRentalOne = new ItemRental(mock(User.class));
        itemRentalOne.setId(1337L);
        ItemRental itemRentalTwo = new ItemRental(mock(User.class));
        itemRentalTwo.setId(7331L);
        map.put(itemRentalOne, 10);
        map.put(itemRentalTwo, 2);

        assertEquals(2, recommendationService.findGreatest(map).size());
        assertEquals(itemRentalOne, recommendationService.findGreatest(map).get(0).getKey());
    }

    public List<User> createFakerUser() {
        List<User> users = new ArrayList<>();
        for (long i = 0; i < 20; i++) {
            User user = new User();
            user.setId(i);
            users.add(user);
        }
        return users;
    }

    public List<ItemRental> createFakeItems() {
        List<User> users = createFakerUser();
        List<ItemRental> itemRentals = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            ItemRental itemRental = new ItemRental(users.get(i));
            itemRentals.add(itemRental);
        }
        return itemRentals;
    }

    public List<Contract> createFakeContracts() {
        List<User> users = createFakerUser();
        List<ItemRental> itemRentals = createFakeItems();
        List<Contract> contracts = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            LocalDateTime start = LocalDateTime.now();
            LocalDateTime end = start.plusDays(3);
            Offer offer = new Offer(itemRentals.get(i), users.get(i), start, end);
            Contract contract = new Contract(offer);
            contracts.add(contract);
        }

        for (int i = 0; i < 4; i++) {
            LocalDateTime start = LocalDateTime.now();
            LocalDateTime end = start.plusDays(3);
            Offer offer = new Offer(itemRentals.get(i), users.get(4 - i), start, end);
            Contract contract = new Contract(offer);
            contracts.add(contract);
        }

        return contracts;
    }
}