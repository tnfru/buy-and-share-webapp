package de.hhu.propra.sharingplatform.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.hhu.propra.sharingplatform.dao.PaymentRepo;
import de.hhu.propra.sharingplatform.dto.ProPay;
import de.hhu.propra.sharingplatform.dto.ProPayReservation;
import de.hhu.propra.sharingplatform.model.Payment;
import de.hhu.propra.sharingplatform.model.User;
import java.net.ConnectException;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.server.ResponseStatusException;


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

        String response;
        try {
            response = jsonResponse.getForObject(url, String.class);
        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.REQUEST_TIMEOUT,
                "Couldnt reach Propayserver.");
        }
        return response;
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
        List<String> pathVariables = new ArrayList<>();
        pathVariables.add("reservation");
        pathVariables.add("reserve");
        pathVariables.add(proPayIdSender);
        pathVariables.add(proPayIdRecipient);
        Map<String, String> parameters = new HashMap<>();
        parameters.put("amount", Double.toString(amount));

        String response = buildRequest("POST", "http://" + host + ":8888/",
            pathVariables, parameters);

        ObjectMapper mapper = new ObjectMapper();
        ProPayReservation proPayReservation = null;
        try {
            proPayReservation = mapper.readValue(response,
                ProPayReservation.class);
            return proPayReservation.getId();
        } catch (IOException ioException) {
            ioException.printStackTrace();
            return -1;
        }
    }

    public void createAccount(String proPayId, double amount) {
        List<String> pathVariables = new ArrayList<>();
        pathVariables.add("account");
        pathVariables.add(proPayId);
        Map<String, String> parameters = new HashMap<>();
        parameters.put("amount", Double.toString(amount));

        buildRequest("POST", "http://" + host + ":8888/",
            pathVariables, parameters);
    }


    private String buildRequest(String requestType, String serverAddress, List<String> pathVars,
        Map<String, String> parameters) {
        StringBuilder urlBuilder = new StringBuilder(serverAddress);
        // append path variables
        for (String pathVar : pathVars) {
            urlBuilder.append(pathVar + "/");
        }
        urlBuilder.deleteCharAt(urlBuilder.lastIndexOf("/"));
        // append parameters
        urlBuilder.append("?");
        for (String parameter : parameters.keySet()) {
            urlBuilder.append(parameter + "=" + parameters.get(parameter) + "&");
        }
        urlBuilder.deleteCharAt(urlBuilder.lastIndexOf("&"));
        URL url;
        try {
            url = new URL(urlBuilder.toString());
        } catch (MalformedURLException malFormedException) {
            malFormedException.printStackTrace();
            return "-1";
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(requestType);
            conn.setDoOutput(true);
            return convertHttpResponse(new InputStreamReader(conn.getInputStream()));
        } catch (IOException ioException) {
            ioException.printStackTrace();
            return "-1";
        }
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

    public void freeReservation(long amountProPayId, String proPayIdSender) {

    }

    public void transferMoney(Payment paymentInfo) {

    }

    public void punishReservation(long bailProPayId, String proPayIdSender) {

    }

    public void addAmount(String proPayIdSender, double amount) {
        try {
            URL url =
                new URL("http://" + host + ":8888/account/" + proPayIdSender);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(conn.getOutputStream());
            out.writeBytes("amount=" + amount);
            out.flush();
            out.close();
            convertHttpResponse(new InputStreamReader(conn.getInputStream()));
        } catch (ConnectException connectException) {
            throw new ResponseStatusException(HttpStatus.REQUEST_TIMEOUT,
                "Couldnt reach Propayserver.");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
