package de.hhu.propra.sharingplatform.service;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hhu.propra.sharingplatform.dao.ContractRepo;
import de.hhu.propra.sharingplatform.model.Contract;
import de.hhu.propra.sharingplatform.model.Item;
import de.hhu.propra.sharingplatform.model.Offer;
import de.hhu.propra.sharingplatform.model.User;
import java.time.LocalDateTime;

import de.hhu.propra.sharingplatform.service.payment.PaymentService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

@Ignore
@RunWith(SpringRunner.class)
@Import( {Offer.class, Contract.class, ContractService.class})
public class ContractServiceTest {

    private User owner;
    private User borrower;
    private Item item;
    private Offer offer;
    private Contract contract;

    @MockBean
    ContractRepo contractRepo;

    @MockBean
    PaymentService paymentService;

    @Autowired
    ContractService contractService;

    @Before
    public void setUpTests() {
        owner = new User();
        borrower = new User();
        item = new Item(owner);

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now();
        end = end.plusDays(3);
        offer = new Offer(item, borrower, start, end);
        contract = new Contract(offer);

    }

    @Test
    public void endContractTest() {
        when(contractRepo.findOneById(anyLong())).thenReturn(contract);

        Assert.assertEquals(contract.getExpectedEnd(), contract.getRealEnd());

        contractService.returnItem(anyLong(), anyString());

        ArgumentCaptor<Contract> argument = ArgumentCaptor.forClass(Contract.class);

        verify(paymentService, times(0)).createPayment(any());
        verify(contractRepo, times(1)).save(argument.capture());

        Assert.assertEquals(contract, argument.getValue());
        Assert.assertNotEquals(contract.getExpectedEnd(), contract.getRealEnd());
    }

    @Test(expected = NullPointerException.class)
    public void endContractNotInDbTest() {
        when(contractRepo.findOneById(anyLong())).thenReturn(null);

        contractService.returnItem(anyLong(), anyString());
    }

    @Test
    public void calcPriceTest() {
        ArgumentCaptor<Contract> argument = ArgumentCaptor.forClass(Contract.class);

        when(contractRepo.findOneById(anyLong())).thenReturn(contract);

        contractService.calcPrice(anyLong());

        verify(paymentService, times(1)).createPayment(argument.capture());

        Assert.assertEquals(contract, argument.getValue());
    }

    @Test
    public void calcPriceNotInDbTest() {
        ArgumentCaptor<Contract> argument = ArgumentCaptor.forClass(Contract.class);

        when(contractRepo.findOneById(anyLong())).thenReturn(null);

        contractService.calcPrice(anyLong());

        verify(paymentService, times(1)).createPayment(argument.capture());

        Assert.assertNull(argument.getValue());

    }
}