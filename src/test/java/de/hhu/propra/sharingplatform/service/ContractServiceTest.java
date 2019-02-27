package de.hhu.propra.sharingplatform.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hhu.propra.sharingplatform.dao.contractdao.ContractRepo;
import de.hhu.propra.sharingplatform.model.Offer;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.model.contracts.BorrowContract;
import de.hhu.propra.sharingplatform.model.contracts.Contract;
import de.hhu.propra.sharingplatform.model.items.ItemRental;
import de.hhu.propra.sharingplatform.service.payment.PaymentService;
import java.time.LocalDateTime;
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
@Import({Offer.class, Contract.class, ContractService.class})
public class ContractServiceTest {

    private BorrowContract contract;

    @MockBean
    ContractRepo contractRepo;

    @MockBean
    PaymentService paymentService;

    @Autowired
    ContractService contractService;

    @Before
    public void setUpTests() {
        User owner = new User();
        User borrower = new User();
        ItemRental itemRental = new ItemRental(owner);

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now();
        end = end.plusDays(3);
        Offer offer = new Offer(itemRental, borrower, start, end);
        contract = new BorrowContract(offer);
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