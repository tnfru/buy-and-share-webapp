package de.hhu.propra.sharingplatform.dao;

import de.hhu.propra.sharingplatform.model.ItemRental;
import java.util.Optional;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface ItemRentalRepo extends CrudRepository<ItemRental, Long> {
    ItemRental findOneById(long id);

    Optional<ItemRental> findById(long id);

    List<ItemRental> findAllByNameContainsIgnoreCase(String search);
}
