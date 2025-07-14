package io.xquti.mdb.service;

import io.xquti.mdb.dto.UserDto;
import io.xquti.mdb.exception.EntityNotFoundException;
import io.xquti.mdb.model.User;
import io.xquti.mdb.repository.UserRepository;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private DtoMapper dtoMapper;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserDto testUserDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setUsername("testuser");
        testUser.setProvider("local");
        testUser.setCreatedAt(LocalDateTime.now());

        testUserDto = new UserDto(
            1L,
            "testuser",
            "test@example.com",
            null,
            "local",
            Set.of(User.Role.USER),
            LocalDateTime.now()
        );
    }

    @Test
    void findByEmail_WithExistingUser_ShouldReturnUser() {
        // Arrange
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

        // Act
        Optional<User> result = userService.findByEmail(email);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testUser.getEmail(), result.get().getEmail());
        verify(userRepository).findByEmail(email);
    }

    @Test
    void findByEmail_WithNonExistentUser_ShouldReturnEmpty() {
        // Arrange
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.findByEmail(email);

        // Assert
        assertFalse(result.isPresent());
        verify(userRepository).findByEmail(email);
    }

    @Test
    void findByEmailDto_WithExistingUser_ShouldReturnUserDto() {
        // Arrange
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(dtoMapper.toUserDto(any(User.class))).thenReturn(testUserDto);

        // Act
        UserDto result = userService.findByEmailDto(email);

        // Assert
        assertNotNull(result);
        assertEquals(testUserDto.email(), result.email());
        verify(userRepository).findByEmail(email);
        verify(dtoMapper).toUserDto(testUser);
    }

    @Test
    void findByEmailDto_WithNonExistentUser_ShouldReturnNull() {
        // Arrange
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act
        UserDto result = userService.findByEmailDto(email);

        // Assert
        assertNull(result);
        verify(userRepository).findByEmail(email);
        verify(dtoMapper, never()).toUserDto(any());
    }

    @Test
    void save_WithValidUser_ShouldSaveUser() {
        // Arrange
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User result = userService.save(testUser);

        // Assert
        assertNotNull(result);
        assertEquals(testUser.getEmail(), result.getEmail());
        verify(userRepository).save(testUser);
    }

    @Test
    void existsByEmail_WithExistingUser_ShouldReturnTrue() {
        // Arrange
        String email = "test@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);

        // Act
        boolean result = userService.existsByEmail(email);

        // Assert
        assertTrue(result);
        verify(userRepository).existsByEmail(email);
    }

    @Test
    void existsByEmail_WithNonExistentUser_ShouldReturnFalse() {
        // Arrange
        String email = "nonexistent@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(false);

        // Act
        boolean result = userService.existsByEmail(email);

        // Assert
        assertFalse(result);
        verify(userRepository).existsByEmail(email);
    }
}