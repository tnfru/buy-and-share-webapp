package de.hhu.propra.sharingplatform.service.payment;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ProPayApiTest {

    ProPayApi api;
    ProPayNetworkInterface networkInterface;

    @Before
    public void setup() {
        api = new ProPayApi();
        api.host = "google";
        networkInterface = mock(ProPayNetworkInterface.class);
        api.networkInterface = networkInterface;
    }

    @Test
    public void addMoney() {
        List<String> pathVariables = new ArrayList<>();
        pathVariables.add("account");
        pathVariables.add("foo");
        Map<String, String> parameters = new HashMap<>();
        parameters.put("amount", "1");

        api.addMoney("foo", 1);

        verify(networkInterface).buildRequest("POST",
            "http://google:8888/", pathVariables, parameters);
    }

    @Test
    public void reserve() {
        List<String> pathVariables = new ArrayList<>();
        pathVariables.add("reservation");
        pathVariables.add("reserve");
        pathVariables.add("foo");
        pathVariables.add("bar");
        Map<String, String> parameters = new HashMap<>();
        parameters.put("amount", "1");
        when(networkInterface.buildRequest(any(), any(), anyList(), anyMap()))
            .thenReturn("{\n" +
                "  \"amount\": 0,\n" +
                "  \"id\": 0\n" +
                "}");

        long id = api.reserveMoney("foo", "bar", 1);

        verify(networkInterface).buildRequest("POST",
            "http://google:8888/", pathVariables, parameters);
        assertEquals(id, 0);
    }

    @Test
    public void accountBalanceLiquid() {
        when(networkInterface.fetchJson("foo"))
            .thenReturn("{\n" +
                "  \"account\": \"string\",\n" +
                "  \"amount\": 100,\n" +
                "  \"reservations\": [\n" +
                "    {\n" +
                "      \"amount\": 5,\n" +
                "      \"id\": 1\n" +
                "    },\n" +
                "    {\n" +
                "      \"amount\": 1,\n" +
                "      \"id\": 2\n" +
                "    }\n" +
                "  ]\n" +
                "}");

        assertEquals(94, api.getAccountBalanceLiquid("foo"));
    }

    @Test
    public void accountBalance() {
        when(networkInterface.fetchJson("foo"))
            .thenReturn("{\n" +
                "  \"account\": \"string\",\n" +
                "  \"amount\": 100,\n" +
                "  \"reservations\": [\n" +
                "    {\n" +
                "      \"amount\": 5,\n" +
                "      \"id\": 1\n" +
                "    },\n" +
                "    {\n" +
                "      \"amount\": 1,\n" +
                "      \"id\": 2\n" +
                "    }\n" +
                "  ]\n" +
                "}");

        assertEquals(100, api.getAccountBalance("foo"));
    }
}