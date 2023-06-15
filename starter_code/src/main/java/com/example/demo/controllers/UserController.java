package com.example.demo.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

@RestController
@RequestMapping("/api/user")
public class UserController {

	private static final Logger log = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		return ResponseEntity.of(userRepository.findById(id));
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
	}

	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
		try {
			// Check if the username already exists in the database
			User existingUser = userRepository.findByUsername(createUserRequest.getUsername());
			if (existingUser != null) {
				// Return a bad request response with a custom error message
				User errorUser = new User();
				errorUser.setUsername("Username already exists.");
				log.error("CreateUser request failed: {}", errorUser.getUsername());
				return ResponseEntity.badRequest().body(errorUser);
			}

			// Create a new user object and set its username
			User user = new User();
			user.setUsername(createUserRequest.getUsername());

			// Create a new cart object and associate it with the user
			Cart cart = new Cart();
			cartRepository.save(cart);
			user.setCart(cart);

			// Check if the password and confirm password fields are not empty
			if (createUserRequest.getPassword() == null || createUserRequest.getConfirmPassword() == null) {
				// Return a bad request response with a custom error message
				User errorUser = new User();
				errorUser.setUsername("Check password and confirm password fields are not empty.");
				log.error("CreateUser request failed: {}", errorUser.getUsername());
				return ResponseEntity.badRequest().body(errorUser);
			}

			// Check if the password is at least 7 characters long and matches the confirm password field
			if (createUserRequest.getPassword().length() < 7 || !createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())) {
				// Return a bad request response with a custom error message
				User errorUser = new User();
				errorUser.setUsername("Password must be at least 7 characters and match the confirm password field.");
				log.error("CreateUser request failed: {}", errorUser.getUsername());
				return ResponseEntity.badRequest().body(errorUser);
			}

			// Hash the password and save the user object to the database
			user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
			userRepository.save(user);

			// log a message indicating that the user was successfully created
			log.info("CreateUser request successful for user: {}", user.getUsername());

			// Return an OK response with the newly created user object
			return ResponseEntity.ok(user);
		} catch (Exception e) {
			log.error("Exception caught while processing CreateUser request: {}", e.getMessage());
			User errorUser = new User();
			errorUser.setUsername("An error occurred while processing your request.");
			return ResponseEntity.badRequest().body(errorUser);
		}
	}
	
}
