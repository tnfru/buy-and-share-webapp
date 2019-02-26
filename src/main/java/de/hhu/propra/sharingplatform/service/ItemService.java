package de.hhu.propra.sharingplatform.service;

import de.hhu.propra.sharingplatform.dao.ItemRentalRepo;
import de.hhu.propra.sharingplatform.model.ItemRental;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.service.validation.ItemValidator;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ItemService {

    private ImageService itemImageSaver;
    private final UserService userService;
    private final ItemRentalRepo itemRentalRepo;

    public ItemService(ItemRentalRepo itemRentalRepo, UserService userService,
        ImageService itemImageSaver) {
        this.itemRentalRepo = itemRentalRepo;
        this.userService = userService;
        this.itemImageSaver = itemImageSaver;
    }

    public void persistItem(ItemRental itemRental, long userId) {
        validateItem(itemRental);
        User owner = userService.fetchUserById(userId);
        itemRental.setOwner(owner);
        itemRentalRepo.save(itemRental);

        String imagefilename = "bike-dummy.png";
        if (itemRental.getImage() != null && itemRental.getImage().getSize() > 0) {
            imagefilename =
                "itemRental-" + itemRental.getId() + "." + itemRental.getImageExtension();
            itemImageSaver.store(itemRental.getImage(), imagefilename);
        }

        itemRental.setImageFileName(imagefilename);
        itemRentalRepo.save(itemRental);
    }

    public void removeItem(long itemId, long userId) {
        ItemRental itemRental = findIfPresent(itemId);
        allowOnlyOwner(itemRental, userId);

        if (userIsOwner(itemRental.getId(), userId)) {
            itemRental.setDeleted(true);
            itemRentalRepo.save(itemRental);
        }
    }

    private ItemRental findIfPresent(long itemId) {
        Optional<ItemRental> optional = itemRentalRepo.findById(itemId);
        if (!optional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ItemRental not Found");
        }
        return optional.get();
    }

    public ItemRental findItem(long itemId) {
        ItemRental itemRental = findIfPresent(itemId);
        if (itemRental.isDeleted()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This ItemRental was deleted");
        }
        return itemRental;
    }

    public void editItem(ItemRental newItemRental, long oldItemId, long userId) {
        ItemRental oldItemRental = findItem(oldItemId);
        allowOnlyOwner(oldItemRental, userId);
        validateItem(newItemRental);

        newItemRental.setOwner(oldItemRental.getOwner());
        newItemRental.setId(oldItemRental.getId());
        itemRentalRepo.save(newItemRental);
    }

    public void allowOnlyOwner(ItemRental itemRental, long userId) {
        if (itemRental.getOwner().getId() != userId) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your ItemRental");
        }
    }

    public boolean userIsOwner(long itemId, long userId) {
        ItemRental itemRental = findIfPresent(itemId);
        return itemRental.getOwner().getId() == userId;
    }

    public void validateItem(ItemRental itemRental) {
        ItemValidator.validateItem(itemRental);
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

    public List<ItemRental> filter(List<String> keywords) {
        if (keywords == null || keywords.size() == 0) {
            return (List<ItemRental>) itemRentalRepo.findAll();
        }
        List<ItemRental> itemRentals = new ArrayList<>();
        for (String key : keywords) {
            List<ItemRental> searching = itemRentalRepo.findAllByNameContainsIgnoreCase(key);
            itemRentals.addAll(searching);
        }
        return itemRentals;
    }
}
