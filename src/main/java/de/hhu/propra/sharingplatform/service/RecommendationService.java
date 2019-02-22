package de.hhu.propra.sharingplatform.service;

import de.hhu.propra.sharingplatform.dao.ContractRepo;
import de.hhu.propra.sharingplatform.dao.ItemRepo;
import de.hhu.propra.sharingplatform.model.Contract;
import de.hhu.propra.sharingplatform.model.Item;
import de.hhu.propra.sharingplatform.model.User;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.Map.Entry;

@Data
@Service
public class RecommendationService {

    private final ContractRepo contractRepo;

    private final ItemRepo itemRepo;

    private int numberOfItems;

    @Autowired
    public RecommendationService(ContractRepo contractRepo, ItemRepo itemRepo) {
        this.contractRepo = contractRepo;
        this.itemRepo = itemRepo;
        this.numberOfItems = 4;
    }

    /**
     * uses the users who bought x also bought y schema.
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

    /**
     * Looks for the best matches.
     * If not enough are available by K-nearest neighbours random ones will be filled
     *
     * @param map to read items and values from
     * @return array List of best suggestions
     */

    List<Item> findBestItems(Map<Item, Integer> map) {
        List<Entry<Item, Integer>> entrys = findGreatest(map);
        List<Item> bestMatches = new ArrayList<>();

        for (Entry<Item, Integer> entry : entrys) {
            bestMatches.add(entry.getKey());
        }

        return bestMatches.size() > numberOfItems ? bestMatches : fillList(bestMatches);
    }

    List<Item> fillList(List<Item> bestMatches) {
        List<Item> allItems = (List<Item>) itemRepo.findAll();
        while (bestMatches.size() < numberOfItems) {
            bestMatches.add(allItems.get((int) (Math.random() * allItems.size())));
        }
        return bestMatches;
    }

    Map<Item, Integer> fillMap(List<User> otherBorrowers) {
        // Maps the items with the values of their frequency
        Map<Item, Integer> map = new HashMap<>();
        for (User otherBorrower : otherBorrowers) {
            List<Item> borrowedItems = findBorrowedItems(otherBorrower.getId());
            putBorrowedItems(map, borrowedItems);
        }
        return map;
    }

    List<Item> findBorrowedItems(long userId) {
        List<Contract> allContracts = (List<Contract>) contractRepo.findAll();
        List<Item> items = new ArrayList<>();

        for (Contract contract : allContracts) {
            if (contract.getBorrower().getId() == userId) {
                items.add(contract.getItem());
            }
        }
        return items;
    }

    void putBorrowedItems(Map<Item, Integer> map, List<Item> borrowedItems) {
        for (Item borrowedItem : borrowedItems) {
            map.put(borrowedItem, map.getOrDefault(borrowedItem, 1));
        }
    }

    List<User> findOtherBorrowers(List<Contract> contracts) {
        List<User> otherBorrowers = new ArrayList<>();
        for (Contract contract : contracts) {
            otherBorrowers.add(contract.getBorrower());
        }
        return otherBorrowers;
    }

    /**
     * Thank you stackoverflow.
     *
     * @param map map to find highest values of
     * @return numberOfItem recommendations
     */
    <K, V extends Comparable<? super V>> List<Entry<K, V>> findGreatest(Map<K, V> map) {
        Comparator<? super Entry<K, V>> comparator = (Comparator<Entry<K, V>>) (e0, e1) -> {
            V v0 = e0.getValue();
            V v1 = e1.getValue();
            return v0.compareTo(v1);
        };
        PriorityQueue<Entry<K, V>> highest = new PriorityQueue<>(numberOfItems, comparator);
        for (Entry<K, V> entry : map.entrySet()) {
            highest.offer(entry);
            while (highest.size() > numberOfItems) {
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
