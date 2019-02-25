package de.hhu.propra.sharingplatform.model.contracts;

import de.hhu.propra.sharingplatform.model.Conflict;
import de.hhu.propra.sharingplatform.model.Offer;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.service.payment.IPaymentApi;

import javax.persistence.CascadeType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;

public class BorrowContract extends Contract {

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private Conflict conflict;
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private User borrower;
    private LocalDateTime start;
    private LocalDateTime expectedEnd;
    private LocalDateTime realEnd;
    private boolean finished = false;

    public BorrowContract(Offer offer) {
        this.borrower = offer.getBorrower();
        this.item = offer.getItem();
        this.expectedEnd = offer.getEnd();
        this.start = offer.getStart();
    }

    public boolean isConflict() {
        return conflict != null;
    }

    @Override
    public void pay(IPaymentApi paymentApi){

    }
}
