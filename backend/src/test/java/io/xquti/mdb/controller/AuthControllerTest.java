package io.xquti.mdb.controller;

import io.xquti.mdb.dto.UserDto;
import io.xquti.mdb.service.JwtService;
import io.xquti.mdb.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import io.xquti.mdb.config.SecurityConfig;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDto testUserDto;

    @BeforeEach
    void setUp() {
        testUserDto = new UserDto();
        testUserDto.setId(1L);
        testUserDto.setEmail("test@example.com");
        testUserDto.setUsername("testuser");
        testUserDto.setProvider("local");
        testUserDto.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void getCurrentUser_WithValidToken_ShouldReturnUser() throws Exception {
        // Arrange
        String token = "valid-jwt-token";
        String email = "test@example.com";
        
        when(jwtService.extractUsername(token)).thenReturn(email);
        when(jwtService.validateToken(token, email)).thenReturn(true);
        when(userService.findByEmailDto(email)).thenReturn(testUserDto);

        // Act & Assert
        mockMvc.perform(get("/api/auth/me")
                .header("Authorization", "Bearer " + token)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void getCurrentUser_WithInvalidToken_ShouldReturn401() throws Exception {
        // Arrange
        String token = "invalid-jwt-token";
        String email = "test@example.com";
        
        when(jwtService.extractUsername(token)).thenReturn(email);
        when(jwtService.validateToken(token, email)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(get("/api/auth/me")
                .header("Authorization", "Bearer " + token)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getCurrentUser_WithMalformedToken_ShouldReturn401() throws Exception {
        // Arrange
        String token = "malformed-token";
        
        when(jwtService.extractUsername(token)).thenThrow(new RuntimeException("Invalid token"));

        // Act & Assert
        mockMvc.perform(get("/api/auth/me")
                .header("Authorization", "Bearer " + token)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getCurrentUser_WithNonExistentUser_ShouldReturn401() throws Exception {
        // Arrange
        String token = "valid-jwt-token";
        String email = "nonexistent@example.com";
        
        when(jwtService.extractUsername(token)).thenReturn(email);
        when(jwtService.validateToken(token, email)).thenReturn(true);
        when(userService.findByEmailDto(email)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/auth/me")
                .header("Authorization", "Bearer " + token)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getCurrentUser_WithoutAuthorizationHeader_ShouldReturn401() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/auth/me")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void logout_ShouldReturnSuccessMessage() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/auth/logout")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Logged out successfully"));
    }
}