package io.xquti.mdb.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Enhanced security headers configuration for maximum protection
 * Implements OWASP security header recommendations
 */
@Configuration
public class SecurityHeadersConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityHeadersConfig.class);
    private static final SecureRandom secureRandom = new SecureRandom();

    @Bean
    public OncePerRequestFilter securityHeadersFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                    FilterChain filterChain) throws ServletException, IOException {
                
                // Generate nonce for CSP
                String nonce = generateNonce();
                
                // SECURITY: Comprehensive security headers following OWASP guidelines
                response.setHeader("X-Content-Type-Options", "nosniff");
                response.setHeader("X-Frame-Options", "DENY");
                response.setHeader("X-XSS-Protection", "0"); // Disabled as recommended by OWASP
                response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
                response.setHeader("Permissions-Policy", 
                    "geolocation=(), microphone=(), camera=(), payment=(), usb=(), " +
                    "accelerometer=(), gyroscope=(), magnetometer=(), midi=(), " +
                    "picture-in-picture=(), sync-xhr=(), fullscreen=(self)");
                
                // SECURITY: Enhanced Content Security Policy with nonce
                String csp = String.format(
                    "default-src 'self'; " +
                    "script-src 'self' 'nonce-%s' 'strict-dynamic'; " +
                    "style-src 'self' 'unsafe-inline'; " +
                    "img-src 'self' data: https:; " +
                    "font-src 'self'; " +
                    "connect-src 'self'; " +
                    "frame-ancestors 'none'; " +
                    "base-uri 'self'; " +
                    "form-action 'self'; " +
                    "object-src 'none'; " +
                    "media-src 'self'; " +
                    "worker-src 'none'; " +
                    "manifest-src 'self'; " +
                    "upgrade-insecure-requests; " +
                    "block-all-mixed-content", nonce);
                response.setHeader("Content-Security-Policy", csp);
                
                // SECURITY: HSTS with preload
                response.setHeader("Strict-Transport-Security", 
                    "max-age=31536000; includeSubDomains; preload");
                
                // SECURITY: Additional security headers
                response.setHeader("X-Permitted-Cross-Domain-Policies", "none");
                response.setHeader("Cross-Origin-Embedder-Policy", "require-corp");
                response.setHeader("Cross-Origin-Opener-Policy", "same-origin");
                response.setHeader("Cross-Origin-Resource-Policy", "same-origin");
                
                // SECURITY: Cache control for sensitive endpoints
                String requestUri = request.getRequestURI();
                if (requestUri.startsWith("/api/auth/") || requestUri.startsWith("/oauth2/")) {
                    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate, private");
                    response.setHeader("Pragma", "no-cache");
                    response.setHeader("Expires", "0");
                }
                
                // SECURITY: Server information hiding
                response.setHeader("Server", "MDB-Platform");
                
                // Add nonce to request attributes for use in templates
                request.setAttribute("cspNonce", nonce);
                
                filterChain.doFilter(request, response);
            }
        };
    }
    
    /**
     * Generate cryptographically secure nonce for CSP
     */
    private String generateNonce() {
        byte[] nonceBytes = new byte[16];
        secureRandom.nextBytes(nonceBytes);
        return Base64.getEncoder().encodeToString(nonceBytes);
    }
}