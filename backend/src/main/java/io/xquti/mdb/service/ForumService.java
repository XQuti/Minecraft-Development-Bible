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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        List<ForumThread> threads = forumThreadRepository.findByTitleContainingOrContentContaining(keyword);
        return dtoMapper.toForumThreadDtoList(threads);
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
        
        return dtoMapper.toForumThreadDto(savedThread);
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
        
        // Verify thread exists
        if (!forumThreadRepository.existsById(threadId)) {
            return null; // Return null to indicate thread not found
        }
        
        Page<ForumPost> posts = forumPostRepository.findByThreadIdOrderByCreatedAtAsc(threadId, pageable);
        return posts.map(dtoMapper::toForumPostDto);
    }
    
    public ForumPostDto createPost(Long threadId, String content, Long userId) {
        logger.info("Creating new forum post in thread: {} by user: {}", threadId, userId);
        
        ForumThread thread = forumThreadRepository.findById(threadId)
            .orElse(null);
        
        if (thread == null) {
            return null; // Return null to indicate thread not found
        }
        
        if (thread.getIsLocked()) {
            return null; // Return null to indicate thread is locked
        }
        
        User author = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User", userId));
        
        ForumPost post = new ForumPost(content, author, thread);
        ForumPost savedPost = forumPostRepository.save(post);
        
        logger.info("Successfully created forum post: {}", savedPost.getId());
        
        return dtoMapper.toForumPostDto(savedPost);
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