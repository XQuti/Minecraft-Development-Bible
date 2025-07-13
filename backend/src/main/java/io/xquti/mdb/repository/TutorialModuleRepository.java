package io.xquti.mdb.repository;

import io.xquti.mdb.model.TutorialModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TutorialModuleRepository extends JpaRepository<TutorialModule, Long> {
    
    List<TutorialModule> findByIsPublishedTrueOrderByOrderIndexAsc();
    
    List<TutorialModule> findAllByOrderByOrderIndexAsc();
    
    List<TutorialModule> findByCategoryOrderByOrderIndexAsc(String category);
}