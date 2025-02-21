package com.driverlink.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/** Represents a user in the system. This entity stores user information and credentials. */
@Entity
@Table(
    name = "users",
    uniqueConstraints = {
      @UniqueConstraint(columnNames = "username"),
      @UniqueConstraint(columnNames = "email")
    })
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class User extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "Username is required")
  @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
  @Column(nullable = false)
  private String username;

  @NotBlank(message = "Email is required")
  @Email(message = "Email should be valid")
  @Column(nullable = false, unique = true)
  private String email;

  @NotBlank(message = "Password is required")
  @Size(min = 6, message = "Password must be at least 6 characters")
  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  private boolean active = true;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
  @Enumerated(EnumType.STRING)
  @Column(name = "role")
  private Set<Role> roles = new HashSet<>();

  @Column(nullable = false)
  private LocalDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    this.createdAt = LocalDateTime.now();
  }

  // Builder pattern
  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private final User user;

    private Builder() {
      user = new User();
    }

    public Builder username(String username) {
      user.setUsername(username);
      return this;
    }

    public Builder email(String email) {
      user.setEmail(email);
      return this;
    }

    public Builder password(String password) {
      user.setPassword(password);
      return this;
    }

    public Builder role(Set<Role> roles) {
      user.setRoles(roles);
      return this;
    }

    public Builder active(boolean active) {
      user.setActive(active);
      return this;
    }

    public User build() {
      return user;
    }
  }
}
