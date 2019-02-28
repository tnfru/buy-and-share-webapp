package de.hhu.propra.sharingplatform.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hhu.propra.sharingplatform.dao.ConflictRepo;
import de.hhu.propra.sharingplatform.dao.UserRepo;
import de.hhu.propra.sharingplatform.dto.Status;
import de.hhu.propra.sharingplatform.model.Conflict;
import de.hhu.propra.sharingplatform.model.Offer;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.model.contracts.BorrowContract;
import de.hhu.propra.sharingplatform.model.items.ItemRental;
import de.hhu.propra.sharingplatform.service.payment.IBankAccountService;
import de.hhu.propra.sharingplatform.service.payment.PaymentService;
import java.time.LocalDateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@Import({ConflictService.class})
public class ConflictServiceTest {

    @MockBean
    private ConflictRepo conflictRepo;

    @MockBean
    private UserService userService;

    @MockBean
    private PaymentService paymentService;

    @MockBean
    private UserRepo userRepo;

    @MockBean
    private PasswordEncoder encoder;

    @MockBean
    private IBankAccountService bank;

    @MockBean
    private ImageService imageSaver;

    @Autowired
    private ConflictService conflictService;

    private Conflict initConflict() {
        Offer offer = new Offer(new ItemRental(new User()), new User(), LocalDateTime.now(),
            LocalDateTime.now().plusDays(1));

        Conflict conflict = new Conflict();
        conflict.setStatus(Status.PENDING);
        conflict.setContract(new BorrowContract(offer));
        conflict.setRequester(new User());
        conflict.setDescription("description");

        when(conflictRepo.findOneById(anyLong())).thenReturn(conflict);
        return conflict;
    }

    @Test
    public void createConflict() {
        User requester = new User();

        Offer offer = new Offer(new ItemRental(new User()), new User(), LocalDateTime.now(),
            LocalDateTime.now().plusDays(1));

        BorrowContract contract = new BorrowContract(offer);

        when(userService.fetchUserByAccountName("account")).thenReturn(requester);

        ArgumentCaptor<Conflict> argument = ArgumentCaptor.forClass(Conflict.class);

        Conflict conflict = conflictService.createConflict(contract, "account", "description");

        assertEquals(Status.PENDING, conflict.getStatus());
        assertEquals(contract, conflict.getContract());
        assertEquals(requester, conflict.getRequester());
        assertEquals("description", conflict.getDescription());

        verify(conflictRepo, times(1)).save(argument.capture());

        assertEquals(conflict, argument.getValue());
    }

    @Test
    public void setStatusContinued() {
        Conflict conflict = initConflict();

        ArgumentCaptor<Conflict> argument = ArgumentCaptor.forClass(Conflict.class);

        conflictService.setStatus(Status.CONTINUED, 1L);

        verify(conflictRepo, times(1)).save(argument.capture());

        assertEquals(conflict, argument.getValue());
        assertEquals(Status.CONTINUED, argument.getValue().getStatus());
    }

    @Test
    public void setStatusCanceld() {
        Conflict conflict = initConflict();

        ArgumentCaptor<Conflict> argument = ArgumentCaptor.forClass(Conflict.class);

        conflictService.setStatus(Status.CANCELED, 1L);

        verify(conflictRepo, times(1)).save(argument.capture());

        assertEquals(conflict, argument.getValue());
        assertEquals(Status.CANCELED, argument.getValue().getStatus());
    }

    @Test
    public void punishConflict() {
        Conflict conflict = initConflict();

        ArgumentCaptor<BorrowContract> argument = ArgumentCaptor.forClass(BorrowContract.class);

        conflictService.punish(1L);

        verify(paymentService, times(1)).punishBailReservation(argument.capture());

        assertEquals(Status.PUNISHED_BAIL, conflict.getStatus());
    }

}