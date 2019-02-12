package de.hhu.propra.sharingplatform.model;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private Item item;
    private User wantsToBorrow;
    private boolean accept = false;
}
