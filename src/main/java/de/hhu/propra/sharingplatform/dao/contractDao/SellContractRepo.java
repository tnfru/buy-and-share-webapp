package de.hhu.propra.sharingplatform.dao.contractDao;

import de.hhu.propra.sharingplatform.model.Item;
import de.hhu.propra.sharingplatform.model.contracts.SellContract;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SellContractRepo extends CrudRepository<SellContract, Long> {

    List<SellContract> findAllByItemAndFinishedIsFalse(Item item);

    SellContract findOneById(long id);
}
