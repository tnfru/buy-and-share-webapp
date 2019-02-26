package de.hhu.propra.sharingplatform.service;

import de.hhu.propra.sharingplatform.dao.ContractRepo;
import de.hhu.propra.sharingplatform.dao.ItemRepo;
import de.hhu.propra.sharingplatform.model.contracts.BorrowContract;
import de.hhu.propra.sharingplatform.model.contracts.Contract;
import de.hhu.propra.sharingplatform.model.Item;
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
    ItemRepo itemRepo;

    private RecommendationService recommendationService;

    @Before
    public void setUp() {
        this.recommendationService = new RecommendationService(contractRepo, itemRepo);
    }

    @Test
    public void findBorrowedItem() {
        List<BorrowContract> contracts = createFakeContracts();
        //when(contractRepo.findAll()).thenReturn(contracts);
        List<Item> items = recommendationService.findBorrowedItems(5L);

        assertEquals(1, items.size());
        assertEquals(contracts.get(5).getItem(), items.get(0));
    }


    @Ignore
    @Test
    public void findGreatest() {
        this.recommendationService.setNumberOfItems(1);
        Map<Item, Integer> map = new HashMap<>();
        Item itemOne = new Item(mock(User.class));
        itemOne.setId(1337L);
        Item itemTwo = new Item(mock(User.class));
        itemTwo.setId(7331L);
        map.put(itemOne, 10);
        map.put(itemTwo, 2);

        assertEquals(2, recommendationService.findGreatest(map).size());
        assertEquals(itemOne, recommendationService.findGreatest(map).get(0).getKey());
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

    public List<Item> createFakeItems() {
        List<User> users = createFakerUser();
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Item item = new Item(users.get(i));
            items.add(item);
        }
        return items;
    }

    public List<BorrowContract> createFakeContracts() {
        List<User> users = createFakerUser();
        List<Item> items = createFakeItems();
        List<BorrowContract> contracts = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            LocalDateTime start = LocalDateTime.now();
            LocalDateTime end = start.plusDays(3);
            Offer offer = new Offer(items.get(i), users.get(i), start, end);
            BorrowContract contract = new BorrowContract(offer);
            contracts.add(contract);
        }

        for (int i = 0; i < 4; i++) {
            LocalDateTime start = LocalDateTime.now();
            LocalDateTime end = start.plusDays(3);
            Offer offer = new Offer(items.get(i), users.get(4 - i), start, end);
            BorrowContract contract = new BorrowContract(offer);
            contracts.add(contract);
        }

        return contracts;
    }
}