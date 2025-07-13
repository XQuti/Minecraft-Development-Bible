package io.xquti.mdb.controller;

import io.xquti.mdb.dto.UserDto;
import io.xquti.mdb.util.AuthUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthUtils authUtils;

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        logger.debug("Getting current user from token");
        
        UserDto user = authUtils.getCurrentUser(authHeader);
        if (user != null) {
            logger.info("Successfully retrieved current user: {}", user.getEmail());
            return ResponseEntity.ok(user);
        }
        
        logger.warn("Failed to get current user from token");
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