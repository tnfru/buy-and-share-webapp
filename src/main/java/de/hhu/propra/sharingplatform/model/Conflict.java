package de.hhu.propra.sharingplatform.model;

import de.hhu.propra.sharingplatform.dto.Status;
import lombok.Builder;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Conflict {



    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, mappedBy = "conflict")
    private Contract contract;

    public String getStatus() {
        return status.toString();
    }

}
