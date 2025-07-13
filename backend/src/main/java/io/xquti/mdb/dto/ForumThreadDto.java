package io.xquti.mdb.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class ForumThreadDto {
    
    private Long id;
    
    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 200, message = "Title must be between 5 and 200 characters")
    private String title;
    
    @NotBlank(message = "Content is required")
    @Size(min = 10, max = 10000, message = "Content must be between 10 and 10000 characters")
    private String content;
    
    private String category;
    private boolean pinned;
    private boolean locked;
    private UserDto author;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int postCount;
    
    // Constructors
    public ForumThreadDto() {}
    
    public ForumThreadDto(Long id, String title, String content, String category, 
                         boolean pinned, boolean locked, UserDto author, 
                         LocalDateTime createdAt, LocalDateTime updatedAt, int postCount) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.category = category;
        this.pinned = pinned;
        this.locked = locked;
        this.author = author;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.postCount = postCount;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public boolean isPinned() {
        return pinned;
    }
    
    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }
    
    public boolean isLocked() {
        return locked;
    }
    
    public void setLocked(boolean locked) {
        this.locked = locked;
    }
    
    public UserDto getAuthor() {
        return author;
    }
    
    public void setAuthor(UserDto author) {
        this.author = author;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public int getPostCount() {
        return postCount;
    }
    
    public void setPostCount(int postCount) {
        this.postCount = postCount;
    }
}