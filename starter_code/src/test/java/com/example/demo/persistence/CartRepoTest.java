package com.example.demo.persistence;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class CartRepoTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private CartRepository cartRepository;

    @Test
    public void testCartCRUD() {
        // Create a new User and the User Cart
        User user = TestUtils.createUser(entityManager);
        Cart cart = TestUtils.createCart(user, entityManager);
        // Check if the cart's user matches the user you set
        assertEquals(user, cart.getUser());
        // create and add items
        Item item = TestUtils.createItem("item1", "long description", BigDecimal.valueOf(150), entityManager);
        List<Item> itemList = new ArrayList<>();
        itemList.add(item);
        cart.setItems(itemList);

        // Find the cart by ID
        Optional<Cart> foundCart = cartRepository.findById(cart.getId());

        // Check if the cart was found
        assertTrue(foundCart.isPresent());
        assertEquals(cart.getId(), foundCart.get().getId());

        // Update the cart
        cart.setTotal(BigDecimal.valueOf(100));
        entityManager.merge(cart);
        entityManager.flush();

        // Find the updated cart by ID
        Optional<Cart> updatedCart = cartRepository.findById(cart.getId());

        // Check if the updated cart was found
        assertTrue(updatedCart.isPresent());
        assertEquals(BigDecimal.valueOf(100), updatedCart.get().getTotal());

        // Delete the cart
        cartRepository.delete(cart);

        // Try to find the deleted cart by ID
        Optional<Cart> deletedCart = cartRepository.findById(cart.getId());

        // Check if the deleted cart was not found
        assertFalse(deletedCart.isPresent());
    }

    @Test
    public void testAddItemToCart() {

        User user = TestUtils.createUser(entityManager);
        Cart cart = TestUtils.createCart(user, entityManager);
        // Create a new Item
        Item item = TestUtils.createItem("item1", "long description", BigDecimal.valueOf(10), entityManager);

        // Add the item to the cart
        cart.addItem(item);
        entityManager.merge(cart);
        entityManager.flush();

        // Verify that the item was added to the cart
        Optional<Cart> foundCart = cartRepository.findById(cart.getId());
        assertTrue(foundCart.isPresent());
        assertEquals(1, foundCart.get().getItems().size());
        assertEquals(BigDecimal.valueOf(10), foundCart.get().getTotal());
    }

    @Test
    public void testRemoveItemFromCart() {
        User user = TestUtils.createUser(entityManager);
        Cart cart = TestUtils.createCart(user, entityManager);
        // Create a new Item
        Item item = TestUtils.createItem("item1", "long description", BigDecimal.valueOf(150), entityManager);

        // Create another new Item
        Item item2 = TestUtils.createItem("item2", "long description 2", BigDecimal.valueOf(20), entityManager);

        // Add the items to the cart
        cart.addItem(item);
        cart.addItem(item2);
        entityManager.merge(cart);
        entityManager.flush();

        // Remove one of the items from the cart
        cart.removeItem(item);
        entityManager.merge(cart);
        entityManager.flush();

        // Verify that the item was removed from the cart
        Optional<Cart> foundCart = cartRepository.findById(cart.getId());
        assertTrue(foundCart.isPresent());
        assertEquals(1, foundCart.get().getItems().size());
        assertEquals(BigDecimal.valueOf(20), foundCart.get().getTotal());
    }
}
