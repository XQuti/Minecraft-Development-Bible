package io.xquti.mdb.config.security;

import io.xquti.mdb.model.User;
import io.xquti.mdb.service.JwtService;
import io.xquti.mdb.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter for validating JWT tokens in requests
 * Follows Spring Security 6+ best practices with proper error handling
 * and security validation
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        try {
            String authHeader = request.getHeader(AUTHORIZATION_HEADER);
            
            // Skip processing if no Authorization header or not Bearer token
            if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
                filterChain.doFilter(request, response);
                return;
            }

            String jwt = authHeader.substring(BEARER_PREFIX.length());
            String username = jwtService.extractUsername(jwt);

            // Validate token and authenticate user if not already authenticated
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                authenticateUser(request, jwt, username);
            }

        } catch (Exception e) {
            logger.warn("JWT authentication failed: {}", e.getMessage());
            // Clear security context on authentication failure
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Authenticate user with JWT token validation
     * Following Spring Security best practices for token validation
     */
    private void authenticateUser(HttpServletRequest request, String jwt, String username) {
        try {
            // Find user by username
            var userOptional = userService.findByUsername(username);
            if (userOptional.isEmpty()) {
                logger.warn("User not found: {}", username);
                return;
            }
            
            User user = userOptional.get();
            
            // Validate token against username
            if (jwtService.validateToken(jwt, username)) {
                // Create authorities from user roles
                var authorities = user.getRoles().stream()
                    .map(role -> new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + role.name()))
                    .toList();
                
                // Create UserDetails from User entity
                UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                    .username(user.getUsername())
                    .password("") // No password for OAuth2 users
                    .authorities(authorities)
                    .build();
                
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    authorities
                );
                
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                
                logger.debug("Successfully authenticated user: {}", username);
            } else {
                logger.warn("Invalid or blacklisted JWT token for user: {}", username);
            }
            
        } catch (Exception e) {
            logger.warn("Failed to authenticate user {}: {}", username, e.getMessage());
        }
    }

    /**
     * Skip JWT authentication for public endpoints
     * Optimizes performance by avoiding unnecessary token validation
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        
        // Skip JWT validation for public endpoints
        return path.startsWith("/oauth2/") ||
               path.startsWith("/login/") ||
               path.equals("/api/auth/refresh") ||
               path.startsWith("/actuator/health") ||
               path.startsWith("/swagger-ui/") ||
               path.startsWith("/v3/api-docs/") ||
               (path.startsWith("/api/tutorials/") && "GET".equals(request.getMethod())) ||
               (path.startsWith("/api/forums/threads") && "GET".equals(request.getMethod()));
    }
}