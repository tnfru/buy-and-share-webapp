package de.hhu.propra.sharingplatform.service.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hhu.propra.sharingplatform.dao.ContractRepo;
import de.hhu.propra.sharingplatform.model.Contract;
import de.hhu.propra.sharingplatform.model.Item;
import de.hhu.propra.sharingplatform.model.Offer;
import de.hhu.propra.sharingplatform.model.User;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.server.ResponseStatusException;

public class OfferValidatorTest2 {

    private User owner;
    private User borrower;
    private Item item;
    private LocalDateTime start;
    private LocalDateTime end;

    @MockBean
    private ContractRepo contractRepo;

    @Before
    public void setUpTests() {
        owner = new User();
        borrower = new User();
        item = new Item(owner);
        item.setBail(234.0);

        start = LocalDateTime.now().plusDays(3);
        end = start.plusDays(1);
        Offer offer1 = new Offer(item, borrower, start, end);
        Contract contract1 = new Contract(offer1);
        start = end.plusDays(3);
        end = start.plusDays(4);
        Offer offer2 = new Offer(item, borrower, start, end);
        Contract contract2 = new Contract(offer2);

        List<Contract> contractList = new ArrayList<>();
        contractList.add(contract1);
        contractList.add(contract2);

        contractRepo = mock(ContractRepo.class);
        when(contractRepo.findAllByItem(item)).thenReturn(contractList);
    }


    @Test
    public void periodIsBeforeAvailable() {
        boolean thrown = false;

        start = LocalDateTime.now();
        end = start.plusDays(2);

        try {
            OfferValidator.periodIsAvailable(contractRepo, item, start, end);
        } catch (ResponseStatusException responseException) {
            thrown = true;
        }
        assertFalse(thrown);
    }

    @Test
    public void periodIsNotAvailable() {
        boolean thrown = false;

        start = LocalDateTime.now().plusDays(3);
        end = start.plusDays(2);

        try {
            OfferValidator.periodIsAvailable(contractRepo, item, start, end);
        } catch (ResponseStatusException responseException) {
            thrown = true;
            assertEquals("400 BAD_REQUEST \"Invalid period\"",
                responseException.getMessage());
        }
        assertTrue(thrown);
    }

    @Test
    public void periodIsAfterAvailable() {
        boolean thrown = false;

        start = LocalDateTime.now().plusDays(14);
        end = start.plusDays(2);

        try {
            OfferValidator.periodIsAvailable(contractRepo, item, start, end);
        } catch (ResponseStatusException responseException) {
            thrown = true;
        }
        assertFalse(thrown);
    }
}