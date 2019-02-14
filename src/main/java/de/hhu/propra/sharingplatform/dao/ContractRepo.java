package de.hhu.propra.sharingplatform.dao;

import de.hhu.propra.sharingplatform.model.Contract;
import org.springframework.data.repository.CrudRepository;

public interface ContractRepo extends CrudRepository<Contract, Long> {
    Contract findOneById(long id);
}
