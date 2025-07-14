package io.xquti.mdb.config.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * CORS Security Configuration Component
 * 
 * Implements secure CORS configuration following OWASP best practices:
 * - No wildcard origins allowed
 * - Strict origin validation with regex
 * - Production HTTPS enforcement
 * - Minimal allowed methods and headers
 * 
 * @see <a href="https://owasp.org/www-community/attacks/CORS_OriginHeaderScrutiny">OWASP CORS Security</a>
 * @see <a href="https://docs.spring.io/spring-security/reference/servlet/integrations/cors.html">Spring Security CORS</a>
 */
@Component
public class CorsSecurityConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(CorsSecurityConfig.class);
    
    /**
     * Creates a secure CORS configuration source with strict validation
     * 
     * @return CorsConfigurationSource with security-hardened configuration
     * @throws IllegalArgumentException if CORS configuration is invalid or insecure
     */
    public CorsConfigurationSource corsConfigurationSource() {
        logger.info("Configuring CORS security with strict validation");
        
        CorsConfiguration configuration = new CorsConfiguration();
        
        // CRITICAL SECURITY: Strict CORS configuration with comprehensive validation
        String allowedOrigins = System.getenv("ALLOWED_ORIGINS");
        String environment = System.getenv("SPRING_PROFILES_ACTIVE");
        boolean isProduction = "production".equals(environment) || "prod".equals(environment);
        
        if (allowedOrigins != null && !allowedOrigins.trim().isEmpty()) {
            configuration.setAllowedOrigins(validateAndParseOrigins(allowedOrigins, isProduction));
        } else {
            configuration.setAllowedOrigins(getDefaultOrigins(isProduction));
        }
        
        // SECURITY: Restrict allowed methods to minimum required
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // SECURITY: Restrict allowed headers (absolutely no wildcards)
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin", "X-CSRF-Token"
        ));
        
        // SECURITY: Restrict exposed headers
        configuration.setExposedHeaders(Arrays.asList("X-Total-Count", "X-Page-Count"));
        
        // SECURITY: Enable credentials for authenticated requests but with strict origin validation
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(300L); // Reduced cache time to 5 minutes for security
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        logger.info("CORS security configuration completed successfully");
        return source;
    }
    
    /**
     * Validates and parses CORS origins with strict security checks
     * 
     * @param allowedOrigins comma-separated list of origins
     * @param isProduction whether running in production environment
     * @return List of validated origins
     * @throws IllegalArgumentException if any origin fails validation
     */
    private List<String> validateAndParseOrigins(String allowedOrigins, boolean isProduction) {
        String[] origins = allowedOrigins.split(",");
        List<String> validOrigins = new ArrayList<>();
        
        for (String origin : origins) {
            origin = origin.trim();
            
            // SECURITY: Absolutely no wildcards allowed
            if ("*".equals(origin) || origin.contains("*")) {
                logger.error("Wildcard CORS origin (*) is strictly prohibited for security reasons: {}", origin);
                throw new IllegalArgumentException("Wildcard CORS origins are prohibited");
            }
            
            // SECURITY: Validate origin format with strict regex
            if (!isValidOriginFormat(origin)) {
                logger.error("Invalid CORS origin format rejected: {}", origin);
                throw new IllegalArgumentException("Invalid CORS origin format: " + origin);
            }
            
            // SECURITY: Production must use HTTPS only
            if (isProduction && origin.startsWith("http://")) {
                logger.error("HTTP origins not allowed in production: {}", origin);
                throw new IllegalArgumentException("Production environment requires HTTPS origins only");
            }
            
            // SECURITY: Reject suspicious domains
            String domain = origin.replaceFirst("^https?://", "").split(":")[0];
            if (domain.contains("localhost") && isProduction) {
                logger.error("Localhost origins not allowed in production: {}", origin);
                throw new IllegalArgumentException("Localhost origins not allowed in production");
            }
            
            validOrigins.add(origin);
            logger.info("Validated CORS origin: {}", origin);
        }
        
        if (validOrigins.isEmpty()) {
            logger.error("No valid CORS origins found in ALLOWED_ORIGINS");
            throw new IllegalArgumentException("No valid CORS origins configured");
        }
        
        return validOrigins;
    }
    
    /**
     * Gets default origins for development environment
     * 
     * @param isProduction whether running in production
     * @return List of default origins
     * @throws IllegalArgumentException if called in production
     */
    private List<String> getDefaultOrigins(boolean isProduction) {
        if (isProduction) {
            logger.error("ALLOWED_ORIGINS environment variable is required in production");
            throw new IllegalArgumentException("ALLOWED_ORIGINS must be configured in production");
        }
        
        logger.warn("Using default CORS origins for development environment");
        return Arrays.asList("http://localhost:4200", "http://localhost:3000");
    }
    
    /**
     * Validates origin format using strict regex
     * 
     * @param origin the origin to validate
     * @return true if origin format is valid
     */
    private boolean isValidOriginFormat(String origin) {
        // Strict regex for valid HTTP/HTTPS origins
        String originRegex = "^https?://[a-zA-Z0-9]([a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?" +
                            "(\\.([a-zA-Z0-9]([a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?))*+" +
                            "(:[1-9][0-9]{0,4})?$";
        return origin.matches(originRegex);
    }
}