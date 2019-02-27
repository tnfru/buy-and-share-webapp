package de.hhu.propra.sharingplatform.model.contracts;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.model.items.ItemSale;
import de.hhu.propra.sharingplatform.service.payment.IPaymentApi;
import org.junit.Before;
import org.junit.Test;

public class SellContractTest {

    IPaymentApi api;

    SellContract contract;

    @Before
    public void prepare() {
        api = mock(IPaymentApi.class);
        User user = new User();
        user.setPropayId("bar");
        User user2 = new User();
        user2.setPropayId("foo");
        ItemSale item = new ItemSale(user);
        item.setPrice(10);
        contract = new SellContract(item, user2);
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