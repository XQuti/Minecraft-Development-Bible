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
import io.xquti.mdb.search.SearchService;
import io.xquti.mdb.websocket.ForumWebSocketController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Gatherers;

@Service
@Transactional
public class ForumService {
    
    private static final Logger logger = LoggerFactory.getLogger(ForumService.class);
    
    @Autowired
    private ForumThreadRepository forumThreadRepository;
    
    @Autowired
    private ForumPostRepository forumPostRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private DtoMapper dtoMapper;
    
    @Autowired
    private SearchService searchService;
    
    @Autowired
    private ForumWebSocketController webSocketController;
    
    // Thread operations
    public Page<ForumThreadDto> getAllThreads(Pageable pageable, String category) {
        logger.debug("Fetching paged forum threads with category: {}", category);
        Page<ForumThread> threads;
        
        if (category != null && !category.trim().isEmpty()) {
            threads = forumThreadRepository.findByCategoryOrderByPinnedAndUpdated(category, pageable);
        } else {
            threads = forumThreadRepository.findAllOrderByPinnedAndUpdated(pageable);
        }
        return threads.map(dtoMapper::toForumThreadDto);
    }
    
    public ForumThreadDto getThreadById(Long id) {
        logger.debug("Fetching forum thread by id: {}", id);
        ForumThread thread = forumThreadRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("ForumThread", id));
        return dtoMapper.toForumThreadDto(thread);
    }
    
    public List<ForumThreadDto> getThreadsByCategory(String category) {
        logger.debug("Fetching forum threads by category: {}", category);
        List<ForumThread> threads = forumThreadRepository.findByCategoryOrderByCreatedAtDesc(category);
        return dtoMapper.toForumThreadDtoList(threads);
    }
    
    public List<ForumThreadDto> searchThreads(String keyword) {
        logger.debug("Searching forum threads with keyword: {}", keyword);
        
        // SECURITY: Sanitize search keyword and add pagination to prevent DoS
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        // SECURITY: Escape special characters and limit length
        String sanitizedKeyword = keyword.trim();
        if (sanitizedKeyword.length() > 100) {
            sanitizedKeyword = sanitizedKeyword.substring(0, 100);
        }
        
        // SECURITY: Add % wildcards safely and use pagination
        String searchPattern = "%" + sanitizedKeyword.replace("%", "\\%").replace("_", "\\_") + "%";
        
        org.springframework.data.domain.Pageable pageable = 
            org.springframework.data.domain.PageRequest.of(0, 50); // Limit to 50 results
        
        org.springframework.data.domain.Page<ForumThread> threadsPage = 
            forumThreadRepository.findByTitleContainingOrContentContaining(searchPattern, pageable);
        
        // Java 24 Stream Gatherers: Group threads by category and collect top results
        return threadsPage.getContent().stream()
            .gather(Gatherers.windowFixed(10)) // Process in windows of 10
            .flatMap(window -> window.stream()
                .sorted((t1, t2) -> t2.getUpdatedAt().compareTo(t1.getUpdatedAt()))
                .limit(5) // Top 5 from each window
            )
            .map(dtoMapper::toForumThreadDto)
            .toList();
    }
    
    public ForumThreadDto createThread(String title, String content, Long userId) {
        logger.info("Creating new forum thread: {} by user: {}", title, userId);
        
        User author = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User", userId));
        
        ForumThread thread = new ForumThread(title, author);
        if (content != null && !content.trim().isEmpty()) {
            thread.setContent(content);
        }
        
        ForumThread savedThread = forumThreadRepository.save(thread);
        logger.info("Successfully created forum thread: {}", savedThread.getId());
        
        // Index thread for search
        try {
            searchService.indexThread(savedThread);
        } catch (Exception e) {
            logger.warn("Failed to index thread for search: {}", e.getMessage());
        }
        
        // Broadcast new thread via WebSocket
        ForumThreadDto threadDto = dtoMapper.toForumThreadDto(savedThread);
        try {
            webSocketController.broadcastNewThread(threadDto);
        } catch (Exception e) {
            logger.warn("Failed to broadcast new thread via WebSocket: {}", e.getMessage());
        }
        
        return threadDto;
    }
    
    public ForumThreadDto updateThread(Long id, ForumThreadDto threadDto, String userEmail) {
        logger.info("Updating forum thread: {} by user: {}", id, userEmail);
        
        ForumThread existingThread = forumThreadRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("ForumThread", id));
        
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new EntityNotFoundException("User", "email", userEmail));
        
        // Check if user is the author or admin
        if (!existingThread.getAuthor().getId().equals(user.getId()) && 
            !user.getRoles().contains(User.Role.ADMIN)) {
            throw new ForbiddenException("You can only edit your own threads");
        }
        
        existingThread.setTitle(threadDto.getTitle());
        existingThread.setContent(threadDto.getContent());
        existingThread.setCategory(threadDto.getCategory());
        
        ForumThread savedThread = forumThreadRepository.save(existingThread);
        logger.info("Successfully updated forum thread: {}", savedThread.getId());
        
        return dtoMapper.toForumThreadDto(savedThread);
    }
    
    public void deleteThread(Long id, String userEmail) {
        logger.info("Deleting forum thread: {} by user: {}", id, userEmail);
        
        ForumThread thread = forumThreadRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("ForumThread", id));
        
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new EntityNotFoundException("User", "email", userEmail));
        
        // Check if user is the author or admin
        if (!thread.getAuthor().getId().equals(user.getId()) && 
            !user.getRoles().contains(User.Role.ADMIN)) {
            throw new ForbiddenException("You can only delete your own threads");
        }
        
        forumThreadRepository.delete(thread);
        logger.info("Successfully deleted forum thread: {}", id);
    }
    
    // Post operations
    public Page<ForumPostDto> getThreadPosts(Long threadId, Pageable pageable) {
        logger.debug("Fetching posts for thread: {}", threadId);
        
        // Verify thread exists - throw exception instead of returning null
        if (!forumThreadRepository.existsById(threadId)) {
            throw new EntityNotFoundException("ForumThread", threadId);
        }
        
        Page<ForumPost> posts = forumPostRepository.findByThreadIdOrderByCreatedAtAsc(threadId, pageable);
        return posts.map(dtoMapper::toForumPostDto);
    }
    
    public ForumPostDto createPost(Long threadId, String content, Long userId) {
        logger.info("Creating new forum post in thread: {} by user: {}", threadId, userId);
        
        ForumThread thread = forumThreadRepository.findById(threadId)
            .orElseThrow(() -> new EntityNotFoundException("ForumThread", threadId));
        
        if (thread.getIsLocked()) {
            throw new ForbiddenException("Cannot post to locked thread");
        }
        
        User author = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User", userId));
        
        ForumPost post = new ForumPost(content, author, thread);
        ForumPost savedPost = forumPostRepository.save(post);
        
        logger.info("Successfully created forum post: {}", savedPost.getId());
        
        // Index post for search
        try {
            searchService.indexPost(savedPost);
        } catch (Exception e) {
            logger.warn("Failed to index post for search: {}", e.getMessage());
        }
        
        // Broadcast new post via WebSocket
        ForumPostDto postDto = dtoMapper.toForumPostDto(savedPost);
        try {
            webSocketController.broadcastNewPost(postDto);
        } catch (Exception e) {
            logger.warn("Failed to broadcast new post via WebSocket: {}", e.getMessage());
        }
        
        return postDto;
    }
    
    public ForumPostDto updatePost(Long postId, ForumPostDto postDto, String userEmail) {
        logger.info("Updating forum post: {} by user: {}", postId, userEmail);
        
        ForumPost existingPost = forumPostRepository.findById(postId)
            .orElseThrow(() -> new EntityNotFoundException("ForumPost", postId));
        
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new EntityNotFoundException("User", "email", userEmail));
        
        // Check if user is the author or admin
        if (!existingPost.getAuthor().getId().equals(user.getId()) && 
            !user.getRoles().contains(User.Role.ADMIN)) {
            throw new ForbiddenException("You can only edit your own posts");
        }
        
        existingPost.setContent(postDto.getContent());
        ForumPost savedPost = forumPostRepository.save(existingPost);
        
        logger.info("Successfully updated forum post: {}", savedPost.getId());
        
        return dtoMapper.toForumPostDto(savedPost);
    }
    
    public void deletePost(Long postId, String userEmail) {
        logger.info("Deleting forum post: {} by user: {}", postId, userEmail);
        
        ForumPost post = forumPostRepository.findById(postId)
            .orElseThrow(() -> new EntityNotFoundException("ForumPost", postId));
        
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new EntityNotFoundException("User", "email", userEmail));
        
        // Check if user is the author or admin
        if (!post.getAuthor().getId().equals(user.getId()) && 
            !user.getRoles().contains(User.Role.ADMIN)) {
            throw new ForbiddenException("You can only delete your own posts");
        }
        
        forumPostRepository.delete(post);
        logger.info("Successfully deleted forum post: {}", postId);
    }
}