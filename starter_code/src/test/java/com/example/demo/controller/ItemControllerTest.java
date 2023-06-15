package com.example.demo.controller;

import com.example.demo.TestService;
import com.example.demo.TestUtils;
import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ItemControllerTest {

    private ItemController itemController;
    private ItemRepository itemRepo = mock(ItemRepository.class);
    @Autowired
    private TestService testService;
    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setUp() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepo);
    }

    @Test
    public void testItemController() {
        ItemController controller = new ItemController();
        assertNotNull(controller);
    }

    @Test
    public void testGetItems() {
        // Set up test data
        Item item1 = testService.createItem("Item 1", "description", BigDecimal.valueOf(20));
        Item item2 = testService.createItem("Item 2", "description2", BigDecimal.valueOf(20));
        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);

        // Mock repository call
        when(itemRepo.findAll()).thenReturn(items);

        // Call the controller method
        ResponseEntity<List<Item>> response = itemController.getItems();

        // Verify the response
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Item> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(2, responseBody.size());
        assertEquals(item1, responseBody.get(0));
        assertEquals(item2, responseBody.get(1));
    }

    @Test
    public void testGetItemById() {
        // Set up test data
        Long itemId = 1L;
        Item item = testService.createItem("Item 1", "description", BigDecimal.valueOf(20));

        // Mock repository call
        when(itemRepo.findById(itemId)).thenReturn(Optional.of(item));

        // Call the controller method
        ResponseEntity<Item> response = itemController.getItemById(itemId);

        // Verify the response
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Item responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(item, responseBody);
    }

    @Test
    public void testGetItemsByName() {
        // Set up test data
        String itemName = "Item";
        List<Item> items = new ArrayList<>();
        items.add(testService.createItem("Item 1", "description", BigDecimal.valueOf(20)));
        items.add(testService.createItem("Item 2", "description2", BigDecimal.valueOf(30)));

        // Mock repository call
        when(itemRepo.findByName(itemName)).thenReturn(items);

        // Call the controller method
        ResponseEntity<List<Item>> response = itemController.getItemsByName(itemName);

        // Verify the response
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Item> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(items.size(), responseBody.size());
        assertEquals(items.get(0), responseBody.get(0));
        assertEquals(items.get(1), responseBody.get(1));
    }
}
