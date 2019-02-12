package de.hhu.propra.sharingplatform.modelDAO;

import de.hhu.propra.sharingplatform.model.Item;
import org.springframework.data.repository.CrudRepository;

public interface ItemRepo extends CrudRepository<Item, Long> {
}
