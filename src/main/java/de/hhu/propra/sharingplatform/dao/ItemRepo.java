package de.hhu.propra.sharingplatform.dao;

import de.hhu.propra.sharingplatform.model.Item;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface ItemRepo extends CrudRepository<Item, Long> {
    Item findOneById(long id);

    Optional<Item> findById(long id);
}
