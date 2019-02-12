package de.hhu.propra.sharingplatform.modelDAO;

import de.hhu.propra.sharingplatform.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepo extends CrudRepository<User, Long> {

}
