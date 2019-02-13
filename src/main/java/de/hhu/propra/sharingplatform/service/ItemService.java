package de.hhu.propra.sharingplatform.service;

import de.hhu.propra.sharingplatform.model.Item;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.modelDAO.ItemRepo;
import de.hhu.propra.sharingplatform.modelDAO.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ItemService {

    @Autowired
    ItemRepo itemRepo;

    @Autowired
    UserRepo userRepo;

    public void persistItem(Item item, long userId) {
        User owner = userRepo.findOneById(userId);
        item.setOwner(owner);
        itemRepo.save(item);
    }

    public void removeItem(long userId, long itemId) {
        Item item = itemRepo.findOneById(itemId);
        if(item.getOwner().getId() == userId) {
            item.setDeleted(true);
            itemRepo.save(item);
        }
    }
}
