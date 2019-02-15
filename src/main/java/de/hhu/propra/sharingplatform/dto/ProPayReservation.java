package de.hhu.propra.sharingplatform.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class ProPayReservation {

    private double amount;
    private long id;
}
