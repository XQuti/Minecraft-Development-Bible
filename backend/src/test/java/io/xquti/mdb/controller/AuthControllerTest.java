package io.xquti.mdb.controller;

import io.xquti.mdb.dto.UserDto;
import io.xquti.mdb.util.AuthUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.boot.test.context.TestConfiguration;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
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

@WebMvcTest(controllers = AuthController.class)
@Import(AuthControllerTest.TestConfig.class)
class AuthControllerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public AuthUtils authUtils() {
            return Mockito.mock(AuthUtils.class);
        }
        
        @Bean
        @Primary
        public io.xquti.mdb.service.JwtService jwtService() {
            return Mockito.mock(io.xquti.mdb.service.JwtService.class);
        }
        
        @Bean
        @Primary
        public io.xquti.mdb.service.UserService userService() {
            return Mockito.mock(io.xquti.mdb.service.UserService.class);
        }
        
        @Bean
        @Primary
        public io.xquti.mdb.config.security.TokenBlacklistService tokenBlacklistService() {
            return Mockito.mock(io.xquti.mdb.config.security.TokenBlacklistService.class);
        }
        
        @Bean
        @Primary
        public SecurityFilterChain testFilterChain(HttpSecurity http) throws Exception {
            http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz.anyRequest().permitAll());
            return http.build();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    
    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private AuthUtils authUtils;
    
    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private io.xquti.mdb.service.JwtService jwtService;
    
    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private io.xquti.mdb.service.UserService userService;
    
    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private io.xquti.mdb.config.security.TokenBlacklistService tokenBlacklistService;
    
    private UserDto testUserDto;

    @BeforeEach
    void setUp() {
        testUserDto = new UserDto(
            1L,
            "testuser",
            "test@example.com",
            null,
            "local",
            Set.of(),
            LocalDateTime.now()
        );
    }

    @Test
    void getCurrentUser_WithValidToken_ShouldReturnUser() throws Exception {
        // Arrange
        String token = "valid-jwt-token";
        String email = "test@example.com";
        
        when(authUtils.getCurrentUser("Bearer " + token)).thenReturn(testUserDto);

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
        
        when(authUtils.getCurrentUser("Bearer " + token)).thenReturn(null);

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
        
        when(authUtils.getCurrentUser("Bearer " + token)).thenReturn(null);

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
        
        when(authUtils.getCurrentUser("Bearer " + token)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/auth/me")
                .header("Authorization", "Bearer " + token)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getCurrentUser_WithoutAuthorizationHeader_ShouldReturn401() throws Exception {
        // Arrange
        when(authUtils.getCurrentUser(null)).thenReturn(null);
        
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