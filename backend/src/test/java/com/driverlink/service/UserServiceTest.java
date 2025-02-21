package com.driverlink.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.driverlink.exception.UserException;
import com.driverlink.model.User;
import com.driverlink.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserValidator userValidator;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("Password123!")
                .role("USER")
                .active(true)
                .build();
        testUser.setId(1L);
    }

    @Test
    void createUser_Success() {
        // Arrange
        User inputUser = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("Password123!")
                .role("USER")
                .active(true)
                .build();

        String encodedPassword = "encodedPassword";
        when(passwordEncoder.encode(inputUser.getPassword())).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1L);
            return savedUser;
        });
        doNothing().when(userValidator).validateNewUser(any(User.class));

        // Act
        User createdUser = userService.createUser(inputUser);

        // Assert
        assertNotNull(createdUser);
        assertEquals(inputUser.getUsername(), createdUser.getUsername());
        assertEquals(encodedPassword, createdUser.getPassword());
        verify(userValidator).validateNewUser(inputUser);
        verify(passwordEncoder).encode(inputUser.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void getUserById_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        User foundUser = userService.getUserById(1L);

        // Assert
        assertNotNull(foundUser);
        assertEquals(testUser.getUsername(), foundUser.getUsername());
    }

    @Test
    void getUserById_NotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserException.class, () -> userService.getUserById(1L));
    }

    @Test
    void getAllUsers_Success() {
        // Arrange
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(users);

        // Act
        List<User> foundUsers = userService.getAllUsers();

        // Assert
        assertNotNull(foundUsers);
        assertEquals(1, foundUsers.size());
        assertEquals(testUser.getUsername(), foundUsers.get(0).getUsername());
    }

    @Test
    void updateUser_Success() {
        // Arrange
        User updatedUser = User.builder()
                .username("updateduser")
                .email("updated@example.com")
                .password("UpdatedPass123!")
                .role("USER")
                .active(true)
                .build();
        updatedUser.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        doNothing().when(userValidator).validateUpdateUser(any(User.class), any(User.class));

        // Act
        User result = userService.updateUser(updatedUser);

        // Assert
        assertNotNull(result);
        assertEquals(updatedUser.getUsername(), result.getUsername());
        assertEquals(updatedUser.getEmail(), result.getEmail());
    }

    @Test
    void deleteUser_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).delete(testUser);

        // Act & Assert
        assertDoesNotThrow(() -> userService.deleteUser(1L));
        verify(userRepository).delete(testUser);
    }
}
