package de.hhu.propra.sharingplatform.model;

import de.hhu.propra.sharingplatform.dto.Status;
import de.hhu.propra.sharingplatform.model.contracts.Contract;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class Conflict {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private Contract contract;

    @Column(length = 1000)
    private String description;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private User requester;

}
