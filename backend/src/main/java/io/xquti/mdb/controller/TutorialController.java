package io.xquti.mdb.controller;

import io.xquti.mdb.dto.TutorialLessonDto;
import io.xquti.mdb.dto.TutorialModuleDto;
import io.xquti.mdb.service.TutorialService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tutorials")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
public class TutorialController {

    private static final Logger logger = LoggerFactory.getLogger(TutorialController.class);

    @Autowired
    private TutorialService tutorialService;

    @GetMapping("/modules")
    public ResponseEntity<List<TutorialModuleDto>> getAllModules() {
        logger.debug("Getting all published tutorial modules");
        
        List<TutorialModuleDto> modules = tutorialService.getAllPublishedModules();
        
        logger.info("Retrieved {} published tutorial modules", modules.size());
        return ResponseEntity.ok(modules);
    }

    @GetMapping("/modules/{moduleId}")
    public ResponseEntity<TutorialModuleDto> getModule(@PathVariable Long moduleId) {
        logger.debug("Getting tutorial module: {}", moduleId);
        
        TutorialModuleDto module = tutorialService.getPublishedModuleById(moduleId);
        if (module == null) {
            logger.warn("Tutorial module not found or not published: {}", moduleId);
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(module);
    }

    @GetMapping("/modules/{moduleId}/lessons")
    public ResponseEntity<List<TutorialLessonDto>> getModuleLessons(@PathVariable Long moduleId) {
        logger.debug("Getting lessons for module: {}", moduleId);
        
        List<TutorialLessonDto> lessons = tutorialService.getPublishedLessonsByModule(moduleId);
        if (lessons == null) {
            logger.warn("Tutorial module not found or not published: {}", moduleId);
            return ResponseEntity.notFound().build();
        }
        
        logger.info("Retrieved {} lessons for module: {}", lessons.size(), moduleId);
        return ResponseEntity.ok(lessons);
    }

    @GetMapping("/lessons/{lessonId}")
    public ResponseEntity<TutorialLessonDto> getLesson(@PathVariable Long lessonId) {
        logger.debug("Getting tutorial lesson: {}", lessonId);
        
        TutorialLessonDto lesson = tutorialService.getPublishedLessonById(lessonId);
        if (lesson == null) {
            logger.warn("Tutorial lesson not found or not published: {}", lessonId);
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(lesson);
    }
}