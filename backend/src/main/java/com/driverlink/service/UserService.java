package com.driverlink.service;

import com.driverlink.exception.UserException;
import com.driverlink.model.User;
import com.driverlink.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing user operations. Implements business logic for user CRUD operations with
 * proper validation and security measures.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final UserValidator userValidator;
  private final PasswordEncoder passwordEncoder;

  /**
   * Creates a new user with proper validation and password encryption.
   *
   * @param user the user to create
   * @return the created user
   * @throws UserException if validation fails
   */
  @Operation(summary = "Create a new user")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "201", description = "User created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
      })
  public User createUser(User user) {
    validateAndPrepareUser(user);
    return userRepository.save(user);
  }

  /**
   * Retrieves a user by their ID.
   *
   * @param id the user ID
   * @return the user
   * @throws UserException if user not found
   */
  @Operation(summary = "Get user by ID")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "User found"),
        @ApiResponse(responseCode = "404", description = "User not found")
      })
  public User getUserById(Long id) {
    return userRepository
        .findById(id)
        .orElseThrow(
            () -> new UserException.UserNotFoundException("User not found with id: " + id));
  }

  /**
   * Retrieves all users in the system.
   *
   * @return list of all users
   */
  @Operation(summary = "Get all users")
  @ApiResponse(responseCode = "200", description = "List of users retrieved")
  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  /**
   * Updates an existing user with validation.
   *
   * @param user the user to update
   * @return the updated user
   * @throws UserException if validation fails or user not found
   */
  @Operation(summary = "Update user")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "User updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "User not found")
      })
  public User updateUser(User user) {
    User existingUser = getUserById(user.getId());
    userValidator.validateUpdateUser(user, existingUser);
    return userRepository.save(user);
  }

  /**
   * Deletes a user by their ID.
   *
   * @param id the user ID
   * @throws UserException if user not found
   */
  @Operation(summary = "Delete user")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "User deleted successfully"),
        @ApiResponse(responseCode = "404", description = "User not found")
      })
  public void deleteUser(Long id) {
    User user = getUserById(id);
    userRepository.delete(user);
  }

  /**
   * Validates and prepares a user for creation by encoding their password and performing validation
   * checks.
   *
   * @param user the user to validate and prepare
   */
  private void validateAndPrepareUser(User user) {
    userValidator.validateNewUser(user);
    user.setPassword(passwordEncoder.encode(user.getPassword()));
  }
}
