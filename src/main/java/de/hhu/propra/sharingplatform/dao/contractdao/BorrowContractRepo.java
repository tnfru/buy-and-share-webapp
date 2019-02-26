package de.hhu.propra.sharingplatform.dao.contractdao;

import de.hhu.propra.sharingplatform.model.Item;
import de.hhu.propra.sharingplatform.model.contracts.BorrowContract;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BorrowContractRepo extends CrudRepository<BorrowContract, Long> {

    BorrowContract findOneById(long id);

    List<BorrowContract> findAllByItem(Item item);

    List<BorrowContract> findAllByItemAndFinishedIsFalse(Item item);

    List<BorrowContract> findAll();

}
