package io.xquti.mdb.dto;

import io.xquti.mdb.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Data Transfer Object for User entity using Java 24 record pattern matching.
 * Demonstrates modern Java features while maintaining backward compatibility.
 */
public record UserDto(
    Long id,
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    String username,
    
    @Email(message = "Email must be valid")
    @NotBlank(message = "Email is required")
    String email,
    
    String avatarUrl,
    String provider,
    Set<User.Role> roles,
    LocalDateTime createdAt
) {
    
    /**
     * Factory method demonstrating Java 24 pattern matching with records
     */
    public static UserDto fromUser(User user) {
        return switch (user) {
            case User u when u.getId() != null -> new UserDto(
                u.getId(),
                u.getUsername(),
                u.getEmail(),
                u.getAvatarUrl(),
                u.getProvider(),
                u.getRoles(),
                u.getCreatedAt()
            );
            case null -> throw new IllegalArgumentException("User cannot be null");
            default -> throw new IllegalStateException("Invalid user state");
        };
    }
    
    /**
     * Compact constructor with validation using Java 24 features
     */
    public UserDto {
        if (id != null && id <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }
        if (username != null && username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (email != null && !email.contains("@")) {
            throw new IllegalArgumentException("Email must be valid");
        }
    }
}