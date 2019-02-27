package de.hhu.propra.sharingplatform.service.payment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProPayNetworkInterface {

    public ProPayNetworkInterface(){
    }

    public String buildRequest(String requestType, String serverAddress,
        List<String> pathVars,
        Map<String, String> parameters) {
        StringBuilder urlBuilder = new StringBuilder(serverAddress);
        // append path variables
        for (String pathVar : pathVars) {
            urlBuilder.append(pathVar).append("/");
        }
        urlBuilder.deleteCharAt(urlBuilder.lastIndexOf("/"));
        // append parameters
        urlBuilder.append("?");
        for (String parameter : parameters.keySet()) {
            urlBuilder.append(parameter).append("=").append(parameters.get(parameter)).append("&");
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
        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.REQUEST_TIMEOUT,
                "Couldnt reach Propayserver!");
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

    public String fetchJson(String userName, String host) {
        String url = "http://" + host + ":8888/account/" + userName;
        RestTemplate jsonResponse = new RestTemplate();

        String response;
        try {
            response = jsonResponse.getForObject(url, String.class);
        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.REQUEST_TIMEOUT,
                "Couldnt reach Propayserver!");
        }
        return response;
    }
}
