package com.example.demo.controller;

import com.example.demo.TestUtils;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.dao.DataAccessException;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;
import java.util.Arrays;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {

    private UserController userController;
    private UserRepository userRepo = mock(UserRepository.class);
    private CartRepository cartRepo = mock(CartRepository.class);
    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);
    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setUp() {
        userController = new UserController();
        // test utils injects the required fields
        TestUtils.injectObjects(userController, "userRepository", userRepo);
        TestUtils.injectObjects(userController, "cartRepository", cartRepo);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
    }

    @Test
    public void create_user_happy_path() throws Exception {
        // happy path, or a sanity test case
        // example of stubbing (defining the return value instead of running the code):
        when(encoder.encode("testpassword")).thenReturn("thisIsHashed");
        CreateUserRequest req = new CreateUserRequest();
        req.setUsername("itsaMeMario");
        req.setPassword("testpassword");
        req.setConfirmPassword("testpassword");

        final ResponseEntity<User> response = userController.createUser(req);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User user = response.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("itsaMeMario", user.getUsername());
        assertEquals("thisIsHashed", user.getPassword());
    }

    @Test
    public void create_user_bad_request() throws Exception {
        CreateUserRequest req = new CreateUserRequest();
        req.setUsername("itsaMeMario");
        req.setPassword("test");
        req.setConfirmPassword("test");

        final ResponseEntity<User> response = userController.createUser(req);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void create_user_with_existing_username() throws Exception {
        // Create a mock CreateUserRequest object with an existing username
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("existinguser");
        request.setPassword("password");
        request.setConfirmPassword("password");

        // Mock the UserRepository to return a non-null User object when findByUsername is called with the existing username
        User existingUser = new User();
        existingUser.setUsername("existinguser");
        when(userRepo.findByUsername("existinguser")).thenReturn(existingUser);

        // Call the createUser method with the mock request object
        ResponseEntity<User> response = userController.createUser(request);

        // Assert that the method returns a bad request response
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void create_user_with_database_error() throws Exception {
        // Create a mock CreateUserRequest object with valid data
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("newuser");
        request.setPassword("password");
        request.setConfirmPassword("password");

        // Mock the UserRepository to return a null User object when findByUsername is called with the new username
        when(userRepo.findByUsername("newuser")).thenReturn(null);

        // Mock the UserRepository to throw a DataAccessException when save is called with the new user
        when(userRepo.save(any(User.class))).thenThrow(new DataAccessException("Database error") {});

        // Call the createUser method with the mock request object
        ResponseEntity<User> response = userController.createUser(request);

        // Assert that the response status code is 400 (bad request)
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        // Assert that the response body contains the expected error message
        assertEquals("An error occurred while processing your request.", response.getBody().getUsername());
    }

    @Test
    public void getUserProfileWithoutAuthentication() {
        ResponseEntity<User> response = restTemplate.getForEntity("/api/user/{username}", User.class, "testUser");
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void testGetCartDetailsWithoutAuthentication() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("/api/order/history/{username}");
        URI uri = builder.build("testUser");
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

}
