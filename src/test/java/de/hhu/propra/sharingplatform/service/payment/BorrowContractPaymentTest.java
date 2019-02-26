package de.hhu.propra.sharingplatform.service.payment;

import de.hhu.propra.sharingplatform.dto.Status;
import de.hhu.propra.sharingplatform.model.Conflict;
import de.hhu.propra.sharingplatform.model.Item;
import de.hhu.propra.sharingplatform.model.Offer;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.model.contracts.BorrowContract;
import de.hhu.propra.sharingplatform.model.payments.BorrowPayment;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@Import( {BorrowContract.class, BorrowPayment.class, IPaymentApi.class})
public class BorrowContractPaymentTest {

    @MockBean
    IPaymentApi paymentApi;

    BorrowContract borrowContract;

    @Before
    public void startup() {
        User user = new User();
        user.setName("Owner");
        user.setPropayId("Owner");
        User user2 = new User();
        user2.setName("Borrower");
        user2.setPropayId("Borrower");
        Item item = new Item(user);
        item.setPrice(100);
        item.setBail(1000);
        Offer offer = new Offer(item, user2, LocalDateTime.now(), LocalDateTime.now().plusDays(2));
        borrowContract = new BorrowContract(offer);
        BorrowPayment borrowPayment = new BorrowPayment("Borrower", "Owner", 300, 1000);
        borrowContract.setBorrowPayment(borrowPayment);
    }

    @Test
    public void prepareContractCallsApi() {
        when(paymentApi.reserveMoney(anyString(), anyString(), eq(1000))).thenReturn((long) 1);
        when(paymentApi.reserveMoney(anyString(), anyString(), eq(300))).thenReturn((long) 2);

        borrowContract.prepare(paymentApi);

        verify(paymentApi, times(2)).reserveMoney(anyString(), anyString(), anyInt());
    }

    @Test
    public void prepareContractHasRightBorrowPayment() {
        when(paymentApi.reserveMoney(anyString(), anyString(), eq(1000))).thenReturn((long) 1);
        when(paymentApi.reserveMoney(anyString(), anyString(), eq(300))).thenReturn((long) 2);

        borrowContract.prepare(paymentApi);

        Assert.assertEquals(1, borrowContract.getBorrowPayment().getBailProPayId());
        Assert.assertEquals(2, borrowContract.getBorrowPayment().getAmountProPayId());
        Assert.assertEquals(1000, borrowContract.getBorrowPayment().getBail());
        Assert.assertEquals(300, borrowContract.getBorrowPayment().getAmount());
    }

    @Test
    public void freeBailBorrowContract() {
        BorrowPayment bp = borrowContract.getBorrowPayment();
        bp.setBailProPayId(2);
        bp.setProPayIdSender("Borrower");
        borrowContract.setBorrowPayment(bp);

        borrowContract.freeBail(paymentApi);

        verify(paymentApi, times(1)).freeReservation(2, "Borrower");
    }

    @Test
    public void freeChangeBorrowContract() {
        BorrowPayment bp = borrowContract.getBorrowPayment();
        bp.setAmountProPayId(1);
        bp.setProPayIdSender("Borrower");
        borrowContract.setBorrowPayment(bp);

        borrowContract.freeCharge(paymentApi);

        verify(paymentApi, times(1)).freeReservation(1, "Borrower");
    }

    @Test
    public void punishBailBorrowContract() {
        BorrowPayment bp = borrowContract.getBorrowPayment();
        bp.setBailProPayId(2);
        bp.setProPayIdSender("Borrower");
        borrowContract.setBorrowPayment(bp);

        borrowContract.punishBail(paymentApi);

        verify(paymentApi, times(1)).punishReservation(2, "Borrower");
    }

    @Test
    public void payBorrowContract() {
        BorrowPayment bp = borrowContract.getBorrowPayment();
        bp.setAmount(300);
        bp.setProPayIdSender("Borrower");
        bp.setProPayIdRecipient("Owner");
        borrowContract.setBorrowPayment(bp);

        borrowContract.pay(paymentApi);

        verify(paymentApi, times(1))
            .transferMoney(300, "Borrower", "Owner");
    }

    @Test
    public void freeChargeBorrowContract() {
        BorrowPayment bp = borrowContract.getBorrowPayment();
        bp.setAmountProPayId(2);
        bp.setProPayIdSender("Borrower");
        borrowContract.setBorrowPayment(bp);

        borrowContract.freeCharge(paymentApi);

        verify(paymentApi, times(1)).freeReservation(2, "Borrower");
    }

    @Test
    public void returnItemBorrowContract() {
        borrowContract.setBorrowPayment(new BorrowPayment("Borrower", "Owner", 300, 1000));

        borrowContract.returnItem();

        Assert.assertEquals(100, borrowContract.getBorrowPayment().getAmount());
        //Only compares the full days
        Assert.assertEquals(LocalDateTime.now().toLocalDate(), borrowContract.getRealEnd().toLocalDate());
    }

    @Test
    public void getOpenConflictsOneOpen() {
        List<Conflict> conflicts = new ArrayList<>();
        Conflict conflict = new Conflict();
        conflict.setStatus(Status.PENDING);
        Conflict conflict1 = new Conflict();
        conflict1.setStatus(Status.PUNISHED_BAIL);
        conflicts.add(conflict);
        conflicts.add(conflict1);

        borrowContract.setConflicts(conflicts);

        Assert.assertEquals(1, borrowContract.getOpenConflicts().size());
    }

    @Test
    public void getOpenConflictsTwoOpen() {
        List<Conflict> conflicts = new ArrayList<>();
        Conflict conflict = new Conflict();
        conflict.setStatus(Status.PENDING);
        Conflict conflict1 = new Conflict();
        conflict1.setStatus(Status.PENDING);
        conflicts.add(conflict);
        conflicts.add(conflict1);

        borrowContract.setConflicts(conflicts);

        Assert.assertEquals(2, borrowContract.getOpenConflicts().size());
    }

    @Test
    public void getOpenConflictsNoneOpen() {
        List<Conflict> conflicts = new ArrayList<>();
        Conflict conflict = new Conflict();
        conflict.setStatus(Status.BAIL_FREED);
        Conflict conflict1 = new Conflict();
        conflict1.setStatus(Status.PUNISHED_BAIL);
        conflicts.add(conflict);
        conflicts.add(conflict1);

        borrowContract.setConflicts(conflicts);

        Assert.assertEquals(0, borrowContract.getOpenConflicts().size());
    }
}
