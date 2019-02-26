package de.hhu.propra.sharingplatform.service;

import static org.junit.Assert.*;

import de.hhu.propra.sharingplatform.dao.ItemRentalRepo;
import de.hhu.propra.sharingplatform.model.ItemRental;
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
public class ItemRentalServiceTest {

    @MockBean
    private UserService userService;

    @MockBean
    private ItemRentalRepo itemRentalRepo;

    private ItemRental itemRental;
    private User user;
    private ItemService itemService;
    private ImageService imageService;

    @Before
    public void init() {
        imageService = mock(ImageService.class);
        itemService = new ItemService(itemRentalRepo, userService, imageService);

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

        verify(itemRentalRepo, times(2)).save(argument.capture());
        assertEquals(itemRental, argument.getValue());
        assertEquals(1, (long) argument.getValue().getOwner().getId());
    }

    @Test
    public void removeOneItemValidUser() {
        Optional<ItemRental> optional = Optional.ofNullable(itemRental);
        when(itemRentalRepo.findById(anyLong())).thenReturn(optional);

        itemService.removeItem(1L, 1);

        assertTrue(itemRentalRepo.findById(1L).get().isDeleted());
    }

    @Test
    public void removeOneItemInvalidUser() {
        boolean thrown = false;
        Optional<ItemRental> optional = Optional.ofNullable(itemRental);
        when(itemRentalRepo.findById(anyLong())).thenReturn(optional);

        try {
            itemService.removeItem(1L, 2);
        } catch (ResponseStatusException rse) {
            thrown = true;
            assertEquals("403 FORBIDDEN \"Not your ItemRental\"", rse.getMessage());
        }
        assertFalse(itemRentalRepo.findById(1L).get().isDeleted());
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
        verify(itemRentalRepo, times(0)).save(any());
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

        when(itemRentalRepo.findById(1L)).thenReturn(optional);

        itemService.editItem(editItemRental, 1L, 1L);

        verify(itemRentalRepo, times(1)).save(argument.capture());
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

        when(itemRentalRepo.findOneById(1)).thenReturn(itemRental);
        try {
            itemService.editItem(editItemRental, 1, 2);
        } catch (ResponseStatusException rse) {
            thrown = true;
            assertEquals("404 NOT_FOUND \"ItemRental not Found\"", rse.getMessage());
        }

        verify(itemRentalRepo, times(0)).save(any());
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

        when(itemRentalRepo.findById(1)).thenReturn(Optional.of(itemRental));
        try {
            itemService.editItem(editItemRental, 1, 1);
        } catch (ResponseStatusException rse) {
            thrown = true;
            assertEquals("400 BAD_REQUEST \"Invalid Description\"", rse.getMessage());
        }

        verify(itemRentalRepo, times(0)).save(any());
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
        when(itemRentalRepo.findById(1)).thenReturn(Optional.of(itemRental));

        try {
            itemService.editItem(editItemRental, 1, 2);
        } catch (ResponseStatusException rse) {
            thrown = true;
            assertEquals("403 FORBIDDEN \"Not your ItemRental\"", rse.getMessage());
        }

        verify(itemRentalRepo, times(0)).save(any());
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
        List<ItemRental> dbNoItemRentals = new ArrayList<>();
        List<ItemRental> dbAllItemRental = new ArrayList<>();
        dbAllItemRental.add(itemRental);

        when(itemRentalRepo.findAllByNameContainsIgnoreCase(any())).thenReturn(dbNoItemRentals);
        when(itemRentalRepo.findAll()).thenReturn(dbAllItemRental);

        List<ItemRental> itemRentals = itemService.filter(keywords);

        assertEquals(1, itemRentals.size());
    }

    @Test
    public void filterKeyswordList() {
        List<String> keywords = new ArrayList<>();
        keywords.add("cool");
        keywords.add("search");

        List<ItemRental> dbFilterParam1 = new ArrayList<>();
        List<ItemRental> dbFilterParam2 = new ArrayList<>();
        dbFilterParam1.add(itemRental);
        dbFilterParam1.add(itemRental);
        dbFilterParam2.add(itemRental);

        List<ItemRental> dbAllItemRental = new ArrayList<>();

        when(itemRentalRepo.findAllByNameContainsIgnoreCase("cool")).thenReturn(dbFilterParam1);
        when(itemRentalRepo.findAllByNameContainsIgnoreCase("search")).thenReturn(dbFilterParam2);
        when(itemRentalRepo.findAll()).thenReturn(dbAllItemRental);

        List<ItemRental> itemRentals = itemService.filter(keywords);

        assertEquals(3, itemRentals.size());
    }
}
