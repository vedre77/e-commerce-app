package com.example.demo.controller;

import com.example.demo.TestService;
import com.example.demo.TestUtils;
import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
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
import java.util.Optional;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CartControllerTest {

    private CartController cartController;
    private UserRepository userRepo = mock(UserRepository.class);
    private CartRepository cartRepo = mock(CartRepository.class);
    private ItemRepository itemRepo = mock(ItemRepository.class);
    @Autowired
    private TestService testService;
    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setUp() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepo);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepo);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepo);
    }

    @Test
    public void testAddAndRemoveFromCart() {
        // Set up test data
        User user = testService.createUser();
        Cart cart = testService.createCart(user);
        Item item = testService.createItem("name", "description", BigDecimal.valueOf(20));
        user.setCart(cart);

        // Mock repository calls
        when(userRepo.findByUsername("testUser")).thenReturn(user);
        when(itemRepo.findById(1L)).thenReturn(Optional.of(item));

        // Add item to the cart
        ModifyCartRequest addToCartRequest = new ModifyCartRequest();
        addToCartRequest.setUsername("testUser");
        addToCartRequest.setItemId(1L);
        addToCartRequest.setQuantity(1);

        // Call the controller method to add item
        ResponseEntity<Cart> addToCartResponse = cartController.addTocart(addToCartRequest);

        // Verify the response for adding item
        assertNotNull(addToCartResponse);
        assertEquals(HttpStatus.OK, addToCartResponse.getStatusCode());
        Cart addedItemCart = addToCartResponse.getBody();
        assertNotNull(addedItemCart);
        assertEquals(1, addedItemCart.getItems().size());
        assertEquals(item, addedItemCart.getItems().get(0));

        // Remove item from the cart
        ModifyCartRequest removeFromCartRequest = new ModifyCartRequest();
        removeFromCartRequest.setUsername("testUser");
        removeFromCartRequest.setItemId(1L);
        removeFromCartRequest.setQuantity(1);

        // Call the controller method to remove item
        ResponseEntity<Cart> removeFromCartResponse = cartController.removeFromcart(removeFromCartRequest);

        // Verify the response for removing item
        assertNotNull(removeFromCartResponse);
        assertEquals(HttpStatus.OK, removeFromCartResponse.getStatusCode());
        Cart removedItemCart = removeFromCartResponse.getBody();
        assertNotNull(removedItemCart);
        assertEquals(0, removedItemCart.getItems().size());
    }
}
