package com.example.demo.controller;

import com.example.demo.TestService;
import com.example.demo.TestUtils;
import com.example.demo.controllers.OrderController;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderControllerTest {

    private UserController userController;
    private OrderController orderController;
    @Autowired
    private TestService testService;
    private UserRepository userRepo = mock(UserRepository.class);
    private OrderRepository orderRepo = mock(OrderRepository.class);
    private CartRepository cartRepo = mock(CartRepository.class);

    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() {
        userController = new UserController();
        // test utils injects the required fields
        TestUtils.injectObjects(userController, "userRepository", userRepo);
        TestUtils.injectObjects(userController, "cartRepository", cartRepo);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepo);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepo);
    }

    @Test
    public void testGetOrdersForUser() throws Exception {
        // Set up test data
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("testUser");
        createUserRequest.setPassword("testPassword");
        createUserRequest.setConfirmPassword("testPassword");

        ResponseEntity<User> createUserResponse = userController.createUser(createUserRequest);
        assertEquals(HttpStatus.OK, createUserResponse.getStatusCode());

        // Retrieve the created user from the response
        User createdUser = createUserResponse.getBody();
        assertNotNull(createdUser);

        // Set up the orders for the user
        List<UserOrder> orders = new ArrayList<>();
        UserOrder order1 = new UserOrder();
        UserOrder order2 = new UserOrder();
        orders.add(order1);
        orders.add(order2);

        // Mock repository calls
        when(userRepo.findByUsername("testUser")).thenReturn(createdUser);
        when(orderRepo.findByUser(createdUser)).thenReturn(orders);

        // Call the controller method
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("testUser");

        // Verify the response
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<UserOrder> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(2, responseBody.size());
        assertEquals(order1, responseBody.get(0));
        assertEquals(order2, responseBody.get(1));
    }
}
