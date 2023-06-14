package com.example.demo.persistence;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.repositories.CartRepository;
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
public class CartRepoTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private CartRepository cartRepository;

    @Test
    public void testCartCRUD() {
        // Create a new Cart
        Cart cart = new Cart();
        // Set cart properties
        entityManager.persist(cart);
        entityManager.flush();

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
}
