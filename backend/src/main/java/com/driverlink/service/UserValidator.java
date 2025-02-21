package com.driverlink.service;

import com.driverlink.exception.UserException.InvalidInputException;
import com.driverlink.model.User;
import com.driverlink.repository.UserRepository;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class UserValidator {
  private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

  private final UserRepository userRepository;

  public UserValidator(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public void validateNewUser(User user) {
    validateRequiredFields(user);
    validateEmail(user.getEmail());
    validateUniqueUsername(user.getUsername());
    validateUniqueEmail(user.getEmail());
  }

  public void validateUpdateUser(User user, User existingUser) {
    validateRequiredFields(user);
    validateEmail(user.getEmail());

    if (!existingUser.getUsername().equals(user.getUsername())) {
      validateUniqueUsername(user.getUsername());
    }

    if (!existingUser.getEmail().equals(user.getEmail())) {
      validateUniqueEmail(user.getEmail());
    }
  }

  private void validateRequiredFields(User user) {
    if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
      throw new InvalidInputException("Username is required");
    }
    if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
      throw new InvalidInputException("Email is required");
    }
  }

  private void validateEmail(String email) {
    if (!EMAIL_PATTERN.matcher(email).matches()) {
      throw new InvalidInputException("Invalid email format");
    }
  }

  private void validateUniqueUsername(String username) {
    if (userRepository.findByUsername(username) != null) {
      throw new InvalidInputException("Username already exists");
    }
  }

  private void validateUniqueEmail(String email) {
    if (userRepository.findByEmail(email) != null) {
      throw new InvalidInputException("Email already exists");
    }
  }
}
