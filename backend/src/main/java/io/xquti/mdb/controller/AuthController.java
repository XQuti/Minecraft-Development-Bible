package io.xquti.mdb.controller;

import io.xquti.mdb.dto.UserDto;
import io.xquti.mdb.service.JwtService;
import io.xquti.mdb.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        logger.debug("Getting current user from token");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("Missing or invalid Authorization header");
            return ResponseEntity.status(401).build();
        }
        
        try {
            String token = authHeader.substring(7); // Remove "Bearer " prefix
            String email = jwtService.extractUsername(token);
            
            if (jwtService.validateToken(token, email)) {
                UserDto user = userService.findByEmailDto(email);
                if (user != null) {
                    logger.info("Successfully retrieved current user: {}", email);
                    return ResponseEntity.ok(user);
                }
            }
        } catch (Exception e) {
            logger.warn("Failed to get current user from token: {}", e.getMessage());
        }
        
        return ResponseEntity.status(401).build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        logger.info("User logout requested");
        // Since we're using stateless JWT, logout is handled client-side
        // by removing the token from storage
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logged out successfully");
        return ResponseEntity.ok(response);
    }
}