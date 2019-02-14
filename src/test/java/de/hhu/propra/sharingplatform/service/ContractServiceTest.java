package de.hhu.propra.sharingplatform.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hhu.propra.sharingplatform.model.Contract;
import de.hhu.propra.sharingplatform.model.Item;
import de.hhu.propra.sharingplatform.model.Offer;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.modelDAO.ContractRepo;
import java.util.Date;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
@Import({Offer.class, Contract.class, ContractService.class})
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

        Date start = new Date();
        Date end = new Date();
        end.setTime(start.getTime() + 1337);
        offer = new Offer(item, borrower, start, end);
        contract = new Contract(offer);
    }

    @Test
    public void endContractTest() {
        ArgumentCaptor<Contract> argument = ArgumentCaptor.forClass(Contract.class);

        when(contractRepo.findOneById(anyLong())).thenReturn(contract);

        Assert.assertEquals(contract.getExpectedEnd(), contract.getRealEnd());

        contractService.endContract(anyLong());

        verify(paymentService, times(0)).create(any());
        verify(contractRepo, times(1)).save(argument.capture());

        Assert.assertEquals(contract, argument.getValue());

        Assert.assertNotEquals(contract.getExpectedEnd(), contract.getRealEnd());
    }

    @Test(expected = NullPointerException.class)
    public void endContractNotInDbTest() {
        when(contractRepo.findOneById(anyLong())).thenReturn(null);

        contractService.endContract(anyLong());
    }

    @Test
    public void calcPriceTest() {
        ArgumentCaptor<Contract> argument = ArgumentCaptor.forClass(Contract.class);

        when(contractRepo.findOneById(anyLong())).thenReturn(contract);

        contractService.calcPrice(anyLong());

        verify(paymentService, times(1)).create(argument.capture());

        Assert.assertEquals(contract, argument.getValue());
    }

    @Test
    public void calcPriceNotInDbTest() {
        ArgumentCaptor<Contract> argument = ArgumentCaptor.forClass(Contract.class);

        when(contractRepo.findOneById(anyLong())).thenReturn(null);

        contractService.calcPrice(anyLong());

        verify(paymentService, times(1)).create(argument.capture());

        Assert.assertNull(argument.getValue());

    }
}