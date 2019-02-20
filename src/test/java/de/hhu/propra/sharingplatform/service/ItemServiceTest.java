package de.hhu.propra.sharingplatform.service;

import static org.junit.Assert.*;

import de.hhu.propra.sharingplatform.dao.ItemRepo;
import de.hhu.propra.sharingplatform.model.Item;
import de.hhu.propra.sharingplatform.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class ItemServiceTest {

    @MockBean
    private UserService userService;

    @MockBean
    private ItemRepo itemRepo;

    private Item item;
    private User user;
    private ItemService itemService;

    @Before
    public void init() {
        itemService = new ItemService(itemRepo, userService);

        user = new User();
        user.setName("Test");
        user.setId((long) 1);

        item = new Item(user);
        item.setId((long) 1);
        item.setName("TestItem");
        item.setBail(100.0);
        item.setPrice(20.0);
        item.setDescription("This is a test");
        item.setLocation("Test-Location");
    }

    @Test
    public void persistOneValidItem() {
        ArgumentCaptor<Item> argument = ArgumentCaptor.forClass(Item.class);
        when(userService.fetchUserById(1L)).thenReturn(user);

        itemService.persistItem(item, 1);

        verify(itemRepo, times(1)).save(argument.capture());
        assertEquals(item, argument.getValue());
        assertEquals(1, (long) argument.getValue().getOwner().getId());
    }

    @Test
    public void removeOneItemValidUser() {
        Optional<Item> optional = Optional.ofNullable(item);
        when(itemRepo.findById(anyLong())).thenReturn(optional);

        itemService.removeItem(1L, 1);

        assertTrue(itemRepo.findById(1L).get().isDeleted());
    }

    @Test
    public void removeOneItemInvalidUser() {
        Optional<Item> optional = Optional.ofNullable(item);
        when(itemRepo.findById(anyLong())).thenReturn(optional);

        itemService.removeItem(1L, 2);

        assertFalse(itemRepo.findById(1L).get().isDeleted());
    }

    @Test
    public void dontPersistInvalidItem() {
        boolean thrown = false;
        item.setLocation(null);
        when(userService.fetchUserById(1L)).thenReturn(user);
        try {
            itemService.persistItem(item, 1);
        } catch (ResponseStatusException rse) {
            thrown = true;
            assertEquals("400 BAD_REQUEST \"Invalid Location\"", rse.getMessage());
        }
        verify(itemRepo, times(0)).save(any());
        assertTrue(thrown);
    }

    @Test
    public void editItemValidItemAndUser() {
        Item editItem = new Item(user);
        editItem.setDescription("This is edited");
        editItem.setLocation(item.getLocation());
        editItem.setPrice(item.getPrice());
        editItem.setBail(item.getBail());
        editItem.setName(item.getName());
        ArgumentCaptor<Item> argument = ArgumentCaptor.forClass(Item.class);
        Optional<Item> optional = Optional.ofNullable(item);

        when(itemRepo.findById(1L)).thenReturn(optional);

        itemService.editItem(editItem, 1L, 1L);

        verify(itemRepo, times(1)).save(argument.capture());
        assertEquals(argument.getValue().getDescription(), editItem.getDescription());
    }

    @Test
    public void editItemValidItemAndInvalidUser() {
        boolean thrown = false;
        Item editItem = new Item(user);
        editItem.setDescription("This is edited");
        editItem.setLocation(item.getLocation());
        editItem.setPrice(item.getPrice());
        editItem.setBail(item.getBail());
        editItem.setName(item.getName());

        when(itemRepo.findOneById(1)).thenReturn(item);
        try {
            itemService.editItem(editItem, 1, 2);
        } catch (ResponseStatusException rse) {
            thrown = true;
            assertEquals("404 NOT_FOUND \"Invalid Item\"", rse.getMessage());
        }

        verify(itemRepo, times(0)).save(any());
        assertTrue(thrown);
    }

    @Test
    public void editItemInvalidItemAndValidUser() {
        boolean thrown = false;
        Item editItem = new Item(user);
        editItem.setDescription(null);
        editItem.setLocation(item.getLocation());
        editItem.setPrice(item.getPrice());
        editItem.setBail(item.getBail());
        editItem.setName(item.getName());

        when(itemRepo.findOneById(1)).thenReturn(item);
        try {
            itemService.editItem(editItem, 1, 1);
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
        Item editItem = new Item(user);
        editItem.setDescription(null);
        editItem.setLocation(item.getLocation());
        editItem.setPrice(item.getPrice());
        editItem.setBail(item.getBail());
        editItem.setName(item.getName());
        when(itemRepo.findOneById(1)).thenReturn(item);

        try {
            itemService.editItem(editItem, 1, 2);
        } catch (ResponseStatusException rse) {
            thrown = true;
            assertEquals("400 BAD_REQUEST \"Invalid Description\"", rse.getMessage());
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

    @Test(expected = NullPointerException.class)
    public void searchKeywordsNullString() {
        String search = null;
        itemService.searchKeywords(search);
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
        List<Item> dbNoItems = new ArrayList<>();
        List<Item> dbAllItem = new ArrayList<>();
        dbAllItem.add(item);

        when(itemRepo.findAllByNameContainsIgnoreCase(any())).thenReturn(dbNoItems);
        when(itemRepo.findAll()).thenReturn(dbAllItem);

        List<Item> items = itemService.filter(keywords);

        assertEquals(1, items.size());
    }

    @Test
    public void filterKeyswordList() {
        List<String> keywords = new ArrayList<>();
        keywords.add("cool");
        keywords.add("search");

        List<Item> dbFilterParam1 = new ArrayList<>();
        List<Item> dbFilterParam2 = new ArrayList<>();
        dbFilterParam1.add(item);
        dbFilterParam1.add(item);
        dbFilterParam2.add(item);

        List<Item> dbAllItem = new ArrayList<>();

        when(itemRepo.findAllByNameContainsIgnoreCase("cool")).thenReturn(dbFilterParam1);
        when(itemRepo.findAllByNameContainsIgnoreCase("search")).thenReturn(dbFilterParam2);
        when(itemRepo.findAll()).thenReturn(dbAllItem);

        List<Item> items = itemService.filter(keywords);

        assertEquals(3, items.size());
    }
}
