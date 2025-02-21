package com.driverlink.exception;

/** Exception thrown for user-related errors. */
public class UserException extends RuntimeException {
  private final String code;

  public UserException(String message) {
    super(message);
    this.code = null;
  }

  public UserException(String message, Throwable cause) {
    super(message, cause);
    this.code = null;
  }

  public UserException(String message, String code) {
    super(message);
    this.code = code;
  }

  public String getCode() {
    return code;
  }

  public static class UserNotFoundException extends UserException {
    public UserNotFoundException(Long id) {
      super("User not found with id: " + id, "USER_NOT_FOUND");
    }

    public UserNotFoundException(String message) {
      super(message, "USER_NOT_FOUND");
    }
  }

  public static class DuplicateUsernameException extends UserException {
    public DuplicateUsernameException() {
      super("Username already exists", "DUPLICATE_USERNAME");
    }
  }

  public static class DuplicateEmailException extends UserException {
    public DuplicateEmailException() {
      super("Email already exists", "DUPLICATE_EMAIL");
    }
  }

  public static class InvalidInputException extends UserException {
    public InvalidInputException(String message) {
      super(message, "INVALID_INPUT");
    }
  }
}
