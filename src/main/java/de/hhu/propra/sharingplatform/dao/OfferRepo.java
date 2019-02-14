package de.hhu.propra.sharingplatform.dao;

import de.hhu.propra.sharingplatform.model.Offer;
import org.springframework.data.repository.CrudRepository;

public interface OfferRepo extends CrudRepository<Offer, Long> {
    Offer findOneById(long id);
}
