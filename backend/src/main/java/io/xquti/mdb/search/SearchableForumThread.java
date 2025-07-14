package io.xquti.mdb.search;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

/**
 * Elasticsearch document for forum thread search functionality.
 */
@Document(indexName = "forum_threads")
public class SearchableForumThread {

    @Id
    private Long id;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String title;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String content;

    @Field(type = FieldType.Keyword)
    private String category;

    @Field(type = FieldType.Keyword)
    private String authorUsername;

    @Field(type = FieldType.Date)
    private LocalDateTime createdAt;

    @Field(type = FieldType.Date)
    private LocalDateTime updatedAt;

    @Field(type = FieldType.Integer)
    private Integer postCount;

    @Field(type = FieldType.Boolean)
    private Boolean isPinned;

    @Field(type = FieldType.Boolean)
    private Boolean isLocked;

    @Field(type = FieldType.Keyword)
    private String[] tags;

    // Constructors
    public SearchableForumThread() {}

    public SearchableForumThread(Long id, String title, String content, String category, 
                               String authorUsername, LocalDateTime createdAt, LocalDateTime updatedAt,
                               Integer postCount, Boolean isPinned, Boolean isLocked, String[] tags) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.category = category;
        this.authorUsername = authorUsername;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.postCount = postCount;
        this.isPinned = isPinned;
        this.isLocked = isLocked;
        this.tags = tags;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getAuthorUsername() { return authorUsername; }
    public void setAuthorUsername(String authorUsername) { this.authorUsername = authorUsername; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Integer getPostCount() { return postCount; }
    public void setPostCount(Integer postCount) { this.postCount = postCount; }

    public Boolean getIsPinned() { return isPinned; }
    public void setIsPinned(Boolean isPinned) { this.isPinned = isPinned; }

    public Boolean getIsLocked() { return isLocked; }
    public void setIsLocked(Boolean isLocked) { this.isLocked = isLocked; }

    public String[] getTags() { return tags; }
    public void setTags(String[] tags) { this.tags = tags; }
}