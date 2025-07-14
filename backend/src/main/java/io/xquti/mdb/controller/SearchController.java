package io.xquti.mdb.controller;

import io.xquti.mdb.search.SearchService;
import io.xquti.mdb.search.SearchableForumThread;
import io.xquti.mdb.search.SearchableForumPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for search operations.
 */
@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
public class SearchController {

    private final SearchService searchService;

    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    /**
     * Search forum threads.
     */
    @GetMapping("/threads")
    public ResponseEntity<Page<SearchableForumThread>> searchThreads(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String author,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<SearchableForumThread> results;
        
        if ((query != null && !query.trim().isEmpty()) || 
            (category != null && !category.trim().isEmpty()) || 
            (author != null && !author.trim().isEmpty())) {
            results = searchService.advancedThreadSearch(query, category, author, pageable);
        } else {
            results = searchService.searchThreads("", pageable);
        }

        return ResponseEntity.ok(results);
    }

    /**
     * Search forum posts.
     */
    @GetMapping("/posts")
    public ResponseEntity<Page<SearchableForumPost>> searchPosts(
            @RequestParam String query,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<SearchableForumPost> results;
        
        if (category != null && !category.trim().isEmpty()) {
            results = searchService.searchPostsByCategory(category, pageable);
        } else {
            results = searchService.searchPosts(query, pageable);
        }

        return ResponseEntity.ok(results);
    }

    /**
     * Search threads by category.
     */
    @GetMapping("/threads/category/{category}")
    public ResponseEntity<Page<SearchableForumThread>> searchThreadsByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<SearchableForumThread> results = searchService.searchThreadsByCategory(category, pageable);
        
        return ResponseEntity.ok(results);
    }

    /**
     * Search threads by author.
     */
    @GetMapping("/threads/author/{author}")
    public ResponseEntity<Page<SearchableForumThread>> searchThreadsByAuthor(
            @PathVariable String author,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<SearchableForumThread> results = searchService.searchThreadsByAuthor(author, pageable);
        
        return ResponseEntity.ok(results);
    }
}