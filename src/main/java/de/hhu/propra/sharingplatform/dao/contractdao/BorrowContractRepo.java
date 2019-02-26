package de.hhu.propra.sharingplatform.dao.contractdao;

import de.hhu.propra.sharingplatform.model.contracts.BorrowContract;
import de.hhu.propra.sharingplatform.model.items.Item;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface BorrowContractRepo extends CrudRepository<BorrowContract, Long> {

    BorrowContract findOneById(long id);

    List<BorrowContract> findAllByItem(Item item);

    List<BorrowContract> findAllByItemAndFinishedIsFalse(Item item);
}
