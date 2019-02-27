package de.hhu.propra.sharingplatform.service.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.hhu.propra.sharingplatform.dao.PaymentRepo;
import de.hhu.propra.sharingplatform.dto.ProPay;
import de.hhu.propra.sharingplatform.dto.ProPayReservation;
import de.hhu.propra.sharingplatform.service.ApiService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProPayApi implements IPaymentApi {

    private final PaymentRepo paymentRepo;
    String host = "localhost";

    @Autowired
    public ProPayApi(PaymentRepo paymentRepo) {
        this.paymentRepo = paymentRepo;
    }

    @Override
    public void addMoney(String proPayId, int amount) {
        createAccountOrAddMoney(proPayId, amount);
    }

    @Override
    public void transferMoney(int amount, String fromAccount, String toAccount) {
        List<String> path = new ArrayList<>();
        path.add("account");
        path.add(fromAccount);
        path.add("transfer");
        path.add(toAccount);
        Map<String, String> parameters = new HashMap<>();
        parameters.put("amount", Integer.toString(amount));

        ApiService.buildRequest("POST", "http://" + host + ":8888/",
            path, parameters);
    }

    @Override
    public int getAccountReservations(String account) {
        ProPay borrowerProPay = mapJsonToPropay(account);
        int reservationAmount = 0;
        if (borrowerProPay != null) {
            for (ProPayReservation reservation : borrowerProPay.getReservations()) {
                reservationAmount += reservation.getAmount();
            }
        }
        return reservationAmount;
    }

    @Override
    public int getAccountBalanceLiquid(String account) {
        return getAccountBalance(account) - getAccountReservations(account);
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

        String response = ApiService.buildRequest("POST", "http://" + host + ":8888/",
            pathVariables, parameters);

        ObjectMapper mapper = new ObjectMapper();
        ProPayReservation proPayReservation;
        try {
            proPayReservation = mapper.readValue(response,
                ProPayReservation.class);
            return proPayReservation.getId();
        } catch (IOException ioException) {
            ioException.printStackTrace();
            return -1;
        }
    }

    @Override
    public void freeReservation(long amountProPayId, String proPayIdSender) {
        List<String> path = new ArrayList<>();
        path.add("reservation");
        path.add("release");
        path.add(proPayIdSender);
        Map<String, String> parameters = new HashMap<>();
        parameters.put("reservationId", Long.toString(amountProPayId));

        ApiService.buildRequest("POST", "http://" + host + ":8888/",
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

        ApiService.buildRequest("POST", "http://" + host + ":8888/",
            path, parameters);

    }

    @Override
    public int getAccountBalance(String proPayId) {
        return mapJsonToPropay(proPayId).getAmount();
    }

    private void createAccountOrAddMoney(String proPayId, int amount) {
        List<String> pathVariables = new ArrayList<>();
        pathVariables.add("account");
        pathVariables.add(proPayId);
        Map<String, String> parameters = new HashMap<>();
        parameters.put("amount", Integer.toString(amount));

        ApiService.buildRequest("POST", "http://" + host + ":8888/",
            pathVariables, parameters);
    }

    private ProPay mapJsonToPropay(String userName) {
        String jsonResponse = ApiService.fetchJson(host, userName);
        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.readValue(jsonResponse, ProPay.class);
        } catch (IOException ioException) {
            ioException.printStackTrace();
            return null;
        }
    }
}
