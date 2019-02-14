package de.hhu.propra.sharingplatform.modelDAO;


import de.hhu.propra.sharingplatform.model.Payment;
import org.springframework.data.repository.CrudRepository;

public interface PaymentRepo extends CrudRepository<Payment, Long> {

    Payment findById(long id);
}
