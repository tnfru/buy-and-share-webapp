package de.hhu.propra.sharingplatform.dao.contractdao;

import de.hhu.propra.sharingplatform.model.contracts.SellContract;
import de.hhu.propra.sharingplatform.model.items.Item;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface SellContractRepo extends CrudRepository<SellContract, Long> {

    List<SellContract> findAllByItemAndFinishedIsFalse(Item item);

    SellContract findOneById(long id);
}
