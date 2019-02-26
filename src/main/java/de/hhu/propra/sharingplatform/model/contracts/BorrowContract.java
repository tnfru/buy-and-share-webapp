package de.hhu.propra.sharingplatform.model.contracts;

import de.hhu.propra.sharingplatform.dto.Status;
import de.hhu.propra.sharingplatform.model.Conflict;
import de.hhu.propra.sharingplatform.model.Offer;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.model.payments.BorrowPayment;
import de.hhu.propra.sharingplatform.service.payment.IPaymentApi;

import javax.persistence.CascadeType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class BorrowContract extends Contract {

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
        mappedBy = "contract")
    private List<Conflict> conflicts;
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private User borrower;
    private LocalDateTime start;
    private LocalDateTime expectedEnd;
    private LocalDateTime realEnd;

    private BorrowContract(){}

    public BorrowContract(Offer offer) {
        borrower = offer.getBorrower();
        item = offer.getItem();
        expectedEnd = offer.getEnd();
        start = offer.getStart();
    }


    @Override
    public void prepare(IPaymentApi paymentApi) {
        String from = borrower.getPropayId();
        String to = super.item.getOwner().getPropayId();
        long timespan = Math.max(start.until(expectedEnd, ChronoUnit.DAYS) + 1, 0);
        int amount = (int) Math.ceil(timespan * super.item.getPrice());
        int bail = super.item.getBail();
        payment = new BorrowPayment(from, to, amount, bail);
        ((BorrowPayment) payment).reserve(paymentApi);
    }

    public void freeBail(IPaymentApi paymentApi){

    }

    public void punishBail(IPaymentApi paymentApi){

    }

    public void returnItem(){
        realEnd = LocalDateTime.now();
        long timespan = Math.max(start.until(realEnd, ChronoUnit.DAYS) + 1, 0);
        int amount = (int) Math.ceil(timespan * super.item.getPrice());
        payment.setAmount(amount);
    }

    public List<Conflict> getOpenConflicts() {
        List<Conflict> openConflicts = new ArrayList<>();
        for (Conflict conflict : conflicts) {
            if (conflict.getStatus().equals(Status.PENDING)) {
                openConflicts.add(conflict);
            }
        }
        return openConflicts;
    }
}
