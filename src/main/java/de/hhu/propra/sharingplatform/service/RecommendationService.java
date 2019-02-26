package de.hhu.propra.sharingplatform.service;

import de.hhu.propra.sharingplatform.dao.ContractRepo;
import de.hhu.propra.sharingplatform.dao.ItemRentalRepo;
import de.hhu.propra.sharingplatform.model.Contract;
import de.hhu.propra.sharingplatform.model.ItemRental;
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

    private final ItemRentalRepo itemRentalRepo;

    private int numberOfItems;

    @Autowired
    public RecommendationService(ContractRepo contractRepo, ItemRentalRepo itemRentalRepo) {
        this.contractRepo = contractRepo;
        this.itemRentalRepo = itemRentalRepo;
        this.numberOfItems = 4;
    }

    /**
     * uses the users who bought x also bought y schema.
     *
     * @param itemId itemRental to find recommendations for
     * @return returns list of itemRentals
     */

    public List<ItemRental> findRecommendations(long itemId) {
        ItemRental itemRental = itemRentalRepo.findOneById(itemId);
        List<Contract> contracts = contractRepo.findAllByItemRental(itemRental);
        List<User> otherBorrowers = findOtherBorrowers(contracts);
        Map<ItemRental, Integer> map = fillMap(otherBorrowers);

        return findBestItems(map, itemId);
    }

    /**
     * Looks for the best matches. If not enough are available by K-nearest neighbours random ones
     * will be filled
     *
     * @param map to read itemRentals and values from
     * @return array List of best suggestions
     */

    List<ItemRental> findBestItems(Map<ItemRental, Integer> map, long itemId) {
        List<Entry<ItemRental, Integer>> entrys = findGreatest(map);
        List<ItemRental> suggestions = new ArrayList<>();

        for (Entry<ItemRental, Integer> entry : entrys) {
            if (entry.getKey().getId() != itemId) {
                suggestions.add(entry.getKey());
            }
        }

        while (suggestions.size() > numberOfItems) {
            suggestions.remove(0);
        }

        return suggestions.size() < numberOfItems ? fillList(suggestions) : suggestions;
    }

    List<ItemRental> fillList(List<ItemRental> suggestions) {
        List<ItemRental> allItemRentals = (List<ItemRental>) itemRentalRepo.findAll();
        while (suggestions.size() < numberOfItems) {
            ItemRental randomSuggestion = allItemRentals
                .get((int) (Math.random() * allItemRentals.size()));
            if (!suggestions.contains(randomSuggestion)) {
                suggestions.add(randomSuggestion);
            }
        }
        return suggestions;
    }

    Map<ItemRental, Integer> fillMap(List<User> otherBorrowers) {
        // Maps the itemRentals with the values of their frequency
        Map<ItemRental, Integer> map = new HashMap<>();
        for (User otherBorrower : otherBorrowers) {
            List<ItemRental> borrowedItemRentals = findBorrowedItems(otherBorrower.getId());
            putBorrowedItems(map, borrowedItemRentals);
        }
        return map;
    }

    List<ItemRental> findBorrowedItems(long userId) {
        List<Contract> allContracts = (List<Contract>) contractRepo.findAll();
        List<ItemRental> itemRentals = new ArrayList<>();

        for (Contract contract : allContracts) {
            if (contract.getBorrower().getId() == userId) {
                itemRentals.add(contract.getItemRental());
            }
        }
        return itemRentals;
    }

    private void putBorrowedItems(Map<ItemRental, Integer> map,
        List<ItemRental> borrowedItemRentals) {
        for (ItemRental borrowedItemRental : borrowedItemRentals) {
            map.put(borrowedItemRental, map.getOrDefault(borrowedItemRental, 1));
        }
    }

    private List<User> findOtherBorrowers(List<Contract> contracts) {
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
        int suggestionCount = numberOfItems + 1;
        Comparator<? super Entry<K, V>> comparator = (Comparator<Entry<K, V>>) (e0, e1) -> {
            V v0 = e0.getValue();
            V v1 = e1.getValue();
            return v0.compareTo(v1);
        };
        PriorityQueue<Entry<K, V>> highest = new PriorityQueue<>(suggestionCount, comparator);
        for (Entry<K, V> entry : map.entrySet()) {
            highest.offer(entry);
            while (highest.size() > suggestionCount) {
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
