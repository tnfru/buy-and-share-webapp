package de.hhu.propra.sharingplatform.dao;

import de.hhu.propra.sharingplatform.model.Offer;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OfferRepo extends CrudRepository<Offer, Long> {
    Offer findOneById(long id);
    
    List<Offer> findAllByItemId(long itemId);

    List<Offer> findAllByItemIdAndAcceptIsFalseAndDeclineIsFalse(long itemId);

    List<Offer> findAllByItemIdAndAcceptIsTrueOrDeclineIsTrue(long itemId);
}
