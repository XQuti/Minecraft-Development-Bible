package io.xquti.mdb.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tutorial_lessons")
public class TutorialLesson {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String content; // Markdown content
    
    private String type; // "text", "video", "interactive", etc.
    
    @Column(name = "display_order", nullable = false)
    private Integer orderIndex;
    
    @Column(name = "is_published", nullable = false)
    private Boolean isPublished = true;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    private TutorialModule module;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public TutorialLesson() {
        this.createdAt = LocalDateTime.now();
    }
    
    public TutorialLesson(String title, String content, String type, 
                         Integer orderIndex, TutorialModule module) {
        this();
        this.title = title;
        this.content = content;
        this.type = type;
        this.orderIndex = orderIndex;
        this.module = module;
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
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public Integer getOrderIndex() {
        return orderIndex;
    }
    
    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }
    
    public Boolean getIsPublished() {
        return isPublished;
    }
    
    public void setIsPublished(Boolean isPublished) {
        this.isPublished = isPublished;
    }
    
    public TutorialModule getModule() {
        return module;
    }
    
    public void setModule(TutorialModule module) {
        this.module = module;
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
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}