package io.xquti.mdb.repository;

import io.xquti.mdb.model.ForumThread;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ForumThreadRepository extends JpaRepository<ForumThread, Long> {
    
    @Query("SELECT t FROM ForumThread t ORDER BY t.isPinned DESC, t.updatedAt DESC, t.createdAt DESC")
    Page<ForumThread> findAllOrderByPinnedAndUpdated(Pageable pageable);
    
    @Query("SELECT t FROM ForumThread t WHERE t.category = :category ORDER BY t.isPinned DESC, t.updatedAt DESC, t.createdAt DESC")
    Page<ForumThread> findByCategoryOrderByPinnedAndUpdated(@Param("category") String category, Pageable pageable);
    
    List<ForumThread> findByCategoryOrderByCreatedAtDesc(String category);
    
    @Query("SELECT t FROM ForumThread t WHERE t.title LIKE %:keyword% OR t.content LIKE %:keyword%")
    List<ForumThread> findByTitleContainingOrContentContaining(@Param("keyword") String keyword);
}