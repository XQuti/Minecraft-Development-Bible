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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import org.mockito.Mockito;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ForumController.class)
@Import(ForumControllerTest.TestSecurityConfig.class)
class ForumControllerTest {

    @TestConfiguration
    static class TestSecurityConfig {
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

    @MockBean
    private ForumService forumService;

    @MockBean
    private AuthUtils authUtils;

    @Autowired
    private ObjectMapper objectMapper;

    private ForumThreadDto testThreadDto;
    private ForumPostDto testPostDto;
    
    private io.xquti.mdb.dto.UserDto createTestUserDto() {
        io.xquti.mdb.dto.UserDto userDto = new io.xquti.mdb.dto.UserDto();
        userDto.setId(1L);
        userDto.setEmail("test@example.com");
        userDto.setUsername("testuser");
        return userDto;
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
        when(forumService.getAllThreads(any(), isNull())).thenReturn(threadPage);

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
        when(forumService.getAllThreads(any(), eq(category))).thenReturn(threadPage);

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
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
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
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void createThread_WithInvalidData_ShouldReturn400() throws Exception {
        // Arrange - empty title should fail validation
        ForumController.CreateThreadRequest request = new ForumController.CreateThreadRequest();
        request.setTitle(""); // Invalid - empty title
        request.setContent("Thread content");

        // Act & Assert
        mockMvc.perform(post("/api/forums/threads")
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
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
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
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void createPost_WithInvalidData_ShouldReturn400() throws Exception {
        // Arrange - empty content should fail validation
        Long threadId = 1L;
        ForumController.CreatePostRequest request = new ForumController.CreatePostRequest();
        request.setContent(""); // Invalid - empty content

        // Act & Assert
        mockMvc.perform(post("/api/forums/threads/{threadId}/posts", threadId)
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