package io.xquti.mdb.config;

import io.xquti.mdb.service.JwtService;
import io.xquti.mdb.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private UserService userService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                // Public endpoints - read-only access
                .requestMatchers("/api/tutorials/**").permitAll()
                .requestMatchers("/api/forums/threads").permitAll()
                .requestMatchers("/api/forums/threads/*/posts").permitAll()
                .requestMatchers("/oauth2/**", "/login/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                // Auth endpoints - handle authentication internally
                .requestMatchers("/api/auth/me").permitAll()
                .requestMatchers("/api/auth/logout").permitAll()
                // Forum write operations require authentication
                .requestMatchers(HttpMethod.POST, "/api/forums/threads").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/forums/threads/*/posts").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/forums/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/forums/**").authenticated()
                // Admin endpoints (if any) would require ADMIN role
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .successHandler(oAuth2AuthenticationSuccessHandler())
                .failureUrl("/login?error=true")
            )
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Only allow specific origins in production
        configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:4200", "http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L); // Cache preflight response for 1 hour
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            OAuth2User oAuth2User = oauthToken.getPrincipal();
            
            String provider = oauthToken.getAuthorizedClientRegistrationId();
            String email = oAuth2User.getAttribute("email");
            String name = oAuth2User.getAttribute("name");
            String providerId = oAuth2User.getAttribute("id").toString();
            String avatarUrl = oAuth2User.getAttribute("avatar_url");
            
            logger.info("OAuth2 authentication successful for user: {} via provider: {}", email, provider);
            
            try {
                // Create or update user
                userService.createOrUpdateUser(email, name, provider, providerId, avatarUrl);
                
                // Generate JWT token
                String token = jwtService.generateToken(email);
                
                // Redirect to frontend with token
                response.sendRedirect("http://localhost:4200/auth/callback?token=" + token);
                
                logger.info("OAuth2 authentication completed successfully for user: {}", email);
            } catch (Exception e) {
                logger.error("Error during OAuth2 authentication for user: {}", email, e);
                response.sendRedirect("http://localhost:4200/login?error=true");
            }
        };
    }

    @Bean
    public OncePerRequestFilter jwtAuthenticationFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                    FilterChain filterChain) throws ServletException, IOException {
                
                String authHeader = request.getHeader("Authorization");
                String token = null;
                String username = null;

                // Extract JWT token from Authorization header
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    token = authHeader.substring(7);
                    try {
                        username = jwtService.extractUsername(token);
                    } catch (Exception e) {
                        logger.debug("Invalid JWT token: " + e.getMessage());
                    }
                }

                // Validate token and set authentication
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    try {
                        if (jwtService.validateToken(token, username)) {
                            logger.debug("JWT token validated successfully for user: " + username);
                            // Create authentication token and set in security context
                            org.springframework.security.authentication.UsernamePasswordAuthenticationToken authToken = 
                                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                                    username, null, java.util.Collections.emptyList());
                            SecurityContextHolder.getContext().setAuthentication(authToken);
                        }
                    } catch (Exception e) {
                        logger.debug("JWT token validation failed for user: " + username + " - error: " + e.getMessage());
                    }
                }

                filterChain.doFilter(request, response);
            }
        };
    }
}