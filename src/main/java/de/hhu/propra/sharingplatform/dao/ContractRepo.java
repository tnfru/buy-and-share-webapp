package de.hhu.propra.sharingplatform.dao;

import de.hhu.propra.sharingplatform.model.contracts.BorrowContract;
import de.hhu.propra.sharingplatform.model.contracts.Contract;
import de.hhu.propra.sharingplatform.model.Item;
import de.hhu.propra.sharingplatform.model.contracts.SellContract;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ContractRepo extends CrudRepository<Contract, Long> {

    Contract findOneById(long id);

    BorrowContract findOneBorrowContractById(long id);

    SellContract findOneSellContractById(long id);

    List<Contract> findAllByItem(Item item);

    List<Contract> findAllByItemAndFinishedIsFalse(Item item);
}
