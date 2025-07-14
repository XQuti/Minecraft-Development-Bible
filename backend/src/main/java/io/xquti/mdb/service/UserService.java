package io.xquti.mdb.service;

import io.xquti.mdb.dto.UserDto;
import io.xquti.mdb.exception.EntityNotFoundException;
import io.xquti.mdb.model.User;
import io.xquti.mdb.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private DtoMapper dtoMapper;
    
    public Optional<User> findByEmail(String email) {
        logger.debug("Finding user by email: {}", email);
        return userRepository.findByEmail(email);
    }
    
    public Optional<User> findByUsername(String username) {
        logger.debug("Finding user by username: {}", username);
        return userRepository.findByUsername(username);
    }
    
    public Optional<User> findByProviderAndProviderId(String provider, String providerId) {
        logger.debug("Finding user by provider: {} and providerId: {}", provider, providerId);
        return userRepository.findByProviderAndProviderId(provider, providerId);
    }
    
    public UserDto findUserDtoById(Long id) {
        logger.debug("Finding user DTO by id: {}", id);
        User user = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User", id));
        return dtoMapper.toUserDto(user);
    }
    
    public List<UserDto> findAllUsers() {
        logger.debug("Finding all users");
        List<User> users = userRepository.findAll();
        return dtoMapper.toUserDtoList(users);
    }
    
    public User save(User user) {
        logger.debug("Saving user: {}", user.getEmail());
        return userRepository.save(user);
    }
    
    public User createOrUpdateUser(String email, String username, String provider, 
                                  String providerId, String avatarUrl) {
        logger.info("Creating or updating user - email: {}, provider: {}", email, provider);
        
        Optional<User> existingUser = findByProviderAndProviderId(provider, providerId);
        
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.setEmail(email);
            user.setUsername(username);
            user.setAvatarUrl(avatarUrl);
            User savedUser = save(user);
            logger.info("Updated existing user: {}", savedUser.getId());
            return savedUser;
        } else {
            User newUser = new User(username, email, provider, providerId);
            newUser.setAvatarUrl(avatarUrl);
            User savedUser = save(newUser);
            logger.info("Created new user: {}", savedUser.getId());
            return savedUser;
        }
    }
    
    public UserDto updateUser(Long id, UserDto userDto) {
        logger.info("Updating user: {}", id);
        
        User existingUser = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User", id));
        
        // Update allowed fields
        existingUser.setUsername(userDto.username());
        existingUser.setEmail(userDto.email());
        existingUser.setAvatarUrl(userDto.avatarUrl());
        
        User savedUser = save(existingUser);
        logger.info("Successfully updated user: {}", savedUser.getId());
        
        return dtoMapper.toUserDto(savedUser);
    }
    
    public void deleteUser(Long id) {
        logger.info("Deleting user: {}", id);
        
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User", id);
        }
        
        userRepository.deleteById(id);
        logger.info("Successfully deleted user: {}", id);
    }
    
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    public UserDto findByEmailDto(String email) {
        logger.debug("Finding user DTO by email: {}", email);
        Optional<User> user = userRepository.findByEmail(email);
        return user.map(dtoMapper::toUserDto).orElse(null);
    }
}