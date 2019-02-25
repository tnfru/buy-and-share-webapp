package de.hhu.propra.sharingplatform.service.payment;

import de.hhu.propra.sharingplatform.dto.ProPay;
import de.hhu.propra.sharingplatform.dto.ProPayReservation;
import de.hhu.propra.sharingplatform.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class ApiServiceTest {

    private String fakeJson;

    @Test
    public void jsonMapped() {
        this.fakeJson = "{\"account\":\"foo\"," + "\"amount\":5000.0,\"reservations\":[{\"id\":4,"
            + "\"amount\":500.0}]}";
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

    @Test
    public void multipleReservations() {
        this.fakeJson = "{\"account\":\"foo\",\"amount\":5000.0,\"reservations\":[{\"id\":4,"
            + "\"amount\":500.0},{\"id\":5,\"amount\":1337.0}]}";
        ApiService apiService = mock(ApiService.class);
        when(apiService.fetchJson(anyString())).thenReturn(fakeJson);
        when(apiService.mapJson(anyString())).thenCallRealMethod();

        ProPay proPay = apiService.mapJson("foo");
        List<ProPayReservation> reservations = proPay.getReservations();

        assertEquals("foo", proPay.getAccount());
        assertEquals(5000.0, proPay.getAmount(), 0.01);
        assertEquals(4, reservations.get(0).getId());
        assertEquals(500.0, reservations.get(0).getAmount(), 0.01);
        assertEquals(5, reservations.get(1).getId());
        assertEquals(1337.0, reservations.get(1).getAmount(), 0.01);
    }

    @Test
    public void isSolvent() {
        this.fakeJson = "{\"account\":\"foo\"," + "\"amount\":5000.0,\"reservations\":[{\"id\":4,"
            + "\"amount\":500.0}]}";

        User fakeUser = mock(User.class);
        when(fakeUser.getPropayId()).thenReturn("foo");

        ApiService apiService = mock(ApiService.class);
        when(apiService.fetchJson(anyString())).thenReturn(fakeJson);
        when(apiService.mapJson(anyString())).thenCallRealMethod();
        when(apiService.isSolvent(any(), anyInt())).thenCallRealMethod();

        assertTrue(apiService.isSolvent(fakeUser, 1000));
    }

    @Test
    public void notSolvent() {
        this.fakeJson = "{\"account\":\"foo\"," + "\"amount\":5000.0,\"reservations\":[{\"id\":4,"
            + "\"amount\":500.0}]}";

        User fakeUser = mock(User.class);
        when(fakeUser.getPropayId()).thenReturn("foo");

        ApiService apiService = mock(ApiService.class);
        when(apiService.fetchJson(anyString())).thenReturn(fakeJson);
        when(apiService.mapJson(anyString())).thenCallRealMethod();
        when(apiService.isSolvent(any(), anyInt())).thenCallRealMethod();

        assertFalse(apiService.isSolvent(fakeUser, 10000));
    }

    @Test
    public void notSolventReservations() {
        this.fakeJson = "{\"account\":\"foo\"," + "\"amount\":700.0,\"reservations\":[{\"id\":4,"
            + "\"amount\":500.0}]}";

        User fakeUser = mock(User.class);
        when(fakeUser.getPropayId()).thenReturn("foo");

        ApiService apiService = mock(ApiService.class);
        when(apiService.fetchJson(anyString())).thenReturn(fakeJson);
        when(apiService.mapJson(anyString())).thenCallRealMethod();
        when(apiService.isSolvent(any(), anyInt())).thenCallRealMethod();

        assertFalse(apiService.isSolvent(fakeUser, 600));
    }
}
