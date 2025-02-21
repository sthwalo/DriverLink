package com.driverlink.mapper;

import com.driverlink.dto.UserDTO;
import com.driverlink.model.Role;
import com.driverlink.model.User;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
    dto.setActive(user.isActive());
    dto.setCreatedAt(user.getCreatedAt());
    dto.setRoles(user.getRoles().stream()
        .map(Role::name)
        .collect(Collectors.toSet()));
    return dto;
  }

  /** Converts a UserDTO to User entity. Used for creating new users or updating existing ones. */
  public User toEntity(UserDTO dto) {
    if (dto == null) {
      return null;
    }

    User user = new User();
    updateEntity(user, dto);
    return user;
  }

  /** Updates an existing User entity with DTO data. Only updates non-null fields from the DTO. */
  public void updateEntity(User user, UserDTO dto) {
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
    if (dto.getRoles() != null && !dto.getRoles().isEmpty()) {
      Set<Role> roles = dto.getRoles().stream()
          .map(roleStr -> {
            try {
              return Role.valueOf(roleStr.toUpperCase());
            } catch (IllegalArgumentException e) {
              throw new IllegalArgumentException("Invalid role: " + roleStr);
            }
          })
          .collect(Collectors.toSet());
      user.setRoles(roles);
    } else {
      // Set default role if none provided
      user.setRoles(Collections.singleton(Role.ROLE_USER));
    }
    if (dto.getActive() != null) {
      user.setActive(dto.getActive());
    }
  }
}
