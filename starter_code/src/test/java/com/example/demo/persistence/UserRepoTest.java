package com.example.demo.persistence;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepoTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void testUserCRUD() {
        // Create a new User
        User user = new User();
        // Set user properties
        user.setUsername("testuser");
        user.setPassword("testpassword");
        entityManager.persist(user);
        entityManager.flush();

        // Find the user by ID
        Optional<User> foundUser = userRepository.findById(user.getId());

        // Check if the user was found
        assertTrue(foundUser.isPresent());
        assertEquals(user.getId(), foundUser.get().getId());

        // Update the user
        user.setPassword("newpassword");
        entityManager.merge(user);
        entityManager.flush();

        // Find the updated user by ID
        Optional<User> updatedUser = userRepository.findById(user.getId());

        // Check if the updated user was found
        assertTrue(updatedUser.isPresent());
        assertEquals("newpassword", updatedUser.get().getPassword());

        // Delete the user
        userRepository.delete(user);

        // Try to find the deleted user by ID
        Optional<User> deletedUser = userRepository.findById(user.getId());

        // Check if the deleted user was not found
        assertFalse(deletedUser.isPresent());
    }
}
