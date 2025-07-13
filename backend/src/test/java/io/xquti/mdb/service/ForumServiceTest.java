package io.xquti.mdb.service;

import io.xquti.mdb.dto.ForumPostDto;
import io.xquti.mdb.dto.ForumThreadDto;
import io.xquti.mdb.exception.EntityNotFoundException;
import io.xquti.mdb.exception.ForbiddenException;
import io.xquti.mdb.model.ForumPost;
import io.xquti.mdb.model.ForumThread;
import io.xquti.mdb.model.User;
import io.xquti.mdb.repository.ForumPostRepository;
import io.xquti.mdb.repository.ForumThreadRepository;
import io.xquti.mdb.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ForumServiceTest {

    @Mock
    private ForumThreadRepository forumThreadRepository;

    @Mock
    private ForumPostRepository forumPostRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DtoMapper dtoMapper;

    @InjectMocks
    private ForumService forumService;

    private User testUser;
    private ForumThread testThread;
    private ForumPost testPost;
    private ForumThreadDto testThreadDto;
    private ForumPostDto testPostDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setUsername("testuser");

        testThread = new ForumThread();
        testThread.setId(1L);
        testThread.setTitle("Test Thread");
        testThread.setCategory("general");
        testThread.setAuthor(testUser);
        testThread.setCreatedAt(LocalDateTime.now());
        testThread.setUpdatedAt(LocalDateTime.now());

        testPost = new ForumPost();
        testPost.setId(1L);
        testPost.setContent("Test post content");
        testPost.setThread(testThread);
        testPost.setAuthor(testUser);
        testPost.setCreatedAt(LocalDateTime.now());

        testThreadDto = new ForumThreadDto();
        testThreadDto.setId(1L);
        testThreadDto.setTitle("Test Thread");
        testThreadDto.setCategory("general");

        testPostDto = new ForumPostDto();
        testPostDto.setId(1L);
        testPostDto.setContent("Test post content");
    }

    @Test
    void getAllThreads_WithoutCategory_ShouldReturnAllThreads() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<ForumThread> threads = Arrays.asList(testThread);
        Page<ForumThread> threadPage = new PageImpl<>(threads, pageable, 1);
        Page<ForumThreadDto> expectedDtoPage = new PageImpl<>(Arrays.asList(testThreadDto), pageable, 1);

        when(forumThreadRepository.findAllOrderByPinnedAndUpdated(pageable)).thenReturn(threadPage);
        when(dtoMapper.toForumThreadDto(testThread)).thenReturn(testThreadDto);

        // Act
        Page<ForumThreadDto> result = forumService.getAllThreads(pageable, null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testThreadDto.getTitle(), result.getContent().get(0).getTitle());
        verify(forumThreadRepository).findAllOrderByPinnedAndUpdated(pageable);
        // Page mapping is handled internally
    }

    @Test
    void getAllThreads_WithCategory_ShouldReturnFilteredThreads() {
        // Arrange
        String category = "general";
        Pageable pageable = PageRequest.of(0, 10);
        List<ForumThread> threads = Arrays.asList(testThread);
        Page<ForumThread> threadPage = new PageImpl<>(threads, pageable, 1);
        Page<ForumThreadDto> expectedDtoPage = new PageImpl<>(Arrays.asList(testThreadDto), pageable, 1);

        when(forumThreadRepository.findByCategoryOrderByPinnedAndUpdated(category, pageable)).thenReturn(threadPage);
        when(dtoMapper.toForumThreadDto(testThread)).thenReturn(testThreadDto);

        // Act
        Page<ForumThreadDto> result = forumService.getAllThreads(pageable, category);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(forumThreadRepository).findByCategoryOrderByPinnedAndUpdated(category, pageable);
        // Page mapping is handled internally
    }

    @Test
    void createThread_WithValidData_ShouldCreateThread() {
        // Arrange
        String title = "New Thread";
        String content = "Thread content";
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(forumThreadRepository.save(any(ForumThread.class))).thenReturn(testThread);
        when(dtoMapper.toForumThreadDto(any(ForumThread.class))).thenReturn(testThreadDto);

        // Act
        ForumThreadDto result = forumService.createThread(title, content, userId);

        // Assert
        assertNotNull(result);
        assertEquals(testThreadDto.getTitle(), result.getTitle());
        verify(userRepository).findById(userId);
        verify(forumThreadRepository).save(any(ForumThread.class));
        verify(dtoMapper).toForumThreadDto(testThread);
    }

    @Test
    void createThread_WithNonExistentUser_ShouldThrowException() {
        // Arrange
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> 
            forumService.createThread("Title", "Content", userId));
        verify(userRepository).findById(userId);
        verify(forumThreadRepository, never()).save(any());
    }

    @Test
    void getThreadPosts_WithValidThreadId_ShouldReturnPosts() {
        // Arrange
        Long threadId = 1L;
        Pageable pageable = PageRequest.of(0, 20);
        List<ForumPost> posts = Arrays.asList(testPost);
        Page<ForumPost> postPage = new PageImpl<>(posts, pageable, 1);
        Page<ForumPostDto> expectedDtoPage = new PageImpl<>(Arrays.asList(testPostDto), pageable, 1);

        when(forumThreadRepository.existsById(threadId)).thenReturn(true);
        when(forumPostRepository.findByThreadIdOrderByCreatedAtAsc(threadId, pageable)).thenReturn(postPage);
        when(dtoMapper.toForumPostDto(testPost)).thenReturn(testPostDto);

        // Act
        Page<ForumPostDto> result = forumService.getThreadPosts(threadId, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(forumThreadRepository).existsById(threadId);
        verify(forumPostRepository).findByThreadIdOrderByCreatedAtAsc(threadId, pageable);
    }

    @Test
    void getThreadPosts_WithNonExistentThread_ShouldThrowException() {
        // Arrange
        Long threadId = 999L;
        Pageable pageable = PageRequest.of(0, 20);
        when(forumThreadRepository.existsById(threadId)).thenReturn(false);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            forumService.getThreadPosts(threadId, pageable);
        });
        
        verify(forumThreadRepository).existsById(threadId);
        verify(forumPostRepository, never()).findByThreadIdOrderByCreatedAtAsc(any(), any());
    }

    @Test
    void createPost_WithValidData_ShouldCreatePost() {
        // Arrange
        Long threadId = 1L;
        String content = "New post content";
        Long userId = 1L;

        when(forumThreadRepository.findById(threadId)).thenReturn(Optional.of(testThread));
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(forumPostRepository.save(any(ForumPost.class))).thenReturn(testPost);
        when(dtoMapper.toForumPostDto(any(ForumPost.class))).thenReturn(testPostDto);

        // Act
        ForumPostDto result = forumService.createPost(threadId, content, userId);

        // Assert
        assertNotNull(result);
        assertEquals(testPostDto.getContent(), result.getContent());
        verify(forumThreadRepository).findById(threadId);
        verify(userRepository).findById(userId);
        verify(forumPostRepository).save(any(ForumPost.class));
        verify(dtoMapper).toForumPostDto(testPost);
    }

    @Test
    void createPost_WithNonExistentThread_ShouldThrowException() {
        // Arrange
        Long threadId = 999L;
        when(forumThreadRepository.findById(threadId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            forumService.createPost(threadId, "Content", 1L);
        });
        
        verify(forumThreadRepository).findById(threadId);
        verify(forumPostRepository, never()).save(any());
    }

    @Test
    void createPost_WithNonExistentUser_ShouldThrowException() {
        // Arrange
        Long threadId = 1L;
        Long userId = 999L;

        when(forumThreadRepository.findById(threadId)).thenReturn(Optional.of(testThread));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> 
            forumService.createPost(threadId, "Content", userId));
        verify(forumThreadRepository).findById(threadId);
        verify(userRepository).findById(userId);
        verify(forumPostRepository, never()).save(any());
    }
}