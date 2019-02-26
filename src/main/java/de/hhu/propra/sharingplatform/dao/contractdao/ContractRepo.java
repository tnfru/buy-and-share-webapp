package de.hhu.propra.sharingplatform.dao.contractdao;

import de.hhu.propra.sharingplatform.model.Item;
import de.hhu.propra.sharingplatform.model.contracts.Contract;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ContractRepo extends CrudRepository<Contract, Long> {

    Contract findOneById(long id);

    List<Contract> findAllByItem(Item item);

    List<Contract> findAllByItemAndFinishedIsFalse(Item item);
}
