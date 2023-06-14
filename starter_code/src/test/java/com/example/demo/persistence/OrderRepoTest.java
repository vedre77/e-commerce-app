package com.example.demo.persistence;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
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
public class OrderRepoTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private OrderRepository orderRepository;

    @Test
    public void testOrderCRUD() {
        // Create a new Order
        UserOrder order = new UserOrder();
        // Set order properties
        order.setTotal(BigDecimal.valueOf(100));
        // Create a new User
        User user = new User();
        // Set user properties
        user.setUsername("testuser");
        user.setPassword("testpassword");
        entityManager.persist(user);
        entityManager.flush();
        order.setUser(user);
        entityManager.persist(order);
        entityManager.flush();

        // Find the order by ID
        Optional<UserOrder> foundOrder = orderRepository.findById(order.getId());

        // Check if the order was found
        assertTrue(foundOrder.isPresent());
        assertEquals(order.getId(), foundOrder.get().getId());

        // Update the order
        order.setTotal(BigDecimal.valueOf(200));
        entityManager.merge(order);
        entityManager.flush();

        // Find the updated order by ID
        Optional<UserOrder> updatedOrder = orderRepository.findById(order.getId());

        // Check if the updated order was found
        assertTrue(updatedOrder.isPresent());
        assertEquals(BigDecimal.valueOf(200), updatedOrder.get().getTotal());

        // Delete the order
        orderRepository.delete(order);

        // Try to find the deleted order by ID
        Optional<UserOrder> deletedOrder = orderRepository.findById(order.getId());

        // Check if the deleted order was not found
        assertFalse(deletedOrder.isPresent());
    }

}
