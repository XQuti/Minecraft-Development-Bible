package io.xquti.mdb.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * Elasticsearch repository for forum thread search operations.
 */
@Repository
public interface ForumSearchRepository extends ElasticsearchRepository<SearchableForumThread, Long> {

    /**
     * Search threads by title and content.
     */
    Page<SearchableForumThread> findByTitleContainingOrContentContaining(
            String title, String content, Pageable pageable);

    /**
     * Search threads by category.
     */
    Page<SearchableForumThread> findByCategory(String category, Pageable pageable);

    /**
     * Search threads by author.
     */
    Page<SearchableForumThread> findByAuthorUsername(String authorUsername, Pageable pageable);

    /**
     * Search threads by title containing text.
     */
    Page<SearchableForumThread> findByTitleContaining(String title, Pageable pageable);

    /**
     * Search threads by content containing text.
     */
    Page<SearchableForumThread> findByContentContaining(String content, Pageable pageable);

    /**
     * Find pinned threads.
     */
    Page<SearchableForumThread> findByIsPinnedTrue(Pageable pageable);

    /**
     * Find threads by category and search text.
     */
    Page<SearchableForumThread> findByCategoryAndTitleContainingOrCategoryAndContentContaining(
            String category1, String title, String category2, String content, Pageable pageable);
}