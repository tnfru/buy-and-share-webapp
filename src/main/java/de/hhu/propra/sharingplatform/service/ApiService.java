package de.hhu.propra.sharingplatform.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.hhu.propra.sharingplatform.dao.PaymentRepo;
import de.hhu.propra.sharingplatform.dto.ProPay;
import de.hhu.propra.sharingplatform.dto.ProPayReservation;
import de.hhu.propra.sharingplatform.model.Payment;
import de.hhu.propra.sharingplatform.model.User;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Data
@Service
public class ApiService {

    final PaymentRepo paymentRepo;
    String host = "localhost";

    @Autowired
    public ApiService(PaymentRepo paymentRepo) {
        this.paymentRepo = paymentRepo;
    }

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

    public void enforcePayment(Payment payment, double totalPrice) {
        long id = reserveMoney(payment.getProPayIdSender(), payment.getProPayIdRecipient(),
            payment.getBail());
        payment.setBailProPayId(id);
        id = reserveMoney(payment.getProPayIdSender(), payment.getProPayIdRecipient(),
            totalPrice);
        payment.setAmountProPayId(id);
        paymentRepo.save(payment);
    }

    private long reserveMoney(String proPayIdSender, String proPayIdRecipient, double amount) {
        try {
            URL url =
                new URL("http://" + host + ":8888/reservation/reserve/" + proPayIdSender
                    + "/" + proPayIdRecipient);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(conn.getOutputStream());
            out.writeBytes("amount=" + amount);
            out.flush();
            out.close();
            String response = convertHttpResponse(new InputStreamReader(conn.getInputStream()));
            ObjectMapper mapper = new ObjectMapper();
            ProPayReservation proPayReservation = mapper.readValue(response,
                ProPayReservation.class);
            return proPayReservation.getId();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return 0;
    }

    //converts any http response to String and return it
    private String convertHttpResponse(InputStreamReader inStream) {
        try {
            BufferedReader in = new BufferedReader(inStream);
            String input;
            StringBuilder inBuffer = new StringBuilder();
            while ((input = in.readLine()) != null) {
                inBuffer.append(input);
            }
            in.close();
            return inBuffer.toString();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return null;
    }

    public boolean isSolvent(User borrower, double amountOwed) {
        ProPay borrowerProPay = mapJson(borrower.getPropayId());

        double reservationAmount = 0;
        for (ProPayReservation reservation : borrowerProPay.getReservations()) {
            reservationAmount += reservation.getAmount();
        }

        return borrowerProPay.getAmount() - reservationAmount >= amountOwed;
    }

    public boolean isSolventFake(User borrower, double amountOwed) {
        return true;
    }
}
