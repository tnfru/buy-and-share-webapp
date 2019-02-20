package de.hhu.propra.sharingplatform.controller;

import static org.junit.Assert.assertTrue;

import de.hhu.propra.sharingplatform.service.ItemService;
import de.hhu.propra.sharingplatform.service.OfferService;
import de.hhu.propra.sharingplatform.service.UserService;
import java.time.LocalDateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ResponseStatusException;

@RunWith(SpringRunner.class)
@Import(OfferController.class)
public class OfferControllerTest {

    @MockBean
    private ItemService itemService;

    @MockBean
    private UserService userService;

    @MockBean
    OfferService offerService;

    @Autowired
    private OfferController offerController;

    @Test
    public void getStartValidInput() {
        String stringTime = "23.01.2019 - 23.02.2019";

        LocalDateTime time = offerController.getStart(stringTime);

        assertTrue(time.isEqual(LocalDateTime.of(2019, 1, 23, 0, 0, 0)));

    }

    @Test(expected = ResponseStatusException.class)
    public void readTimeStartWrongInput() {
        String stringTime = "- 23.01.2019";

        offerController.readTime(stringTime, 0);
    }

    @Test(expected = ResponseStatusException.class)
    public void readTimeStartInvalidInput() {
        String stringTime = "2345 - 23.01.2019";

        offerController.readTime(stringTime, 0);
    }

    @Test
    public void getEndValidInput() {
        String stringTime = "23.01.2019 - 23.01.2019";

        LocalDateTime time = offerController.getEnd(stringTime);

        assertTrue(time.isEqual(LocalDateTime.of(2019, 1, 23, 23, 59, 59)));
    }

    @Test(expected = ResponseStatusException.class)
    public void readTimeEndWrongInput() {
        String stringTime = "23.01.2019 - ";

        offerController.readTime(stringTime, 1);
    }

    @Test(expected = ResponseStatusException.class)
    public void readTimeEndInvalidInput() {
        String stringTime = "23.01.2019 - 234";

        offerController.readTime(stringTime, 1);
    }
}