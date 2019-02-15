package de.hhu.propra.sharingplatform.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.hhu.propra.sharingplatform.dto.ProPay;
import de.hhu.propra.sharingplatform.model.Payment;
import de.hhu.propra.sharingplatform.model.User;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Data
@Service
public class ApiService {

    String host = "localhost";

    String fetchJson(String userName) {
        String url = "http://" + host + ":8888/account/" + userName;
        RestTemplate jsonResponse = new RestTemplate();

        return jsonResponse.getForObject(url, String.class);
    }

    public ProPay mapJson(String userName) {
        String jsonResponse = fetchJson(userName);
        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.readValue(jsonResponse, ProPay.class);
        } catch (IOException ioException) {
            ioException.printStackTrace();
            return null;
        }
    }

    public void enforcePayment(Payment payment) {

    }

    public boolean checkSolvent(User borrower, double totalAmount) {
        ProPay borrowerProPay = mapJson(borrower.getPropayId());
        return borrowerProPay.getAmount() >= totalAmount;
    }
}
