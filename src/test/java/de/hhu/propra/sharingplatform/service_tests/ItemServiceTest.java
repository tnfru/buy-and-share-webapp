package de.hhu.propra.sharingplatform.service_tests;

import de.hhu.propra.sharingplatform.model.Item;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.modelDAO.ItemRepo;
import de.hhu.propra.sharingplatform.modelDAO.UserRepo;
import de.hhu.propra.sharingplatform.service.ItemService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
@Import({ItemService.class, User.class, Item.class,})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ItemServiceTest {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ItemRepo itemRepo;
    private Item item;
    @Autowired
    private ItemService itemService;

    @Before
    public void init() {
        User user = new User();
        user.setName("Test");
        userRepo.save(user);

        item = new Item();
        item.setName("TestItem");
        item.setDeposit(100);
        item.setPrice(20);
        item.setDescription("This is a test");
    }

    @Test
    public void persistOneItem() {
        itemService.persistItem(item, 1);

        assert itemRepo.findOneById(1).getOwner().getId() == 1;
    }
}
