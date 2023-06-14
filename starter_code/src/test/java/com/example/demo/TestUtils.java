package com.example.demo;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import java.lang.reflect.Field;
import java.math.BigDecimal;

public class TestUtils {

    public static  void injectObjects(Object target, String fieldName, Object toInject) {

        boolean wasPrivate = false;

        try {
            Field f = target.getClass().getDeclaredField(fieldName);

            if (!f.isAccessible()) {
                f.setAccessible(true);
                wasPrivate = true;
            }
            f.set(target, toInject);

            if(wasPrivate) {
                f.setAccessible(false);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static User createUser(TestEntityManager entityManager) {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("testpassword");
        entityManager.persist(user);
        entityManager.flush();
        return user;
    }

    public static Cart createCart(User user, TestEntityManager entityManager) {
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setTotal(BigDecimal.ZERO);
        entityManager.persist(cart);
        entityManager.flush();
        return cart;
    }

    public static Item createItem(String name, String description, BigDecimal price, TestEntityManager entityManager) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setPrice(price);
        entityManager.persist(item);
        entityManager.flush();
        return item;
    }

}
