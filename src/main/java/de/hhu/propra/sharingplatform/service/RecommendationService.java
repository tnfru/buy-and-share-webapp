package de.hhu.propra.sharingplatform.service;

import de.hhu.propra.sharingplatform.dao.ContractRepo;
import de.hhu.propra.sharingplatform.dao.ItemRepo;
import de.hhu.propra.sharingplatform.model.Contract;
import de.hhu.propra.sharingplatform.model.Item;
import de.hhu.propra.sharingplatform.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.Map.Entry;

@Service
public class RecommendationService {

    private final ContractRepo contractRepo;

    private final ItemRepo itemRepo;

    private final int numberOfItems = 4;

    @Autowired
    public RecommendationService(ContractRepo contractRepo, ItemRepo itemRepo) {
        this.contractRepo = contractRepo;
        this.itemRepo = itemRepo;
    }

    /**
     * . uses the users who bought x also bought y schema.
     *
     * @param itemId item to find recommendations for
     * @return returns list of items
     */

    public List<Item> findRecommendations(long itemId) {
        Item item = itemRepo.findOneById(itemId);
        List<Contract> contracts = contractRepo.findAllByItem(item);
        List<User> otherBorrowers = findOtherBorrowers(contracts);
        Map<Item, Integer> map = fillMap(otherBorrowers);

        return findBestItems(map);
    }

    List<Item> findBestItems(Map<Item, Integer> map) {
        // Find the items which were most often borrowed. These are our recommendations


        /*

        for (int i = 0; !map.isEmpty() && i < numberOfItems; i++) {
            max = Collections.max(map.values());
            Set<Item> items = map.keySet();
            //Bad Performance but necessary to get all Items with the max value not just one
            for (Item item : items) {
                if (map.get(item) == max && !bestMatches.contains(item)) {
                    bestMatches.add(item);
                }
                if (bestMatches.size() == 3) {
                    return bestMatches;
                }
            }
        } */

        List<Entry<Item, Integer>> entrys = findGreatest(map, numberOfItems);
        List<Item> bestMatches = new ArrayList<>();

        for (Entry<Item, Integer> entry : entrys) {
            bestMatches.add(entry.getKey());
        }

        if (bestMatches.size() < numberOfItems) {
            List<Item> allItems = (List<Item>) itemRepo.findAll();
            while (bestMatches.size() < numberOfItems) {
                bestMatches.add(allItems.get((int) (Math.random() * allItems.size())));
            }
        }

        return bestMatches;
    }

    Map<Item, Integer> fillMap(List<User> otherBorrowers) {
        // Count how often the other items were borrowed
        Map<Item, Integer> map = new HashMap<>();
        for (User otherBorrower : otherBorrowers) {
            List<Item> borrowedItems = findAllBorrowedItems(otherBorrower.getId());
            for (Item borrowedItem : borrowedItems) {
                map.put(borrowedItem, map.getOrDefault(borrowedItem, 1));
            }
        }
        return map;
    }

    List<Item> findAllBorrowedItems(long userId) {
        List<Contract> allContracts = (List<Contract>) contractRepo.findAll();
        List<Item> items = new ArrayList<>();

        for (Contract contract : allContracts) {
            if (contract.getBorrower().getId() == userId) {
                items.add(contract.getItem());
            }
        }
        return items;
    }

    List<User> findOtherBorrowers(List<Contract> contracts) {
        List<User> otherBorrowers = new ArrayList<>();
        for (Contract contract : contracts) { // Find all other borrowers of the same item
            otherBorrowers.add(contract.getBorrower());
        }
        return otherBorrowers;
    }

    private static <K, V extends Comparable<? super V>> List<Entry<K, V>> findGreatest(
        Map<K, V> map, int number) {
        Comparator<? super Entry<K, V>> comparator = (Comparator<Entry<K, V>>) (e0, e1) -> {
            V v0 = e0.getValue();
            V v1 = e1.getValue();
            return v0.compareTo(v1);
        };
        PriorityQueue<Entry<K, V>> highest = new PriorityQueue<>(number, comparator);
        for (Entry<K, V> entry : map.entrySet()) {
            highest.offer(entry);
            while (highest.size() > number) {
                highest.poll();
            }
        }

        List<Entry<K, V>> result = new ArrayList<>();
        while (highest.size() > 0) {
            result.add(highest.poll());
        }
        return result;
    }
}
