package de.hhu.propra.sharingplatform.dao;

import de.hhu.propra.sharingplatform.model.Contract;
import de.hhu.propra.sharingplatform.model.Item;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ContractRepo extends CrudRepository<Contract, Long> {

    Contract findOneById(long id);

    List<Contract> findAllByItem(Item item);

    List<Contract> findAllByActiveIsTrue();

    List<Contract> findAllByItemWhereActiveIsTrue();
}
