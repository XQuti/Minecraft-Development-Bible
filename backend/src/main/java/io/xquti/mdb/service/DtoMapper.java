package io.xquti.mdb.service;

import io.xquti.mdb.dto.*;
import io.xquti.mdb.model.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DtoMapper {
    
    // User mappings
    public UserDto toUserDto(User user) {
        if (user == null) return null;
        
        return new UserDto(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getAvatarUrl(),
            user.getProvider(),
            user.getRoles(),
            user.getCreatedAt()
        );
    }
    
    public User toUserEntity(UserDto userDto) {
        if (userDto == null) return null;
        
        User user = new User();
        user.setId(userDto.id());
        user.setUsername(userDto.username());
        user.setEmail(userDto.email());
        user.setAvatarUrl(userDto.avatarUrl());
        user.setProvider(userDto.provider());
        user.setRoles(userDto.roles());
        user.setCreatedAt(userDto.createdAt());
        return user;
    }
    
    // ForumThread mappings
    public ForumThreadDto toForumThreadDto(ForumThread thread) {
        if (thread == null) return null;
        
        return new ForumThreadDto(
            thread.getId(),
            thread.getTitle(),
            thread.getContent(),
            thread.getCategory(),
            thread.getIsPinned(),
            thread.getIsLocked(),
            toUserDto(thread.getAuthor()),
            thread.getCreatedAt(),
            thread.getUpdatedAt(),
            thread.getPosts() != null ? thread.getPosts().size() : 0
        );
    }
    
    public ForumThread toForumThreadEntity(ForumThreadDto threadDto) {
        if (threadDto == null) return null;
        
        ForumThread thread = new ForumThread();
        thread.setId(threadDto.getId());
        thread.setTitle(threadDto.getTitle());
        thread.setContent(threadDto.getContent());
        thread.setCategory(threadDto.getCategory());
        thread.setIsPinned(threadDto.isPinned());
        thread.setIsLocked(threadDto.isLocked());
        thread.setCreatedAt(threadDto.getCreatedAt());
        thread.setUpdatedAt(threadDto.getUpdatedAt());
        return thread;
    }
    
    // ForumPost mappings
    public ForumPostDto toForumPostDto(ForumPost post) {
        if (post == null) return null;
        
        return new ForumPostDto(
            post.getId(),
            post.getContent(),
            toUserDto(post.getAuthor()),
            post.getThread() != null ? post.getThread().getId() : null,
            post.getCreatedAt(),
            post.getUpdatedAt()
        );
    }
    
    public ForumPost toForumPostEntity(ForumPostDto postDto) {
        if (postDto == null) return null;
        
        ForumPost post = new ForumPost();
        post.setId(postDto.getId());
        post.setContent(postDto.getContent());
        post.setCreatedAt(postDto.getCreatedAt());
        post.setUpdatedAt(postDto.getUpdatedAt());
        return post;
    }
    
    // TutorialModule mappings
    public TutorialModuleDto toTutorialModuleDto(TutorialModule module) {
        if (module == null) return null;
        
        List<TutorialLessonDto> lessonDtos = module.getLessons() != null 
            ? module.getLessons().stream()
                .map(this::toTutorialLessonDto)
                .collect(Collectors.toList())
            : null;
        
        return new TutorialModuleDto(
            module.getId(),
            module.getTitle(),
            module.getDescription(),
            module.getCategory(),
            module.getDifficulty(),
            module.getOrderIndex(),
            module.getIsPublished(),
            lessonDtos,
            module.getCreatedAt(),
            module.getUpdatedAt()
        );
    }
    
    public TutorialModule toTutorialModuleEntity(TutorialModuleDto moduleDto) {
        if (moduleDto == null) return null;
        
        TutorialModule module = new TutorialModule();
        module.setId(moduleDto.getId());
        module.setTitle(moduleDto.getTitle());
        module.setDescription(moduleDto.getDescription());
        module.setCategory(moduleDto.getCategory());
        module.setDifficulty(moduleDto.getDifficulty());
        module.setOrderIndex(moduleDto.getOrderIndex());
        module.setIsPublished(moduleDto.isPublished());
        module.setCreatedAt(moduleDto.getCreatedAt());
        module.setUpdatedAt(moduleDto.getUpdatedAt());
        return module;
    }
    
    // TutorialLesson mappings
    public TutorialLessonDto toTutorialLessonDto(TutorialLesson lesson) {
        if (lesson == null) return null;
        
        return new TutorialLessonDto(
            lesson.getId(),
            lesson.getTitle(),
            lesson.getContent(),
            lesson.getType(),
            lesson.getOrderIndex(),
            lesson.getIsPublished(),
            lesson.getModule() != null ? lesson.getModule().getId() : null,
            lesson.getCreatedAt(),
            lesson.getUpdatedAt()
        );
    }
    
    public TutorialLesson toTutorialLessonEntity(TutorialLessonDto lessonDto) {
        if (lessonDto == null) return null;
        
        TutorialLesson lesson = new TutorialLesson();
        lesson.setId(lessonDto.getId());
        lesson.setTitle(lessonDto.getTitle());
        lesson.setContent(lessonDto.getContent());
        lesson.setType(lessonDto.getType());
        lesson.setOrderIndex(lessonDto.getOrderIndex());
        lesson.setIsPublished(lessonDto.isPublished());
        lesson.setCreatedAt(lessonDto.getCreatedAt());
        lesson.setUpdatedAt(lessonDto.getUpdatedAt());
        return lesson;
    }
    
    // List mappings
    public List<UserDto> toUserDtoList(List<User> users) {
        return users.stream().map(this::toUserDto).collect(Collectors.toList());
    }
    
    public List<ForumThreadDto> toForumThreadDtoList(List<ForumThread> threads) {
        return threads.stream().map(this::toForumThreadDto).collect(Collectors.toList());
    }
    
    public List<ForumPostDto> toForumPostDtoList(List<ForumPost> posts) {
        return posts.stream().map(this::toForumPostDto).collect(Collectors.toList());
    }
    
    public List<TutorialModuleDto> toTutorialModuleDtoList(List<TutorialModule> modules) {
        return modules.stream().map(this::toTutorialModuleDto).collect(Collectors.toList());
    }
    
    public List<TutorialLessonDto> toTutorialLessonDtoList(List<TutorialLesson> lessons) {
        return lessons.stream().map(this::toTutorialLessonDto).collect(Collectors.toList());
    }
}