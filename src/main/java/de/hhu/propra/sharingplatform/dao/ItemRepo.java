package de.hhu.propra.sharingplatform.dao;

import de.hhu.propra.sharingplatform.model.items.Item;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface ItemRepo<T extends Item> extends CrudRepository<T, Long> {

    List<T> findAllByNameContainsIgnoreCaseAndDeletedIsFalse(String key);

    List<T> findAllByDeletedIsFalse();

}
