package de.hhu.propra.sharingplatform.service;

import de.hhu.propra.sharingplatform.dao.ItemRepo;
import de.hhu.propra.sharingplatform.model.Item;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.service.validation.ItemValidator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ItemService {

    private ImageService itemImageSaver;
    private final UserService userService;
    private final ItemRepo itemRepo;

    public ItemService(ItemRepo itemRepo, UserService userService, ImageService itemImageSaver) {
        this.itemRepo = itemRepo;
        this.userService = userService;
        this.itemImageSaver = itemImageSaver;
    }

    @Deprecated
    public void persistItem(Item item, long userId) {
        validateItem(item);
        User owner = userService.fetchUserById(userId);
        item.setOwner(owner);
        itemRepo.save(item);
    }

    public void persistItem(Item item, long userId, MultipartFile image) {
        validateItem(item);
        User owner = userService.fetchUserById(userId);
        item.setOwner(owner);
        itemRepo.save(item);
        String imagefilename = "item-" + item.getId();
        itemImageSaver.store(image, imagefilename);
        item.setImageFileName(imagefilename);
        itemRepo.save(item);
    }

    public void removeItem(long itemId, long userId) {
        Item item = findIfPresent(itemId);
        allowOnlyOwner(item, userId);

        if (userIsOwner(item.getId(), userId)) {
            item.setDeleted(true);
            itemRepo.save(item);
        }
    }

    private Item findIfPresent(long itemId) {
        Optional<Item> optional = itemRepo.findById(itemId);
        if (!optional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid Item");
        }
        return optional.get();
    }

    public Item findItem(long itemId) {
        Item item = findIfPresent(itemId);
        if (item.isDeleted()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This Item was deleted");
        }
        return item;
    }

    public void editItem(Item newItem, long oldItemId, long userId) {
        validateItem(newItem);
        Item oldItem = findIfPresent(oldItemId);
        if (userIsOwner(oldItem.getId(), userId)) {
            newItem.setOwner(oldItem.getOwner());
            newItem.setId(oldItem.getId());
            newItem.setAvailable(oldItem.isAvailable());
            itemRepo.save(newItem);
        }
    }

    public void allowOnlyOwner(Item item, long userId) {
        if (item.getOwner().getId() != userId) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your Item");
        }
    }

    public boolean userIsOwner(long itemId, long userId) {
        Item item = findIfPresent(itemId);
        return item.getOwner().getId() == userId;
    }

    public void validateItem(Item item) {
        ItemValidator.validateItem(item);
    }

    public List<String> searchKeywords(String search) {
        if (search.equals("")) {
            return new ArrayList<>();
        }
        search = search.toLowerCase();
        search = search.replace(",", " ");
        search = search.replace("-", " ");
        search = search.replace("_", " ");
        search = search.trim().replaceAll(" +", " ");
        String[] split = search.split(" ");
        List<String> keywords = new ArrayList<>();
        for (int i = 0; i < split.length; i++) {
            if (!keywords.contains(split[i])) {
                keywords.add(split[i]);
            }
        }
        return keywords;
    }

    public List<Item> filter(List<String> keywords) {
        if (keywords == null || keywords.size() == 0) {
            return (List<Item>) itemRepo.findAll();
        }
        List<Item> items = new ArrayList<>();
        for (String key : keywords) {
            List<Item> searching = itemRepo.findAllByNameContainsIgnoreCase(key);
            items.addAll(searching);
        }
        return items;
    }
}
