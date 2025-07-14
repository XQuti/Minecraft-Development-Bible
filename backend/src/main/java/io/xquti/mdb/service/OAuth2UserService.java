package io.xquti.mdb.service;

import io.xquti.mdb.model.User;
import io.xquti.mdb.model.User.Role;
import io.xquti.mdb.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

/**
 * Custom OAuth2 User Service for handling Google and GitHub authentication
 * Follows Spring Security OAuth2 best practices with proper user management
 * and security validation
 */
@Service
public class OAuth2UserService extends DefaultOAuth2UserService {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        try {
            OAuth2User oauth2User = super.loadUser(userRequest);
            
            // Process and save/update user information
            User user = processOAuth2User(userRequest, oauth2User);
            
            // Return custom OAuth2User implementation with user details
            return new CustomOAuth2User(oauth2User, user);
            
        } catch (Exception e) {
            logger.error("Failed to load OAuth2 user: {}", e.getMessage(), e);
            throw new OAuth2AuthenticationException("OAuth2 user loading failed");
        }
    }

    /**
     * Process OAuth2 user information and create/update user in database
     * Handles both Google and GitHub providers with proper attribute mapping
     */
    private User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oauth2User.getAttributes();
        
        // Extract user information based on provider
        UserInfo userInfo = extractUserInfo(registrationId, attributes);
        
        // Validate required user information
        if (userInfo.email() == null || userInfo.email().trim().isEmpty()) {
            throw new OAuth2AuthenticationException("Email not available from OAuth2 provider");
        }
        
        // Find existing user or create new one
        Optional<User> existingUser = userRepository.findByEmail(userInfo.email());
        
        if (existingUser.isPresent()) {
            return updateExistingUser(existingUser.get(), userInfo, registrationId);
        } else {
            return createNewUser(userInfo, registrationId);
        }
    }

    /**
     * Extract user information from OAuth2 attributes based on provider
     * Handles different attribute structures for Google and GitHub
     */
    private UserInfo extractUserInfo(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId.toLowerCase()) {
            case "google" -> new UserInfo(
                (String) attributes.get("email"),
                (String) attributes.get("name"),
                (String) attributes.get("picture"),
                (String) attributes.get("sub")
            );
            case "github" -> new UserInfo(
                (String) attributes.get("email"),
                (String) attributes.get("name"),
                (String) attributes.get("avatar_url"),
                String.valueOf(attributes.get("id"))
            );
            default -> throw new OAuth2AuthenticationException("Unsupported OAuth2 provider: " + registrationId);
        };
    }

    /**
     * Update existing user with OAuth2 information
     * Preserves existing user data while updating OAuth2 specific fields
     */
    private User updateExistingUser(User existingUser, UserInfo userInfo, String provider) {
        logger.info("Updating existing user: {} from provider: {}", userInfo.email(), provider);
        
        // Update user information if changed
        boolean updated = false;
        
        if (userInfo.avatarUrl() != null && !userInfo.avatarUrl().equals(existingUser.getAvatarUrl())) {
            existingUser.setAvatarUrl(userInfo.avatarUrl());
            updated = true;
        }
        
        // Update provider information
        existingUser.setProvider(provider);
        existingUser.setProviderId(userInfo.providerId());
        
        if (updated) {
            existingUser.setUpdatedAt(LocalDateTime.now());
        }
        
        return userRepository.save(existingUser);
    }

    /**
     * Create new user from OAuth2 information
     * Sets up default user configuration for new OAuth2 users
     */
    private User createNewUser(UserInfo userInfo, String provider) {
        logger.info("Creating new user: {} from provider: {}", userInfo.email(), provider);
        
        User newUser = new User();
        newUser.setEmail(userInfo.email());
        newUser.setUsername(userInfo.name() != null ? userInfo.name() : extractNameFromEmail(userInfo.email()));
        newUser.setAvatarUrl(userInfo.avatarUrl());
        newUser.setProvider(provider);
        newUser.setProviderId(userInfo.providerId());
        // Default role USER is already set in constructor
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(newUser);
    }

    /**
     * Extract display name from email if name is not provided
     * Fallback method for providers that don't provide name
     */
    private String extractNameFromEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "User";
        }
        return email.substring(0, email.indexOf("@"));
    }

    /**
     * Record class for holding extracted user information
     * Uses Java 24 records for immutable data transfer
     */
    private record UserInfo(
        String email,
        String name,
        String avatarUrl,
        String providerId
    ) {}

    /**
     * Custom OAuth2User implementation that includes User entity
     * Provides access to both OAuth2 attributes and database user information
     */
    public static class CustomOAuth2User implements OAuth2User {
        private final OAuth2User oauth2User;
        private final User user;

        public CustomOAuth2User(OAuth2User oauth2User, User user) {
            this.oauth2User = oauth2User;
            this.user = user;
        }

        @Override
        public Map<String, Object> getAttributes() {
            return oauth2User.getAttributes();
        }

        @Override
        public java.util.Collection<? extends org.springframework.security.core.GrantedAuthority> getAuthorities() {
            return oauth2User.getAuthorities();
        }

        @Override
        public String getName() {
            return oauth2User.getName();
        }

        public User getUser() {
            return user;
        }
    }
}