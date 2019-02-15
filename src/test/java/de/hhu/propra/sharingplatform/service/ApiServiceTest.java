package de.hhu.propra.sharingplatform.service;

import de.hhu.propra.sharingplatform.dto.ProPay;
import de.hhu.propra.sharingplatform.dto.ProPayReservation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@Import( {ApiService.class})
public class ApiServiceTest {

    private String fakeJson;

    @Before
    public void setUp() {
        this.fakeJson = "{\"account\":\"foo\"," + "\"amount\":5000.0,\"reservations\":[{\"id\":4," +
            "\"amount\":500.0}]}";
    }

    @Test
    public void mapJson() {
        ApiService apiService = mock(ApiService.class);
        when(apiService.fetchJson(anyString())).thenReturn(fakeJson);
        when(apiService.mapJson(anyString())).thenCallRealMethod();

        ProPay proPay = apiService.mapJson("foo");
        List<ProPayReservation> reservations = proPay.getReservations();

        assertEquals("foo", proPay.getAccount());
        assertEquals(5000.0, proPay.getAmount(), 0.01);
        assertEquals(4, reservations.get(0).getId());
        assertEquals(500.0, reservations.get(0).getAmount(), 0.01);
    }
}