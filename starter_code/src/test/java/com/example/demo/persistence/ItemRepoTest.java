package com.example.demo.persistence;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import java.math.BigDecimal;
import java.util.Optional;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ItemRepoTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ItemRepository itemRepository;

    @Test
    public void testItemCRUD() {
        // Create a new Item
        Item item = new Item();
        // Set item properties
        item.setName("Test Item");
        item.setDescription("This is a test item.");
        item.setPrice(BigDecimal.valueOf(10));
        entityManager.persist(item);
        entityManager.flush();

        // Find the item by ID
        Optional<Item> foundItem = itemRepository.findById(item.getId());

        // Check if the item was found
        assertTrue(foundItem.isPresent());
        assertEquals(item.getId(), foundItem.get().getId());

        // Update the item
        item.setPrice(BigDecimal.valueOf(20));
        entityManager.merge(item);
        entityManager.flush();

        // Find the updated item by ID
        Optional<Item> updatedItem = itemRepository.findById(item.getId());

        // Check if the updated item was found
        assertTrue(updatedItem.isPresent());
        assertEquals(BigDecimal.valueOf(20), updatedItem.get().getPrice());

        // Delete the item
        itemRepository.delete(item);

        // Try to find the deleted item by ID
        Optional<Item> deletedItem = itemRepository.findById(item.getId());

        // Check if the deleted item was not found
        assertFalse(deletedItem.isPresent());
    }
}
