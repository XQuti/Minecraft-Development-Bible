package io.xquti.mdb.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.xquti.mdb.dto.ForumPostDto;
import io.xquti.mdb.dto.ForumThreadDto;
import io.xquti.mdb.dto.UserDto;
import io.xquti.mdb.service.ForumService;
import io.xquti.mdb.util.AuthUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/forums")
@Tag(name = "Forum", description = "Forum management API for threads and posts")
public class ForumController {

    private static final Logger logger = LoggerFactory.getLogger(ForumController.class);

    @Autowired
    private ForumService forumService;
    
    @Autowired
    private AuthUtils authUtils;

    @GetMapping("/threads")
    @Operation(summary = "Get all forum threads with pagination and optional category filtering")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved forum threads"),
        @ApiResponse(responseCode = "400", description = "Invalid pagination parameters")
    })
    public ResponseEntity<Page<ForumThreadDto>> getAllThreads(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of threads per page") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Filter threads by category") @RequestParam(required = false) String category) {
        
        logger.debug("Getting forum threads - page: {}, size: {}, category: {}", page, size, category);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ForumThreadDto> threads = forumService.getAllThreads(pageable, category);
        
        logger.info("Retrieved {} forum threads", threads.getNumberOfElements());
        return ResponseEntity.ok(threads);
    }

    @PostMapping("/threads")
    @Operation(summary = "Create a new forum thread")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Thread created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    public ResponseEntity<ForumThreadDto> createThread(
            @Parameter(description = "Thread creation request") @Valid @RequestBody CreateThreadRequest request,
            @Parameter(description = "JWT authorization header") @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        logger.debug("Creating new forum thread: {}", request.getTitle());
        
        UserDto user = authUtils.getCurrentUser(authHeader);
        if (user == null) {
            logger.warn("Unauthorized attempt to create forum thread");
            return ResponseEntity.status(401).build();
        }
        
        ForumThreadDto thread = forumService.createThread(request.getTitle(), request.getContent(), user.id());
        
        logger.info("Created forum thread with ID: {} by user: {}", thread.getId(), user.email());
        return ResponseEntity.ok(thread);
    }

    @GetMapping("/threads/{threadId}")
    public ResponseEntity<ForumThreadDto> getThread(@PathVariable Long threadId) {
        logger.debug("Getting forum thread: {}", threadId);
        
        ForumThreadDto thread = forumService.getThreadById(threadId);
        if (thread == null) {
            logger.warn("Forum thread not found: {}", threadId);
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(thread);
    }

    @GetMapping("/threads/{threadId}/posts")
    @Operation(summary = "Get all posts in a specific forum thread")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved thread posts"),
        @ApiResponse(responseCode = "404", description = "Thread not found")
    })
    public ResponseEntity<Page<ForumPostDto>> getThreadPosts(
            @Parameter(description = "Thread ID") @PathVariable Long threadId,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of posts per page") @RequestParam(defaultValue = "20") int size) {
        
        logger.debug("Getting posts for thread: {} - page: {}, size: {}", threadId, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ForumPostDto> posts = forumService.getThreadPosts(threadId, pageable);
        
        if (posts == null) {
            logger.warn("Thread not found: {}", threadId);
            return ResponseEntity.notFound().build();
        }
        
        logger.info("Retrieved {} posts for thread: {}", posts.getNumberOfElements(), threadId);
        return ResponseEntity.ok(posts);
    }

    @PostMapping("/threads/{threadId}/posts")
    @Operation(summary = "Create a new post in a forum thread")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Post created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data or thread not found"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "404", description = "Thread not found")
    })
    public ResponseEntity<ForumPostDto> createPost(
            @Parameter(description = "Thread ID") @PathVariable Long threadId,
            @Parameter(description = "Post creation request") @Valid @RequestBody CreatePostRequest request,
            @Parameter(description = "JWT authorization header") @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        logger.debug("Creating post in thread: {}", threadId);
        
        UserDto user = authUtils.getCurrentUser(authHeader);
        if (user == null) {
            logger.warn("Unauthorized attempt to create forum post");
            return ResponseEntity.status(401).build();
        }
        
        ForumPostDto post = forumService.createPost(threadId, request.getContent(), user.id());
        if (post == null) {
            logger.warn("Failed to create post - thread not found or locked: {}", threadId);
            return ResponseEntity.badRequest().build();
        }
        
        logger.info("Created forum post in thread: {} by user: {}", threadId, user.email());
        return ResponseEntity.ok(post);
    }



    // Request DTOs
    public static class CreateThreadRequest {
        @NotBlank(message = "Title is required")
        @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
        private String title;
        
        @Size(max = 10000, message = "Content must not exceed 10000 characters")
        private String content;

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }

    public static class CreatePostRequest {
        @NotBlank(message = "Content is required")
        @Size(min = 1, max = 10000, message = "Content must be between 1 and 10000 characters")
        private String content;

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
}