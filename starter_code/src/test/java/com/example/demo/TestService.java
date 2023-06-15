package com.example.demo;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.ArrayList;

@Service
public class TestService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ItemRepository itemRepository;

    public User createUser() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("testpassword");
        userRepository.save(user);
        return user;
    }

    public Cart createCart(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setItems(new ArrayList<>()); // Initialize the items field with an empty list
        cart.setTotal(BigDecimal.ZERO);
        cartRepository.save(cart);
        return cart;
    }

    public Item createItem(String name, String description, BigDecimal price) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setPrice(price);
        itemRepository.save(item);
        return item;
    }
}
