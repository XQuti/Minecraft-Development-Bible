package io.xquti.mdb.search;

import io.xquti.mdb.model.ForumThread;
import io.xquti.mdb.model.ForumPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service for search operations using Elasticsearch.
 */
@Service
public class SearchService {

    private final ForumSearchRepository forumSearchRepository;
    private final ForumPostSearchRepository forumPostSearchRepository;

    @Autowired
    public SearchService(ForumSearchRepository forumSearchRepository,
                        ForumPostSearchRepository forumPostSearchRepository) {
        this.forumSearchRepository = forumSearchRepository;
        this.forumPostSearchRepository = forumPostSearchRepository;
    }

    /**
     * Search forum threads by query text.
     */
    public Page<SearchableForumThread> searchThreads(String query, Pageable pageable) {
        return forumSearchRepository.findByTitleContainingOrContentContaining(query, query, pageable);
    }

    /**
     * Search forum threads by category.
     */
    public Page<SearchableForumThread> searchThreadsByCategory(String category, Pageable pageable) {
        return forumSearchRepository.findByCategory(category, pageable);
    }

    /**
     * Search forum threads by author.
     */
    public Page<SearchableForumThread> searchThreadsByAuthor(String author, Pageable pageable) {
        return forumSearchRepository.findByAuthorUsername(author, pageable);
    }

    /**
     * Search forum posts by content.
     */
    public Page<SearchableForumPost> searchPosts(String query, Pageable pageable) {
        return forumPostSearchRepository.findByContentContaining(query, pageable);
    }

    /**
     * Search forum posts by category.
     */
    public Page<SearchableForumPost> searchPostsByCategory(String category, Pageable pageable) {
        return forumPostSearchRepository.findByCategory(category, pageable);
    }

    /**
     * Index a forum thread for search.
     */
    public void indexThread(ForumThread thread) {
        SearchableForumThread searchableThread = new SearchableForumThread(
            thread.getId(),
            thread.getTitle(),
            thread.getContent(),
            thread.getCategory(),
            thread.getAuthor().getUsername(),
            thread.getCreatedAt(),
            thread.getUpdatedAt(),
            thread.getPosts() != null ? thread.getPosts().size() : 0,
            thread.getIsPinned(),
            thread.getIsLocked(),
            new String[]{} // Tags can be added later
        );
        forumSearchRepository.save(searchableThread);
    }

    /**
     * Index a forum post for search.
     */
    public void indexPost(ForumPost post) {
        SearchableForumPost searchablePost = new SearchableForumPost(
            post.getId(),
            post.getContent(),
            post.getThread().getId(),
            post.getThread().getTitle(),
            post.getAuthor().getUsername(),
            post.getCreatedAt(),
            post.getUpdatedAt(),
            post.getThread().getCategory()
        );
        forumPostSearchRepository.save(searchablePost);
    }

    /**
     * Remove thread from search index.
     */
    public void removeThreadFromIndex(Long threadId) {
        forumSearchRepository.deleteById(threadId);
    }

    /**
     * Remove post from search index.
     */
    public void removePostFromIndex(Long postId) {
        forumPostSearchRepository.deleteById(postId);
    }

    /**
     * Advanced search with multiple criteria.
     */
    public Page<SearchableForumThread> advancedThreadSearch(String query, String category, 
                                                           String author, Pageable pageable) {
        if (category != null && !category.isEmpty()) {
            if (query != null && !query.isEmpty()) {
                return forumSearchRepository.findByCategoryAndTitleContainingOrCategoryAndContentContaining(
                    category, query, category, query, pageable);
            } else {
                return forumSearchRepository.findByCategory(category, pageable);
            }
        } else if (author != null && !author.isEmpty()) {
            return forumSearchRepository.findByAuthorUsername(author, pageable);
        } else if (query != null && !query.isEmpty()) {
            return forumSearchRepository.findByTitleContainingOrContentContaining(query, query, pageable);
        } else {
            return forumSearchRepository.findAll(pageable);
        }
    }
}