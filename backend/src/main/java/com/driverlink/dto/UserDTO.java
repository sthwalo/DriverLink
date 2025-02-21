package com.driverlink.dto;

import com.driverlink.validation.ValidPassword;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for User entities. Handles the transfer of user data between the client and
 * server, implementing validation and documentation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User Data Transfer Object")
public class UserDTO {

  @Schema(description = "User ID", example = "1")
  private Long id;

  @NotBlank(message = "Username is required")
  @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
  @Pattern(
      regexp = "^[a-zA-Z0-9._-]+$",
      message = "Username can only contain letters, numbers, dots, underscores and hyphens")
  @Schema(description = "Username", example = "john.doe")
  private String username;

  @NotBlank(message = "Email is required")
  @Email(message = "Email should be valid")
  @Schema(description = "Email address", example = "john.doe@example.com")
  private String email;

  @ValidPassword
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @Schema(
      description =
          "Password (must be at least 8 characters with at least one uppercase letter, "
              + "one lowercase letter, one number and one special character)")
  private String password;

  @NotNull(message = "Role is required")
  @Pattern(regexp = "^(ADMIN|USER|DRIVER)$", message = "Role must be either ADMIN, USER or DRIVER")
  @Schema(description = "User role", example = "USER")
  private String role;

  @Schema(description = "Account creation date")
  private LocalDateTime createdAt;

  @Schema(description = "Whether the account is active")
  private boolean active;
}
