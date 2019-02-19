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

import java.util.ArrayList;
import java.util.List;

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
        when(itemRepo.findOneById(1)).thenReturn(item);

        itemService.removeItem(1, 1);

        assertTrue(itemRepo.findOneById(1).isDeleted());
    }

    @Test
    public void removeOneItemInvalidUser() {
        when(itemRepo.findOneById(1)).thenReturn(item);

        itemService.removeItem(1, 2);

        assertTrue(!itemRepo.findOneById(1).isDeleted());
    }

    @Test
    public void dontPersistInvalidItem() {
        item.setLocation(null);
        when(userService.fetchUserById(1L)).thenReturn(user);

        itemService.persistItem(item, 1);
        verify(itemRepo, times(0)).save(any());
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

        when(itemRepo.findOneById(1)).thenReturn(item);

        itemService.editItem(editItem, 1, 1);

        verify(itemRepo, times(1)).save(argument.capture());
        assertEquals(argument.getValue().getDescription(), editItem.getDescription());
    }

    @Test
    public void editItemValidItemAndInvalidUser() {
        Item editItem = new Item(user);
        editItem.setDescription("This is edited");
        editItem.setLocation(item.getLocation());
        editItem.setPrice(item.getPrice());
        editItem.setBail(item.getBail());
        editItem.setName(item.getName());

        when(itemRepo.findOneById(1)).thenReturn(item);

        itemService.editItem(editItem, 1, 2);

        verify(itemRepo, times(0)).save(any());
    }

    @Test
    public void editItemInvalidItemAndValidUser() {
        Item editItem = new Item(user);
        editItem.setDescription(null);
        editItem.setLocation(item.getLocation());
        editItem.setPrice(item.getPrice());
        editItem.setBail(item.getBail());
        editItem.setName(item.getName());

        when(itemRepo.findOneById(1)).thenReturn(item);

        itemService.editItem(editItem, 1, 1);

        verify(itemRepo, times(0)).save(any());
    }

    @Test
    public void editItemInvalidItemAndInvalidUser() {
        Item editItem = new Item(user);
        editItem.setDescription(null);
        editItem.setLocation(item.getLocation());
        editItem.setPrice(item.getPrice());
        editItem.setBail(item.getBail());
        editItem.setName(item.getName());

        when(itemRepo.findOneById(1)).thenReturn(item);

        itemService.editItem(editItem, 1, 2);

        verify(itemRepo, times(0)).save(any());
    }

    @Test
    public void findOneItem() {
        when(itemRepo.findOneById(1)).thenReturn(item);
        Item resultItem = itemService.findItem(1);
        assertEquals(resultItem, item);
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
