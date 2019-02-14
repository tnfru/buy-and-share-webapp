package de.hhu.propra.sharingplatform.service;

import de.hhu.propra.sharingplatform.dao.ItemRepo;
import de.hhu.propra.sharingplatform.dao.UserRepo;
import de.hhu.propra.sharingplatform.model.Item;
import de.hhu.propra.sharingplatform.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ItemServiceTest {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ItemRepo itemRepo;

    private Item item;
    private ItemService itemService;

    @Before
    public void init() {
        itemService = new ItemService(itemRepo, userRepo);

        User user = new User();
        user.setName("Test");
        User user2 = new User();
        user.setName("Test2");
        userRepo.save(user);
        userRepo.save(user2);

        item = new Item();
        item.setName("TestItem");
        item.setDeposit(100);
        item.setPrice(20);
        item.setDescription("This is a test");
        item.setLocation("Test-Location");
    }

    @Test
    public void persistOneItem() {
        itemService.persistItem(item, 1);

        assert itemRepo.findOneById(1).getOwner().getId() == 1;
    }

    @Test
    public void removeOneItemValidUser() {
        itemService.persistItem(item, 1);
        itemService.removeItem(1, 1);

        assert itemRepo.findOneById(1).isDeleted();
    }

    @Test
    public void removeOneItemInvalidUser() {
        itemService.persistItem(item, 1);
        itemService.removeItem(1, 2);

        assert !itemRepo.findOneById(1).isDeleted();
    }

    @Test
    public void dontPersistInvalidItem() {
        item.setLocation(null);
        itemService.persistItem(item, 1);

        assert itemRepo.findOneById(1) == null;
    }

    @Test
    public void getOneItemToEditValidUser() {
        itemService.persistItem(item, 1);
        Item editItem = itemService.getItem(1, 1);

        assert editItem.equals(item);
    }

    @Test
    public void getOneItemToEditInvalidUser() {
        itemService.persistItem(item, 1);
        Item editItem = itemService.getItem(1, 2);

        assert editItem == null;
    }

    @Test
    public void editItemValidItemAndUser() {
        itemService.persistItem(item, 1);
        Item editTemplate = itemService.getItem(1, 1);
        Item editItem = new Item();
        editItem.setDescription("This is edited");
        editItem.setLocation(editTemplate.getLocation());
        editItem.setPrice(editTemplate.getPrice());
        editItem.setDeposit(editTemplate.getDeposit());
        editItem.setName(editTemplate.getName());
        itemService.editItem(editItem, 1, 1);

        assert itemRepo.findOneById(1).getDescription().equals("This is edited");
    }

    @Test
    public void editItemValidItemAndInvalidUser() {
        itemService.persistItem(item, 1);
        Item editTemplate = itemService.getItem(1, 1);
        Item editItem = new Item();
        editItem.setDescription("This is edited");
        editItem.setLocation(editTemplate.getLocation());
        editItem.setPrice(editTemplate.getPrice());
        editItem.setDeposit(editTemplate.getDeposit());
        editItem.setName(editTemplate.getName());
        itemService.editItem(editItem, 1, 2);

        assert !itemRepo.findOneById(1).getDescription().equals("This is edited");
    }

    @Test
    public void editItemInvalidItemAndValidUser() {
        itemService.persistItem(item, 1);
        Item editTemplate = itemService.getItem(1, 1);
        Item editItem = new Item();
        editItem.setDescription(null);
        editItem.setLocation(editTemplate.getLocation());
        editItem.setPrice(editTemplate.getPrice());
        editItem.setDeposit(editTemplate.getDeposit());
        editItem.setName(editTemplate.getName());
        itemService.editItem(editItem, 1, 1);

        assert itemRepo.findOneById(1).getDescription() != null;
    }

    @Test
    public void setItemInvalidItemAndUser() {
        itemService.persistItem(item, 1);
        Item editTemplate = itemService.getItem(1, 1);
        Item editItem = new Item();
        editItem.setDescription(null);
        editItem.setLocation(editTemplate.getLocation());
        editItem.setPrice(editTemplate.getPrice());
        editItem.setDeposit(editTemplate.getDeposit());
        editItem.setName(editTemplate.getName());
        itemService.editItem(editItem, 1, 2);

        assert itemRepo.findOneById(1).equals(item);
    }

    @Test
    public void findOneItem() {
        itemRepo.save(item);
        
        assert itemRepo.findOneById(1) != null;
    }
}
