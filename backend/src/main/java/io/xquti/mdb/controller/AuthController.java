package io.xquti.mdb.controller;

import io.xquti.mdb.dto.UserDto;
import io.xquti.mdb.service.JwtService;
import io.xquti.mdb.util.AuthUtils;
import io.xquti.mdb.config.security.TokenBlacklistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthUtils authUtils;
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        logger.debug("Getting current user from token");
        
        UserDto user = authUtils.getCurrentUser(authHeader);
        if (user != null) {
            logger.info("Successfully retrieved current user: {}", user.email());
            return ResponseEntity.ok(user);
        }
        
        logger.warn("Failed to get current user from token");
        return ResponseEntity.status(401).build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            HttpServletResponse response,
            jakarta.servlet.http.HttpServletRequest request) {
        logger.info("User logout requested");
        
        // SECURITY: Blacklist both access and refresh tokens
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                java.time.Instant expiration = jwtService.extractExpiration(token).toInstant();
                tokenBlacklistService.blacklistToken(token, expiration);
                logger.info("JWT access token blacklisted successfully");
            } catch (Exception e) {
                logger.warn("Failed to blacklist access token during logout: {}", e.getMessage());
            }
        }
        
        // SECURITY: Also check for token in cookies and blacklist
        jakarta.servlet.http.Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (jakarta.servlet.http.Cookie cookie : cookies) {
                if ("auth_token".equals(cookie.getName()) || "refresh_token".equals(cookie.getName())) {
                    try {
                        java.time.Instant expiration = jwtService.extractExpiration(cookie.getValue()).toInstant();
                        tokenBlacklistService.blacklistToken(cookie.getValue(), expiration);
                        logger.info("JWT {} blacklisted successfully", cookie.getName());
                    } catch (Exception e) {
                        logger.warn("Failed to blacklist {} during logout: {}", cookie.getName(), e.getMessage());
                    }
                }
            }
        }
        
        // SECURITY: Clear both auth and refresh cookies with proper security attributes
        response.addHeader("Set-Cookie", 
            "auth_token=; Path=/; HttpOnly; Secure; SameSite=Strict; Max-Age=0");
        response.addHeader("Set-Cookie", 
            "refresh_token=; Path=/api/auth/refresh; HttpOnly; Secure; SameSite=Strict; Max-Age=0");
        
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "Logged out successfully");
        return ResponseEntity.ok(responseBody);
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshToken(
            jakarta.servlet.http.HttpServletRequest request,
            HttpServletResponse response) {
        logger.info("Token refresh requested");
        
        // SECURITY: Get refresh token from cookie only (not from header for security)
        String refreshToken = null;
        jakarta.servlet.http.Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (jakarta.servlet.http.Cookie cookie : cookies) {
                if ("refresh_token".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }
        
        if (refreshToken == null) {
            logger.warn("Refresh token not found in request");
            return ResponseEntity.status(401).body(Map.of("error", "Refresh token required"));
        }
        
        try {
            // SECURITY: Validate refresh token
            String username = jwtService.extractUsername(refreshToken);
            if (jwtService.validateToken(refreshToken, username)) {
                // SECURITY: Generate new access token
                String newAccessToken = jwtService.generateToken(username, false);
                
                // SECURITY: Set new access token in cookie
                response.addHeader("Set-Cookie", 
                    String.format("auth_token=%s; Path=/; HttpOnly; Secure; SameSite=Strict; Max-Age=%d", 
                        newAccessToken, 60 * 60)); // 1 hour
                
                logger.info("Token refreshed successfully for user: {}", username);
                return ResponseEntity.ok(Map.of("message", "Token refreshed successfully"));
            } else {
                logger.warn("Invalid refresh token provided");
                return ResponseEntity.status(401).body(Map.of("error", "Invalid refresh token"));
            }
        } catch (Exception e) {
            logger.error("Error during token refresh: {}", e.getMessage());
            return ResponseEntity.status(401).body(Map.of("error", "Token refresh failed"));
        }
    }
}