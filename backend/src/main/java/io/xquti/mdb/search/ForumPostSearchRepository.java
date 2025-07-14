package io.xquti.mdb.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * Elasticsearch repository for forum post search operations.
 */
@Repository
public interface ForumPostSearchRepository extends ElasticsearchRepository<SearchableForumPost, Long> {

    /**
     * Search posts by content.
     */
    Page<SearchableForumPost> findByContentContaining(String content, Pageable pageable);

    /**
     * Search posts by thread ID.
     */
    Page<SearchableForumPost> findByThreadId(Long threadId, Pageable pageable);

    /**
     * Search posts by author.
     */
    Page<SearchableForumPost> findByAuthorUsername(String authorUsername, Pageable pageable);

    /**
     * Search posts by category.
     */
    Page<SearchableForumPost> findByCategory(String category, Pageable pageable);

    /**
     * Search posts by thread title.
     */
    Page<SearchableForumPost> findByThreadTitleContaining(String threadTitle, Pageable pageable);

    /**
     * Search posts by content and category.
     */
    Page<SearchableForumPost> findByContentContainingAndCategory(
            String content, String category, Pageable pageable);
}