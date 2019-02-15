package de.hhu.propra.sharingplatform.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.hhu.propra.sharingplatform.dto.ProPay;
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

    public ProPay mapJson(String userName) throws IOException {
        String jsonResponse = fetchJson(userName);
        ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(jsonResponse, ProPay.class);
    }
}
