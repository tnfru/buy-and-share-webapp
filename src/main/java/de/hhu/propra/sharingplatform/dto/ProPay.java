package de.hhu.propra.sharingplatform.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
public class ProPay {

    private String account;
    private int amount;
    List<ProPayReservation> reservations;
}
