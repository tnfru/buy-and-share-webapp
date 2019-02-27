package de.hhu.propra.sharingplatform.model.contracts;

import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.model.items.ItemSale;
import de.hhu.propra.sharingplatform.service.payment.IPaymentApi;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SellContractTest {

    IPaymentApi api;

    SellContract contract;

    @Before
    public void prepare() {
        api = mock(IPaymentApi.class);
        User user = new User();
        user.setPropayId("bar");
        ItemSale item = new ItemSale(user);
        item.setPrice(10);
        contract = new SellContract(item, "foo", "bar");
    }

    @Test
    public void correctAmountSendToCorrectAccount() {
        when(api.getAccountBalanceLiquid(any())).thenReturn(10000);

        contract.pay(api);

        verify(api).transferMoney(10, "foo", "bar");
    }

    @Test
    public void notEnoughMoney() {
        boolean thrown = false;
        when(api.getAccountBalanceLiquid(any())).thenReturn(5);
        try {
            contract.pay(api);
        } catch (ResponseStatusException rse) {
            thrown = true;
            assertEquals("400 BAD_REQUEST \"Not enough money\"", rse.getMessage());
        }

        verify(api, times(0))
            .transferMoney(10, "foo", "bar");
    }
}