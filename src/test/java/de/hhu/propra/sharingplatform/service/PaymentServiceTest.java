package de.hhu.propra.sharingplatform.service;

import de.hhu.propra.sharingplatform.dao.PaymentRepo;
import de.hhu.propra.sharingplatform.model.Contract;
import de.hhu.propra.sharingplatform.model.Item;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class PaymentServiceTest {

    @MockBean
    private PaymentRepo paymentRepo;

    @MockBean
    private ApiService apiService;

    private PaymentService paymentService;

    @Before
    public void setUp() {
        this.paymentService = new PaymentService(paymentRepo, apiService);
    }

    @Test
    public void correctPrice() {
        Date start = mock(Date.class);
        Date end = mock(Date.class);
        when(start.getTime()).thenReturn((long) (1337 * (1000.0 * 60 * 60 * 24)));
        when(end.getTime()).thenReturn((long) (7331 * (1000.0 * 60 * 60 * 24)));

        Item item = mock(Item.class);
        when(item.getPrice()).thenReturn(10.0);

        Contract contract = mock(Contract.class);
        when(contract.getStart()).thenReturn(start);
        when(contract.getRealEnd()).thenReturn(end);
        when(contract.getItem()).thenReturn(item);

        assertEquals(59940.0, paymentService.calculateTotalPrice(contract), 0.01);
    }

}