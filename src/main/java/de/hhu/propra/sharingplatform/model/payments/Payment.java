package de.hhu.propra.sharingplatform.model.payments;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.model.contracts.Contract;
import lombok.Data;
import lombok.ToString;

@Data
@Entity
@ToString(exclude = "contract")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    int amount;
    String proPayIdSender;
    String proPayIdRecipient;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
        mappedBy = "payment")
    Contract contract;

    @SuppressWarnings("unused")
    private Payment() {
        // used for jpa
    }



}
