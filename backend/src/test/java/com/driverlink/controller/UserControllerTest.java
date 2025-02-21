package com.driverlink.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.driverlink.dto.UserDTO;
import com.driverlink.mapper.UserMapper;
import com.driverlink.model.User;
import com.driverlink.service.UserService;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

  @Mock private UserService userService;

  @Mock private UserMapper userMapper;

  @InjectMocks private UserController userController;

  private User testUser;
  private UserDTO testUserDTO;

  @BeforeEach
  void setUp() {
    testUser =
        User.builder()
            .username("testuser")
            .email("test@example.com")
            .password("Password123!")
            .role("USER")
            .active(true)
            .build();
    testUser.setId(1L); // Set ID after creation

    testUserDTO = new UserDTO();
    testUserDTO.setId(1L);
    testUserDTO.setUsername("testuser");
    testUserDTO.setEmail("test@example.com");
    testUserDTO.setPassword("Password123!");
    testUserDTO.setRole("USER");
    testUserDTO.setActive(true);
  }

  @Test
  void createUser_Success() {
    // Arrange
    when(userMapper.toEntity(any(UserDTO.class))).thenReturn(testUser);
    when(userService.createUser(any(User.class))).thenReturn(testUser);
    when(userMapper.toDTO(any(User.class))).thenReturn(testUserDTO);

    // Act
    ResponseEntity<UserDTO> response = userController.createUser(testUserDTO);

    // Assert
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(testUserDTO.getUsername(), response.getBody().getUsername());
  }

  @Test
  void getUser_Success() {
    // Arrange
    when(userService.getUserById(1L)).thenReturn(testUser);
    when(userMapper.toDTO(testUser)).thenReturn(testUserDTO);

    // Act
    ResponseEntity<UserDTO> response = userController.getUser(1L);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(testUserDTO.getUsername(), response.getBody().getUsername());
  }

  @Test
  void getAllUsers_Success() {
    // Arrange
    List<User> users = Arrays.asList(testUser);
    List<UserDTO> userDTOs = Arrays.asList(testUserDTO);
    when(userService.getAllUsers()).thenReturn(users);
    when(userMapper.toDTO(testUser)).thenReturn(testUserDTO);

    // Act
    ResponseEntity<List<UserDTO>> response = userController.getAllUsers();

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(1, response.getBody().size());
    assertEquals(testUserDTO.getUsername(), response.getBody().get(0).getUsername());
  }

  @Test
  void updateUser_Success() {
    // Arrange
    when(userMapper.toEntity(any(UserDTO.class))).thenReturn(testUser);
    when(userService.updateUser(any(User.class))).thenReturn(testUser);
    when(userMapper.toDTO(any(User.class))).thenReturn(testUserDTO);

    // Act
    ResponseEntity<UserDTO> response = userController.updateUser(1L, testUserDTO);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(testUserDTO.getUsername(), response.getBody().getUsername());
  }

  @Test
  void deleteUser_Success() {
    // Arrange
    doNothing().when(userService).deleteUser(1L);

    // Act
    ResponseEntity<Void> response = userController.deleteUser(1L);

    // Assert
    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    verify(userService).deleteUser(1L);
  }
}
