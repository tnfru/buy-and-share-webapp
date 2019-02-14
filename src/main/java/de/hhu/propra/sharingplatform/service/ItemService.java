package de.hhu.propra.sharingplatform.service;

import de.hhu.propra.sharingplatform.model.Item;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.dao.ItemRepo;
import de.hhu.propra.sharingplatform.dao.UserRepo;
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
}
