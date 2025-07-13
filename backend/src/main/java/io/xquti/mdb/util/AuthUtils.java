package io.xquti.mdb.util;

import io.xquti.mdb.dto.UserDto;
import io.xquti.mdb.service.JwtService;
import io.xquti.mdb.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Utility class for authentication-related operations.
 * Provides centralized methods for extracting and validating user information from JWT tokens.
 */
@Component
public class AuthUtils {

    private static final Logger logger = LoggerFactory.getLogger(AuthUtils.class);

    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private UserService userService;

    /**
     * Extracts and validates the current user from the Authorization header.
     * 
     * @param authHeader The Authorization header containing the Bearer token
     * @return UserDto if token is valid and user exists, null otherwise
     */
    public UserDto getCurrentUser(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.debug("Missing or invalid Authorization header");
            return null;
        }
        
        try {
            String token = authHeader.substring(7); // Remove "Bearer " prefix
            String email = jwtService.extractUsername(token);
            
            if (jwtService.validateToken(token, email)) {
                return userService.findByEmailDto(email);
            }
        } catch (Exception e) {
            logger.debug("Invalid token in request: {}", e.getMessage());
        }
        return null;
    }
}