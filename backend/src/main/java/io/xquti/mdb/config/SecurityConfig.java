package io.xquti.mdb.config;

import io.xquti.mdb.config.security.CorsSecurityConfig;
import io.xquti.mdb.config.security.JwtAuthenticationFilter;
import io.xquti.mdb.config.security.OAuth2SecurityConfig;
import io.xquti.mdb.config.security.SecurityHeadersConfig;
import io.xquti.mdb.config.security.InputValidationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Autowired
    private CorsSecurityConfig corsSecurityConfig;
    
    @Autowired
    private OAuth2SecurityConfig oAuth2SecurityConfig;
    
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Autowired
    private InputValidationConfig inputValidationConfig;
    
    @Autowired
    private SecurityHeadersConfig securityHeadersConfig;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        logger.info("Configuring Spring Security filter chain with modular security components");
        
        http
            // CORS Configuration - delegated to specialized component
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // CSRF Protection - secure configuration for stateless JWT
            .csrf(csrf -> csrf
                .csrfTokenRepository(org.springframework.security.web.csrf.CookieCsrfTokenRepository.withHttpOnlyFalse())
                .ignoringRequestMatchers("/oauth2/**", "/login/**", "/api/auth/refresh")
                .csrfTokenRequestHandler(new org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler())
            )
            
            // Session Management - stateless for JWT
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Security Headers - basic configuration
            .headers(headers -> headers
                .frameOptions(frame -> frame.deny())
                .contentTypeOptions(content -> content.and())
                .httpStrictTransportSecurity(hsts -> hsts
                    .maxAgeInSeconds(31536000)
                )
            )
            
            // Authorization Rules - clean and maintainable
            .authorizeHttpRequests(this::configureAuthorization)
            
            // OAuth2 Configuration - delegated to specialized component
            .oauth2Login(oAuth2SecurityConfig::configureOAuth2Login)
            
            // Security Filters - properly ordered
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(inputValidationConfig.inputValidationFilter(), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(securityHeadersConfig.securityHeadersFilter(), UsernamePasswordAuthenticationFilter.class);

        logger.info("Spring Security filter chain configured successfully");
        return http.build();
    }
    
    /**
     * Configure authorization rules in a clean, maintainable way
     * Following Spring Security 6+ best practices
     */
    private void configureAuthorization(org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authz) {
        authz
            // Public endpoints - read-only access
            .requestMatchers("/api/tutorials/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/forums/threads").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/forums/threads/*/posts").permitAll()
            .requestMatchers("/oauth2/**", "/login/**").permitAll()
            .requestMatchers("/actuator/health").permitAll()
            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
            
            // Auth endpoints - handle authentication internally
            .requestMatchers("/api/auth/me").permitAll()
            .requestMatchers("/api/auth/logout").permitAll()
            .requestMatchers("/api/auth/refresh").permitAll()
            
            // Forum write operations require authentication
            .requestMatchers(HttpMethod.POST, "/api/forums/threads").authenticated()
            .requestMatchers(HttpMethod.POST, "/api/forums/threads/*/posts").authenticated()
            .requestMatchers(HttpMethod.PUT, "/api/forums/**").authenticated()
            .requestMatchers(HttpMethod.DELETE, "/api/forums/**").authenticated()
            
            // Admin endpoints require ADMIN role
            .requestMatchers("/api/admin/**").hasRole("ADMIN")
            
            // All other requests require authentication
            .anyRequest().authenticated();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        return corsSecurityConfig.corsConfigurationSource();
    }
}