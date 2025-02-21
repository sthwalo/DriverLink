package com.driverlink.mapper;

import com.driverlink.dto.UserDTO;
import com.driverlink.model.User;
import org.springframework.stereotype.Component;

/**
 * Mapper class to convert between User entity and UserDTO. Follows the principle of separation of
 * concerns by handling object transformations.
 */
@Component
public class UserMapper {

  /** Converts a User entity to UserDTO. Note: Password is not included in the DTO for security. */
  public UserDTO toDTO(User user) {
    if (user == null) {
      return null;
    }

    UserDTO dto = new UserDTO();
    dto.setId(user.getId());
    dto.setUsername(user.getUsername());
    dto.setEmail(user.getEmail());
    dto.setRole(user.getRole());
    dto.setCreatedAt(user.getCreatedAt());
    dto.setActive(user.isActive());
    // Password is intentionally not mapped for security
    return dto;
  }

  /** Converts a UserDTO to User entity. Used for creating new users or updating existing ones. */
  public User toEntity(UserDTO dto) {
    if (dto == null) {
      return null;
    }

    return User.builder()
        .username(dto.getUsername())
        .email(dto.getEmail())
        .password(dto.getPassword())
        .role(dto.getRole())
        .active(dto.isActive())
        .build();
  }

  /** Updates an existing User entity with DTO data. Only updates non-null fields from the DTO. */
  public void updateEntityFromDTO(UserDTO dto, User user) {
    if (dto == null || user == null) {
      return;
    }

    if (dto.getUsername() != null) {
      user.setUsername(dto.getUsername());
    }
    if (dto.getEmail() != null) {
      user.setEmail(dto.getEmail());
    }
    if (dto.getPassword() != null) {
      user.setPassword(dto.getPassword());
    }
    if (dto.getRole() != null) {
      user.setRole(dto.getRole());
    }
  }
}
