package io.xquti.mdb.repository;

import io.xquti.mdb.model.TutorialLesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TutorialLessonRepository extends JpaRepository<TutorialLesson, Long> {
    
    List<TutorialLesson> findByModuleIdAndIsPublishedTrueOrderByOrderIndexAsc(Long moduleId);
    
    List<TutorialLesson> findByModuleIdOrderByOrderIndexAsc(Long moduleId);
}