package com.driverlink.controller;

import com.driverlink.dto.UserDTO;
import com.driverlink.mapper.UserMapper;
import com.driverlink.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing user operations. Provides endpoints for CRUD operations with proper
 * validation and error handling.
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
@Tag(name = "User Management", description = "APIs for managing users")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;
  private final UserMapper userMapper;

  /**
   * Creates a new user with validation.
   *
   * @param userDTO the user to create
   * @return the created user
   */
  @PostMapping
  @Operation(summary = "Create a new user")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "201", description = "User created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
      })
  public ResponseEntity<UserDTO> createUser(
      @Parameter(description = "User details", required = true) @Valid @RequestBody
          UserDTO userDTO) {
    return new ResponseEntity<>(
        userMapper.toDTO(userService.createUser(userMapper.toEntity(userDTO))), HttpStatus.CREATED);
  }

  /**
   * Retrieves a user by their ID.
   *
   * @param id the user ID
   * @return the user if found
   */
  @GetMapping("/{id}")
  @Operation(summary = "Get user by ID")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "User found"),
        @ApiResponse(responseCode = "404", description = "User not found")
      })
  public ResponseEntity<UserDTO> getUser(
      @Parameter(description = "User ID", required = true) @PathVariable Long id) {
    return ResponseEntity.ok(userMapper.toDTO(userService.getUserById(id)));
  }

  /**
   * Retrieves all users in the system.
   *
   * @return list of all users
   */
  @GetMapping
  @Operation(summary = "Get all users")
  @ApiResponse(responseCode = "200", description = "List of users retrieved")
  public ResponseEntity<List<UserDTO>> getAllUsers() {
    return ResponseEntity.ok(userService.getAllUsers().stream().map(userMapper::toDTO).toList());
  }

  /**
   * Updates an existing user.
   *
   * @param id the user ID
   * @param userDTO the updated user data
   * @return the updated user
   */
  @PutMapping("/{id}")
  @Operation(summary = "Update user")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "User updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "User not found")
      })
  public ResponseEntity<UserDTO> updateUser(
      @Parameter(description = "User ID", required = true) @PathVariable Long id,
      @Parameter(description = "Updated user details", required = true) @Valid @RequestBody
          UserDTO userDTO) {
    userDTO.setId(id);
    return ResponseEntity.ok(
        userMapper.toDTO(userService.updateUser(userMapper.toEntity(userDTO))));
  }

  /**
   * Deletes a user by their ID.
   *
   * @param id the user ID
   * @return no content on success
   */
  @DeleteMapping("/{id}")
  @Operation(summary = "Delete user")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "User deleted successfully"),
        @ApiResponse(responseCode = "404", description = "User not found")
      })
  public ResponseEntity<Void> deleteUser(
      @Parameter(description = "User ID", required = true) @PathVariable Long id) {
    userService.deleteUser(id);
    return ResponseEntity.noContent().build();
  }
}
