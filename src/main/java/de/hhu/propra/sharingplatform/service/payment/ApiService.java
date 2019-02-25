package de.hhu.propra.sharingplatform.service.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.hhu.propra.sharingplatform.dao.PaymentRepo;
import de.hhu.propra.sharingplatform.dto.ProPay;
import de.hhu.propra.sharingplatform.dto.ProPayReservation;
import de.hhu.propra.sharingplatform.model.Payment;
import de.hhu.propra.sharingplatform.model.User;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
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

@Component
@Data
@Service
public class ApiService implements IPaymentApi {

    final PaymentRepo paymentRepo;
    String host = "localhost";

    @Autowired
    public ApiService(PaymentRepo paymentRepo) {
        this.paymentRepo = paymentRepo;
    }

    @Deprecated
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

    @Deprecated
    ProPay mapJson(String userName) {
        String jsonResponse = fetchJson(userName);
        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.readValue(jsonResponse, ProPay.class);
        } catch (IOException ioException) {
            ioException.printStackTrace();
            return null;
        }
    }

    @Override
    public void enforcePayment(Payment payment, int totalPrice) {
        long id = reserveMoney(payment.getProPayIdSender(), payment.getProPayIdRecipient(),
            payment.getBail());
        payment.setBailProPayId(id);
        id = reserveMoney(payment.getProPayIdSender(), payment.getProPayIdRecipient(),
            totalPrice);
        payment.setAmountProPayId(id);
        paymentRepo.save(payment);
    }

    @Override
    public void addMoney(String proPayId, int amount) {
        createAccountOrAddMoney(proPayId, amount);
    }

    @Override
    public void transferMoney(int amount, String fromAccount, String toAccount)
                throws PaymentException {
        List<String> path = new ArrayList<>();
        path.add("account");
        path.add(fromAccount);
        path.add("transfer");
        path.add(toAccount);
        Map<String, String> parameters = new HashMap<>();
        parameters.put("amount", Integer.toString(amount));

        buildRequest("POST", "http://" + host + ":8888/",
            path, parameters);
    }

    @Override
    public long reserveMoney(String proPayIdSender, String proPayIdRecipient, int amount) {
        List<String> pathVariables = new ArrayList<>();
        pathVariables.add("reservation");
        pathVariables.add("reserve");
        pathVariables.add(proPayIdSender);
        pathVariables.add(proPayIdRecipient);
        Map<String, String> parameters = new HashMap<>();
        parameters.put("amount", Integer.toString(amount));

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

    private void createAccountOrAddMoney(String proPayId, int amount) {
        List<String> pathVariables = new ArrayList<>();
        pathVariables.add("account");
        pathVariables.add(proPayId);
        Map<String, String> parameters = new HashMap<>();
        parameters.put("amount", Integer.toString(amount));

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

    @Override
    public boolean isSolvent(User borrower, int amountOwed) {
        ProPay borrowerProPay = mapJson(borrower.getPropayId());

        int reservationAmount = 0;
        for (ProPayReservation reservation : borrowerProPay.getReservations()) {
            reservationAmount += reservation.getAmount();
        }

        return borrowerProPay.getAmount() - reservationAmount >= amountOwed;
    }

    @Override
    public void freeReservation(long amountProPayId, String proPayIdSender) {
        List<String> path = new ArrayList<>();
        path.add("reservation");
        path.add("release");
        path.add(proPayIdSender);
        Map<String, String> parameters = new HashMap<>();
        parameters.put("reservationId", Long.toString(amountProPayId));

        buildRequest("POST", "http://" + host + ":8888/",
            path, parameters);
    }

    @Override
    public void transferMoney(Payment paymentInfo) {
        List<String> path = new ArrayList<>();
        path.add("account");
        path.add(paymentInfo.getProPayIdSender());
        path.add("transfer");
        path.add(paymentInfo.getProPayIdRecipient());
        Map<String, String> parameters = new HashMap<>();
        parameters.put("amount", Integer.toString(paymentInfo.getAmount()));

        buildRequest("POST", "http://" + host + ":8888/",
            path, parameters);
    }

    @Override
    public void punishReservation(long bailProPayId, String proPayIdSender) {
        List<String> path = new ArrayList<>();
        path.add("reservation");
        path.add("punish");
        path.add(proPayIdSender);
        Map<String, String> parameters = new HashMap<>();
        parameters.put("reservationId", Long.toString(bailProPayId));

        buildRequest("POST", "http://" + host + ":8888/",
            path, parameters);

    }

    @Override
    public int getAccountBalance(String proPayId) {
        return mapJson(proPayId).getAmount();
    }
}
