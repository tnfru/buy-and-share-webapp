package de.hhu.propra.sharingplatform.dao.contractdao;

import de.hhu.propra.sharingplatform.model.contracts.Contract;
import de.hhu.propra.sharingplatform.model.items.ItemRental;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface ContractRepo extends CrudRepository<Contract, Long> {

    Contract findOneById(long id);

    List<Contract> findAllByItem(ItemRental itemRental);

    List<Contract> findAllByItemAndFinishedIsFalse(ItemRental itemRental);
}
