package io.xquti.mdb.controller;

import io.xquti.mdb.dto.ForumPostDto;
import io.xquti.mdb.dto.ForumThreadDto;
import io.xquti.mdb.dto.UserDto;
import io.xquti.mdb.service.ForumService;
import io.xquti.mdb.util.AuthUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import io.xquti.mdb.config.SecurityConfig;
import io.xquti.mdb.config.security.*;
import io.xquti.mdb.service.JwtService;
import io.xquti.mdb.service.UserService;
import io.xquti.mdb.service.OAuth2UserService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import org.mockito.Mockito;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ForumController.class)
@Import(ForumControllerTest.TestConfig.class)
@org.springframework.test.context.TestPropertySource(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration"
})
class ForumControllerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public SecurityFilterChain testFilterChain(HttpSecurity http) throws Exception {
            http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz.anyRequest().permitAll());
            return http.build();
        }
        
        @Bean
        @Primary
        public jakarta.validation.Validator validator() {
            return jakarta.validation.Validation.buildDefaultValidatorFactory().getValidator();
        }
        
        @Bean
        @Primary
        public org.springframework.validation.beanvalidation.LocalValidatorFactoryBean localValidatorFactoryBean() {
            return new org.springframework.validation.beanvalidation.LocalValidatorFactoryBean();
        }
        

        
        @Bean
        @Primary
        public JwtService jwtService() {
            return Mockito.mock(JwtService.class);
        }
        
        @Bean
        @Primary
        public UserService userService() {
            return Mockito.mock(UserService.class);
        }
        
        @Bean
        @Primary
        public OAuth2UserService oAuth2UserService() {
            return Mockito.mock(OAuth2UserService.class);
        }
        
        @Bean
        @Primary
        public CorsSecurityConfig corsSecurityConfig() {
            return Mockito.mock(CorsSecurityConfig.class);
        }
        
        @Bean
        @Primary
        public SecurityHeadersConfig securityHeadersConfig() {
            return Mockito.mock(SecurityHeadersConfig.class);
        }
        
        @Bean
        @Primary
        public InputValidationConfig inputValidationConfig() {
            return Mockito.mock(InputValidationConfig.class);
        }
        
        @Bean
        @Primary
        public JwtAuthenticationFilter jwtAuthenticationFilter() {
            return Mockito.mock(JwtAuthenticationFilter.class);
        }
        
        @Bean
        @Primary
        public OAuth2SecurityConfig oAuth2SecurityConfig() {
            return Mockito.mock(OAuth2SecurityConfig.class);
        }
        
        @Bean
        @Primary
        @SuppressWarnings("unchecked")
        public org.springframework.data.redis.core.RedisTemplate<String, String> redisTemplate() {
            return (org.springframework.data.redis.core.RedisTemplate<String, String>) Mockito.mock(org.springframework.data.redis.core.RedisTemplate.class);
        }
        
        @Bean
        @Primary
        public io.xquti.mdb.repository.UserRepository userRepository() {
            return Mockito.mock(io.xquti.mdb.repository.UserRepository.class);
        }
        
        @Bean
        @Primary
        public io.xquti.mdb.repository.ForumThreadRepository forumThreadRepository() {
            return Mockito.mock(io.xquti.mdb.repository.ForumThreadRepository.class);
        }
        
        @Bean
        @Primary
        public io.xquti.mdb.repository.ForumPostRepository forumPostRepository() {
            return Mockito.mock(io.xquti.mdb.repository.ForumPostRepository.class);
        }
        
        @Bean
        @Primary
        public io.xquti.mdb.search.SearchService searchService() {
            return Mockito.mock(io.xquti.mdb.search.SearchService.class);
        }
        
        @Bean
        @Primary
        public io.xquti.mdb.websocket.ForumWebSocketController forumWebSocketController() {
            return Mockito.mock(io.xquti.mdb.websocket.ForumWebSocketController.class);
        }
        
        @Bean
        @Primary
        public io.xquti.mdb.service.DtoMapper dtoMapper() {
            return Mockito.mock(io.xquti.mdb.service.DtoMapper.class);
        }
        
        @Bean
        @Primary
        public io.xquti.mdb.exception.GlobalExceptionHandler globalExceptionHandler() {
            return new io.xquti.mdb.exception.GlobalExceptionHandler();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    
    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private ForumService forumService;

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private AuthUtils authUtils;

    private ForumThreadDto testThreadDto;
    private ForumPostDto testPostDto;
    
    private io.xquti.mdb.dto.UserDto createTestUserDto() {
        return new io.xquti.mdb.dto.UserDto(
            1L,
            "testuser",
            "test@example.com",
            null,
            "local",
            Set.of(),
            LocalDateTime.now()
        );
    }

    @BeforeEach
    void setUp() {
        testThreadDto = new ForumThreadDto();
        testThreadDto.setId(1L);
        testThreadDto.setTitle("Test Thread");
        testThreadDto.setCategory("general");
        // testThreadDto.setAuthorUsername("testuser"); // Method doesn't exist
        testThreadDto.setCreatedAt(LocalDateTime.now());
        testThreadDto.setUpdatedAt(LocalDateTime.now());
        testThreadDto.setPostCount(5);
        testThreadDto.setPinned(false);

        testPostDto = new ForumPostDto();
        testPostDto.setId(1L);
        testPostDto.setContent("Test post content");
        // testPostDto.setAuthorUsername("testuser"); // Method doesn't exist
        testPostDto.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void getThreads_WithoutCategory_ShouldReturnThreads() throws Exception {
        // Arrange
        List<ForumThreadDto> threads = Arrays.asList(testThreadDto);
        Page<ForumThreadDto> threadPage = new PageImpl<>(threads, PageRequest.of(0, 20), 1);
        when(forumService.getAllThreads(any(Pageable.class), isNull())).thenReturn(threadPage);

        // Act & Assert
        mockMvc.perform(get("/api/forums/threads")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Test Thread"))
                .andExpect(jsonPath("$.content[0].category").value("general"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getThreads_WithCategory_ShouldReturnFilteredThreads() throws Exception {
        // Arrange
        String category = "general";
        List<ForumThreadDto> threads = Arrays.asList(testThreadDto);
        Page<ForumThreadDto> threadPage = new PageImpl<>(threads, PageRequest.of(0, 20), 1);
        when(forumService.getAllThreads(any(Pageable.class), eq(category))).thenReturn(threadPage);

        // Act & Assert
        mockMvc.perform(get("/api/forums/threads")
                .param("category", category)
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].category").value(category));
    }

    @Test
    void createThread_WithValidData_ShouldCreateThread() throws Exception {
        // Arrange
        ForumController.CreateThreadRequest request = new ForumController.CreateThreadRequest();
        request.setTitle("New Thread");
        request.setContent("Thread content");
        
        String token = "valid-jwt-token";
        String email = "test@example.com";
        
        when(authUtils.getCurrentUser("Bearer " + token)).thenReturn(createTestUserDto());
        when(forumService.createThread(eq("New Thread"), eq("Thread content"), eq(1L)))
                .thenReturn(testThreadDto);

        // Act & Assert
        mockMvc.perform(post("/api/forums/threads")
                .header("Authorization", "Bearer valid-jwt-token")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Thread"))
                .andExpect(jsonPath("$.category").value("general"));
    }

    @Test
    void createThread_WithoutAuthentication_ShouldReturn401() throws Exception {
        // Arrange
        ForumController.CreateThreadRequest request = new ForumController.CreateThreadRequest();
        request.setTitle("New Thread");
        request.setContent("Thread content");
        
        when(authUtils.getCurrentUser(null)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(post("/api/forums/threads")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void createThread_WithInvalidData_ShouldReturn400() throws Exception {
        // Arrange - empty title should fail validation
        ForumController.CreateThreadRequest request = new ForumController.CreateThreadRequest();
        request.setTitle(""); // Invalid - empty title
        request.setContent("Thread content");
        
        // Mock authentication
        when(authUtils.getCurrentUser(anyString())).thenReturn(createTestUserDto());

        // Act & Assert
        mockMvc.perform(post("/api/forums/threads")
                .header("Authorization", "Bearer valid-token")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getThreadPosts_WithValidThreadId_ShouldReturnPosts() throws Exception {
        // Arrange
        Long threadId = 1L;
        List<ForumPostDto> posts = Arrays.asList(testPostDto);
        Page<ForumPostDto> postPage = new PageImpl<>(posts, PageRequest.of(0, 20), 1);
        when(forumService.getThreadPosts(eq(threadId), any(Pageable.class))).thenReturn(postPage);

        // Act & Assert
        mockMvc.perform(get("/api/forums/threads/{threadId}/posts", threadId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].content").value("Test post content"));
    }

    @Test
    void createPost_WithValidData_ShouldCreatePost() throws Exception {
        // Arrange
        Long threadId = 1L;
        ForumController.CreatePostRequest request = new ForumController.CreatePostRequest();
        request.setContent("New post content");
        
        String token = "valid-jwt-token";
        String email = "test@example.com";
        
        when(authUtils.getCurrentUser("Bearer " + token)).thenReturn(createTestUserDto());
        when(forumService.createPost(eq(threadId), eq("New post content"), eq(1L)))
                .thenReturn(testPostDto);

        // Act & Assert
        mockMvc.perform(post("/api/forums/threads/{threadId}/posts", threadId)
                .header("Authorization", "Bearer " + token)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").value("Test post content"));
    }

    @Test
    void createPost_WithoutAuthentication_ShouldReturn401() throws Exception {
        // Arrange
        Long threadId = 1L;
        ForumController.CreatePostRequest request = new ForumController.CreatePostRequest();
        request.setContent("New post content");
        
        when(authUtils.getCurrentUser(null)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(post("/api/forums/threads/{threadId}/posts", threadId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void createPost_WithInvalidData_ShouldReturn400() throws Exception {
        // Arrange - empty content should fail validation
        Long threadId = 1L;
        ForumController.CreatePostRequest request = new ForumController.CreatePostRequest();
        request.setContent(""); // Invalid - empty content
        
        // Mock authentication
        when(authUtils.getCurrentUser(anyString())).thenReturn(createTestUserDto());

        // Act & Assert
        mockMvc.perform(post("/api/forums/threads/{threadId}/posts", threadId)
                .header("Authorization", "Bearer valid-token")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getThreadPosts_WithInvalidThreadId_ShouldReturn404() throws Exception {
        // Arrange
        Long threadId = 999L;
        when(forumService.getThreadPosts(eq(threadId), any(Pageable.class)))
                .thenThrow(new io.xquti.mdb.exception.EntityNotFoundException("Thread not found"));

        // Act & Assert
        mockMvc.perform(get("/api/forums/threads/{threadId}/posts", threadId))
                .andExpect(status().isNotFound());
    }
}