package de.hhu.propra.sharingplatform.model.contracts;

import de.hhu.propra.sharingplatform.model.Item;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.service.payment.IPaymentApi;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;
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
        Item item = new Item(user);
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
        when(api.getAccountBalanceLiquid(any())).thenReturn(5);

        contract.pay(api);

        verify(api, times(0)).transferMoney(10, "foo", "bar");
    }
}