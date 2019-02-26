package de.hhu.propra.sharingplatform.dao;

import de.hhu.propra.sharingplatform.model.Offer;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OfferRepo extends CrudRepository<Offer, Long> {
    Offer findOneById(long id);
    
    List<Offer> findAllByItemRentalId(long itemId);

    List<Offer> findAllByItemRentalIdAndAcceptIsFalseAndDeclineIsFalse(long itemId);

    List<Offer> findAllByItemRentalIdAndAcceptIsTrueOrDeclineIsTrue(long itemId);

    List<Offer> findAllByItemRentalIdAndDeclineIsFalseAndAcceptIsFalse(long itemId);
}
