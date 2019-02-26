package de.hhu.propra.sharingplatform.model;

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
        CascadeType.REFRESH}, mappedBy = "itemRental")
    private List<Contract> contracts = new ArrayList<>();

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

    public List<Contract> getChosenContracts(boolean finished) {
        List<Contract> chosenContracts = new ArrayList<>();
        for (Contract contract : contracts) {
            if (contract.isFinished() == finished) {
                chosenContracts.add(contract);
            }
        }
        return chosenContracts;
    }
}
