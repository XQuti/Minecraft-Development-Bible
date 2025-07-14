package io.xquti.mdb.search;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

/**
 * Elasticsearch document for forum post search functionality.
 */
@Document(indexName = "forum_posts")
public class SearchableForumPost {

    @Id
    private Long id;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String content;

    @Field(type = FieldType.Long)
    private Long threadId;

    @Field(type = FieldType.Text)
    private String threadTitle;

    @Field(type = FieldType.Keyword)
    private String authorUsername;

    @Field(type = FieldType.Date)
    private LocalDateTime createdAt;

    @Field(type = FieldType.Date)
    private LocalDateTime updatedAt;

    @Field(type = FieldType.Keyword)
    private String category;

    // Constructors
    public SearchableForumPost() {}

    public SearchableForumPost(Long id, String content, Long threadId, String threadTitle,
                             String authorUsername, LocalDateTime createdAt, LocalDateTime updatedAt,
                             String category) {
        this.id = id;
        this.content = content;
        this.threadId = threadId;
        this.threadTitle = threadTitle;
        this.authorUsername = authorUsername;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.category = category;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Long getThreadId() { return threadId; }
    public void setThreadId(Long threadId) { this.threadId = threadId; }

    public String getThreadTitle() { return threadTitle; }
    public void setThreadTitle(String threadTitle) { this.threadTitle = threadTitle; }

    public String getAuthorUsername() { return authorUsername; }
    public void setAuthorUsername(String authorUsername) { this.authorUsername = authorUsername; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}