package de.hhu.propra.sharingplatform.service;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hhu.propra.sharingplatform.dao.ItemRepo;
import de.hhu.propra.sharingplatform.dao.OfferRepo;
import de.hhu.propra.sharingplatform.dao.contractdao.ContractRepo;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.model.items.Item;
import de.hhu.propra.sharingplatform.model.items.ItemRental;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ResponseStatusException;

@RunWith(SpringRunner.class)
public class ItemRentalServiceTest {

    @MockBean
    private UserService userService;

    @MockBean
    private ItemRepo itemRepo;

    @MockBean
    ContractRepo contractRepo;

    @MockBean
    OfferRepo offerRepo;

    private ItemRental itemRental;
    private User user;
    private ItemService itemService;
    private ImageService imageService;

    @Before
    public void init() {
        imageService = mock(ImageService.class);
        itemService = new ItemService(userService, imageService, itemRepo, contractRepo, offerRepo);

        user = new User();
        user.setName("Test");
        user.setId((long) 1);

        itemRental = new ItemRental(user);
        itemRental.setId((long) 1);
        itemRental.setName("TestItem");
        itemRental.setOwner(user);
        itemRental.setBail(100);
        itemRental.setDailyRate(20);
        itemRental.setDescription("This is a test");
        itemRental.setLocation("Test-Location");
    }

    @Test
    public void persistOneValidItem() {
        ArgumentCaptor<ItemRental> argument = ArgumentCaptor.forClass(ItemRental.class);
        when(userService.fetchUserById(1L)).thenReturn(user);

        itemService.persistItem(itemRental, 1);

        verify(itemRepo, times(2)).save(argument.capture());
        assertEquals(itemRental, argument.getValue());
        assertEquals(1, (long) argument.getValue().getOwner().getId());
    }

    @Test
    public void removeOneItemValidUser() {
        Optional<ItemRental> optional = Optional.ofNullable(itemRental);
        when(itemRepo.findById(anyLong())).thenReturn(optional);

        itemService.removeItem(1L, 1);

        assertTrue(itemRental.isDeleted());
    }

    @Test
    public void removeOneItemInvalidUser() {
        boolean thrown = false;
        Optional<ItemRental> optional = Optional.ofNullable(itemRental);
        when(itemRepo.findById(anyLong())).thenReturn(optional);

        try {
            itemService.removeItem(1L, 2);
        } catch (ResponseStatusException rse) {
            thrown = true;
            assertEquals("403 FORBIDDEN \"Not your Item\"", rse.getMessage());
        }
        assertFalse(itemRental.isDeleted());
        assertTrue(thrown);
    }

    @Test
    public void dontPersistInvalidItem() {
        boolean thrown = false;
        itemRental.setLocation(null);
        when(userService.fetchUserById(1L)).thenReturn(user);
        try {
            itemService.persistItem(itemRental, 1);
        } catch (ResponseStatusException rse) {
            thrown = true;
            assertEquals("400 BAD_REQUEST \"Invalid Location\"", rse.getMessage());
        }
        verify(itemRepo, times(0)).save(any());
        assertTrue(thrown);
    }

    @Test
    public void editItemValidItemAndUser() {
        ItemRental editItemRental = new ItemRental(user);
        editItemRental.setDescription("This is edited");
        editItemRental.setLocation(itemRental.getLocation());
        editItemRental.setDailyRate(itemRental.getDailyRate());
        editItemRental.setBail(itemRental.getBail());
        editItemRental.setName(itemRental.getName());
        editItemRental.setOwner(user);
        ArgumentCaptor<ItemRental> argument = ArgumentCaptor.forClass(ItemRental.class);
        Optional<ItemRental> optional = Optional.ofNullable(itemRental);

        when(itemRepo.findById(1L)).thenReturn(optional);

        itemService.editItem(editItemRental, 1L, 1L);

        verify(itemRepo, times(2)).save(argument.capture());
        assertEquals(argument.getValue().getDescription(), editItemRental.getDescription());
    }

    @Test
    public void editItemValidItemAndInvalidUser() {
        boolean thrown = false;
        ItemRental editItemRental = new ItemRental(user);
        editItemRental.setDescription("This is edited");
        editItemRental.setLocation(itemRental.getLocation());
        editItemRental.setDailyRate(itemRental.getDailyRate());
        editItemRental.setBail(itemRental.getBail());
        editItemRental.setName(itemRental.getName());

        when(itemRepo.findById(1L)).thenReturn(Optional.empty());
        try {
            itemService.editItem(editItemRental, 1, 2);
        } catch (ResponseStatusException rse) {
            thrown = true;
            assertEquals("404 NOT_FOUND \"Item not Found\"", rse.getMessage());
        }

        verify(itemRepo, times(0)).save(any());
        assertTrue(thrown);
    }

    @Test
    public void editItemInvalidItemAndValidUser() {
        boolean thrown = false;
        ItemRental editItemRental = new ItemRental(user);
        editItemRental.setDescription(null);
        editItemRental.setLocation(itemRental.getLocation());
        editItemRental.setDailyRate(itemRental.getDailyRate());
        editItemRental.setBail(itemRental.getBail());
        editItemRental.setName(itemRental.getName());

        when(itemRepo.findById(1L)).thenReturn(Optional.of(itemRental));
        try {
            itemService.editItem(editItemRental, 1, 1);
        } catch (ResponseStatusException rse) {
            thrown = true;
            assertEquals("400 BAD_REQUEST \"Invalid Description\"", rse.getMessage());
        }

        verify(itemRepo, times(0)).save(any());
        assertTrue(thrown);
    }

    @Test
    public void editItemInvalidItemAndInvalidUser() {
        boolean thrown = false;
        ItemRental editItemRental = new ItemRental(user);
        editItemRental.setDescription(null);
        editItemRental.setLocation(itemRental.getLocation());
        editItemRental.setDailyRate(itemRental.getDailyRate());
        editItemRental.setBail(itemRental.getBail());
        editItemRental.setName(itemRental.getName());
        when(itemRepo.findById(1L)).thenReturn(Optional.of(itemRental));

        try {
            itemService.editItem(editItemRental, 1, 2);
        } catch (ResponseStatusException rse) {
            thrown = true;
            assertEquals("403 FORBIDDEN \"Not your Item\"", rse.getMessage());
        }

        verify(itemRepo, times(0)).save(any());
        assertTrue(thrown);
    }

    @Test
    public void searchKeywordsEmptyString() {
        String search = "";

        List<String> keywords = itemService.searchKeywords(search);

        assertEquals(0, keywords.size());
    }

    @Test
    public void searchKeywordsNullString() {
        boolean thrown = false;
        try {
            itemService.searchKeywords(null);
        } catch (NullPointerException npe) {
            thrown = true;
        }
        assertTrue(thrown);
    }

    @Test
    public void searchKeywordsOneSpace() {
        String search = "key words are cool";

        List<String> keywords = itemService.searchKeywords(search);

        assertEquals(4, keywords.size());
        assertEquals("key", keywords.get(0));
        assertEquals("words", keywords.get(1));
        assertEquals("are", keywords.get(2));
        assertEquals("cool", keywords.get(3));
    }

    @Test
    public void searchKeywordsMultipleSpaces() {
        String search = "key     words    are     ";

        List<String> keywords = itemService.searchKeywords(search);

        assertEquals(3, keywords.size());
        assertEquals("key", keywords.get(0));
        assertEquals("words", keywords.get(1));
        assertEquals("are", keywords.get(2));
    }

    @Test
    public void searchKeywordsDifferentSeperators() {
        String search = "__,key,,  - words-_are   __  ";

        List<String> keywords = itemService.searchKeywords(search);

        assertEquals(3, keywords.size());
        assertEquals("key", keywords.get(0));
        assertEquals("words", keywords.get(1));
        assertEquals("are", keywords.get(2));
    }

    @Test
    public void searchKeywordsSameWords() {
        String search = "kEY key, Key-key";

        List<String> keywords = itemService.searchKeywords(search);

        Assert.assertEquals(1, keywords.size());
        Assert.assertEquals("key", keywords.get(0));
    }

    @Test
    public void filterEmptyList() {
        List<String> keywords = new ArrayList<>();
        List<Item> dbFilterParam1 = new ArrayList<>();
        dbFilterParam1.add(itemRental);

        when(itemRepo.findAllByDeletedIsFalse()).thenReturn(dbFilterParam1);
        when(itemRepo.findAllByNameContainsIgnoreCaseAndDeletedIsFalse(anyString()))
            .thenReturn(new ArrayList());

        List<Item> itemRentals = itemService.filterKeywords(itemRepo, keywords);

        assertEquals(1, itemRentals.size());
    }

    @Test
    public void filterKeyswordList() {
        List<String> keywords = new ArrayList<>();
        keywords.add("cool");
        keywords.add("search");

        ItemRental item1 = new ItemRental(new User());
        ItemRental item2 = new ItemRental(new User());
        ItemRental item3 = new ItemRental(new User());
        ItemRental item4 = new ItemRental(new User());

        item1.setId(1L);
        item2.setId(2L);
        item3.setId(3L);
        item4.setId(4L);
        item1.setName("cool Cool");
        item2.setName("cool Search");
        item3.setName("Search wfkfwo");
        item4.setName("cdwdwdwool dwd");

        List<Item> allList = new ArrayList<>();
        allList.add(itemRental);
        allList.add(item1);
        allList.add(item2);
        allList.add(item3);

        List<Item> coolList = new ArrayList<>();
        coolList.add(item1);
        coolList.add(item2);

        List<Item> searchList = new ArrayList<>();
        searchList.add(item2);
        searchList.add(item3);

        when(itemRepo.findAllByDeletedIsFalse()).thenReturn(allList);
        when(itemRepo.findAllByNameContainsIgnoreCaseAndDeletedIsFalse("cool"))
            .thenReturn(coolList);
        when(itemRepo.findAllByNameContainsIgnoreCaseAndDeletedIsFalse("search"))
            .thenReturn(searchList);

        List<Item> filterResult = itemService.filterKeywords(itemRepo, keywords);

        assertEquals(3, filterResult.size());
        assertEquals(item1, filterResult.get(0));
        assertEquals(item2, filterResult.get(1));
        assertEquals(item3, filterResult.get(2));
    }
}
