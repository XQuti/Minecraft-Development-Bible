package io.xquti.mdb.service;

import io.xquti.mdb.dto.TutorialLessonDto;
import io.xquti.mdb.dto.TutorialModuleDto;
import io.xquti.mdb.exception.EntityNotFoundException;
import io.xquti.mdb.model.TutorialLesson;
import io.xquti.mdb.model.TutorialModule;
import io.xquti.mdb.repository.TutorialLessonRepository;
import io.xquti.mdb.repository.TutorialModuleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TutorialService {
    
    private static final Logger logger = LoggerFactory.getLogger(TutorialService.class);
    
    @Autowired
    private TutorialModuleRepository tutorialModuleRepository;
    
    @Autowired
    private TutorialLessonRepository tutorialLessonRepository;
    
    @Autowired
    private DtoMapper dtoMapper;
    
    // Module operations
    public List<TutorialModuleDto> getAllModules() {
        logger.debug("Fetching all tutorial modules");
        List<TutorialModule> modules = tutorialModuleRepository.findAllByOrderByOrderIndexAsc();
        return dtoMapper.toTutorialModuleDtoList(modules);
    }
    
    public List<TutorialModuleDto> getAllPublishedModules() {
        logger.debug("Fetching published tutorial modules");
        List<TutorialModule> modules = tutorialModuleRepository.findByIsPublishedTrueOrderByOrderIndexAsc();
        return dtoMapper.toTutorialModuleDtoList(modules);
    }
    
    public List<TutorialModuleDto> getModulesByCategory(String category) {
        logger.debug("Fetching tutorial modules by category: {}", category);
        List<TutorialModule> modules = tutorialModuleRepository.findByCategoryOrderByOrderIndexAsc(category);
        return dtoMapper.toTutorialModuleDtoList(modules);
    }
    
    public TutorialModuleDto getModuleById(Long id) {
        logger.debug("Fetching tutorial module by id: {}", id);
        TutorialModule module = tutorialModuleRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("TutorialModule", id));
        return dtoMapper.toTutorialModuleDto(module);
    }
    
    public TutorialModuleDto getPublishedModuleById(Long id) {
        logger.debug("Fetching published tutorial module by id: {}", id);
        TutorialModule module = tutorialModuleRepository.findById(id)
            .filter(TutorialModule::getIsPublished)
            .orElse(null);
        return module != null ? dtoMapper.toTutorialModuleDto(module) : null;
    }
    
    public TutorialModuleDto createModule(TutorialModuleDto moduleDto) {
        logger.info("Creating new tutorial module: {}", moduleDto.getTitle());
        
        TutorialModule module = new TutorialModule(
            moduleDto.getTitle(),
            moduleDto.getDescription(),
            moduleDto.getCategory(),
            moduleDto.getDifficulty(),
            moduleDto.getOrderIndex()
        );
        
        module.setIsPublished(moduleDto.isPublished());
        
        TutorialModule savedModule = tutorialModuleRepository.save(module);
        logger.info("Successfully created tutorial module: {}", savedModule.getId());
        
        return dtoMapper.toTutorialModuleDto(savedModule);
    }
    
    public TutorialModuleDto updateModule(Long id, TutorialModuleDto moduleDto) {
        logger.info("Updating tutorial module: {}", id);
        
        TutorialModule existingModule = tutorialModuleRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("TutorialModule", id));
        
        existingModule.setTitle(moduleDto.getTitle());
        existingModule.setDescription(moduleDto.getDescription());
        existingModule.setCategory(moduleDto.getCategory());
        existingModule.setDifficulty(moduleDto.getDifficulty());
        existingModule.setOrderIndex(moduleDto.getOrderIndex());
        existingModule.setIsPublished(moduleDto.isPublished());
        
        TutorialModule savedModule = tutorialModuleRepository.save(existingModule);
        logger.info("Successfully updated tutorial module: {}", savedModule.getId());
        
        return dtoMapper.toTutorialModuleDto(savedModule);
    }
    
    public void deleteModule(Long id) {
        logger.info("Deleting tutorial module: {}", id);
        
        if (!tutorialModuleRepository.existsById(id)) {
            throw new EntityNotFoundException("TutorialModule", id);
        }
        
        tutorialModuleRepository.deleteById(id);
        logger.info("Successfully deleted tutorial module: {}", id);
    }
    
    // Lesson operations
    public List<TutorialLessonDto> getLessonsByModuleId(Long moduleId) {
        logger.debug("Fetching lessons for module: {}", moduleId);
        
        // Verify module exists
        if (!tutorialModuleRepository.existsById(moduleId)) {
            throw new EntityNotFoundException("TutorialModule", moduleId);
        }
        
        List<TutorialLesson> lessons = tutorialLessonRepository.findByModuleIdOrderByOrderIndexAsc(moduleId);
        return dtoMapper.toTutorialLessonDtoList(lessons);
    }
    
    public List<TutorialLessonDto> getPublishedLessonsByModule(Long moduleId) {
        logger.debug("Fetching published lessons for module: {}", moduleId);
        
        // Verify module exists and is published
        TutorialModule module = tutorialModuleRepository.findById(moduleId)
            .filter(TutorialModule::getIsPublished)
            .orElse(null);
        
        if (module == null) {
            return null; // Return null to indicate module not found or not published
        }
        
        List<TutorialLesson> lessons = tutorialLessonRepository.findByModuleIdAndIsPublishedTrueOrderByOrderIndexAsc(moduleId);
        return dtoMapper.toTutorialLessonDtoList(lessons);
    }
    
    public TutorialLessonDto getLessonById(Long id) {
        logger.debug("Fetching tutorial lesson by id: {}", id);
        TutorialLesson lesson = tutorialLessonRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("TutorialLesson", id));
        return dtoMapper.toTutorialLessonDto(lesson);
    }
    
    public TutorialLessonDto getPublishedLessonById(Long id) {
        logger.debug("Fetching published tutorial lesson by id: {}", id);
        TutorialLesson lesson = tutorialLessonRepository.findById(id)
            .filter(TutorialLesson::getIsPublished)
            .orElse(null);
        return lesson != null ? dtoMapper.toTutorialLessonDto(lesson) : null;
    }
    
    public TutorialLessonDto createLesson(Long moduleId, TutorialLessonDto lessonDto) {
        logger.info("Creating new tutorial lesson: {} for module: {}", lessonDto.getTitle(), moduleId);
        
        TutorialModule module = tutorialModuleRepository.findById(moduleId)
            .orElseThrow(() -> new EntityNotFoundException("TutorialModule", moduleId));
        
        TutorialLesson lesson = new TutorialLesson(
            lessonDto.getTitle(),
            lessonDto.getContent(),
            lessonDto.getType(),
            lessonDto.getOrderIndex(),
            module
        );
        
        lesson.setIsPublished(lessonDto.isPublished());
        
        TutorialLesson savedLesson = tutorialLessonRepository.save(lesson);
        logger.info("Successfully created tutorial lesson: {}", savedLesson.getId());
        
        return dtoMapper.toTutorialLessonDto(savedLesson);
    }
    
    public TutorialLessonDto updateLesson(Long id, TutorialLessonDto lessonDto) {
        logger.info("Updating tutorial lesson: {}", id);
        
        TutorialLesson existingLesson = tutorialLessonRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("TutorialLesson", id));
        
        existingLesson.setTitle(lessonDto.getTitle());
        existingLesson.setContent(lessonDto.getContent());
        existingLesson.setType(lessonDto.getType());
        existingLesson.setOrderIndex(lessonDto.getOrderIndex());
        existingLesson.setIsPublished(lessonDto.isPublished());
        
        TutorialLesson savedLesson = tutorialLessonRepository.save(existingLesson);
        logger.info("Successfully updated tutorial lesson: {}", savedLesson.getId());
        
        return dtoMapper.toTutorialLessonDto(savedLesson);
    }
    
    public void deleteLesson(Long id) {
        logger.info("Deleting tutorial lesson: {}", id);
        
        if (!tutorialLessonRepository.existsById(id)) {
            throw new EntityNotFoundException("TutorialLesson", id);
        }
        
        tutorialLessonRepository.deleteById(id);
        logger.info("Successfully deleted tutorial lesson: {}", id);
    }
}