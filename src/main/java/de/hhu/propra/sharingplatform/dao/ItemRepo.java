package de.hhu.propra.sharingplatform.dao;

import de.hhu.propra.sharingplatform.model.Item;
import org.springframework.data.repository.CrudRepository;

public interface ItemRepo extends CrudRepository<Item, Long> {
    Item findOneById(long id);
}
