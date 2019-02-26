package de.hhu.propra.sharingplatform.model.items;

import de.hhu.propra.sharingplatform.model.Offer;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.model.contracts.BorrowContract;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = false)
public class ItemRental extends Item {

    private Integer bail;
    private Integer dailyRate;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST,
        CascadeType.REFRESH}, mappedBy = "itemRental")
    private List<Offer> offers = new ArrayList<>();

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST,
        CascadeType.REFRESH}, mappedBy = "item")
    private List<BorrowContract> contracts = new ArrayList<>();

    @SuppressWarnings("unused")
    private ItemRental() {
        // used for jpa
    }

    public ItemRental(User owner) {
        super(owner);
    }

    public long getActiveOffers() {
        long counter = 0;
        for (Offer offer : offers) {
            if (!(offer.isAccept() || offer.isDecline())) {
                counter++;
            }
        }
        return counter;
    }

    public List<BorrowContract> getChosenContracts(boolean finished) {
        List<BorrowContract> chosenContracts = new ArrayList<>();
        for (BorrowContract contract : contracts) {
            if (contract.isFinished() == finished) {
                chosenContracts.add(contract);
            }
        }
        return chosenContracts;
    }
}
