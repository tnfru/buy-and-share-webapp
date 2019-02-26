package de.hhu.propra.sharingplatform.model;

import de.hhu.propra.sharingplatform.dto.Status;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private User borrower;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private Payment payment;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private ItemRental itemRental;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
        mappedBy = "contract")
    private List<Conflict> conflicts;

    private LocalDateTime start;
    private LocalDateTime expectedEnd;
    private LocalDateTime realEnd;
    private boolean finished = false;


    @SuppressWarnings("unused")
    private Contract() {
        // used by jpa
    }

    public Contract(Offer offer) {
        this.borrower = offer.getBorrower();
        this.itemRental = offer.getItemRental();
        this.expectedEnd = offer.getEnd();
        this.start = offer.getStart();
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
