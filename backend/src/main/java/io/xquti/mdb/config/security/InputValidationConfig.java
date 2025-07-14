package io.xquti.mdb.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * SECURITY: Input validation and sanitization configuration
 * Prevents XSS, injection attacks, and malicious input
 */
@Configuration
public class InputValidationConfig {

    // SECURITY: Compile patterns once for performance
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
        "(?i)(union|select|insert|update|delete|drop|create|alter|exec|execute|script|javascript|vbscript|onload|onerror)",
        Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern XSS_PATTERN = Pattern.compile(
        "(?i)(<script|</script|javascript:|vbscript:|onload=|onerror=|onclick=|onmouseover=|<iframe|</iframe)",
        Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern PATH_TRAVERSAL_PATTERN = Pattern.compile(
        "(\\.\\./|\\.\\.\\\\|%2e%2e%2f|%2e%2e%5c)",
        Pattern.CASE_INSENSITIVE
    );

    @Bean
    public PolicyFactory htmlSanitizer() {
        // SECURITY: Create strict HTML sanitizer policy
        return Sanitizers.FORMATTING
            .and(Sanitizers.LINKS)
            .and(Sanitizers.BLOCKS)
            .and(Sanitizers.TABLES);
    }

    @Bean
    public OncePerRequestFilter inputValidationFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                    FilterChain filterChain) throws ServletException, IOException {
                
                // SECURITY: Validate JSON request bodies for XSS/injection attacks
                if ("POST".equals(request.getMethod()) || "PUT".equals(request.getMethod())) {
                    String contentType = request.getContentType();
                    if (contentType != null && contentType.contains("application/json")) {
                        try {
                            // Read request body
                            String body = request.getReader().lines()
                                .collect(java.util.stream.Collectors.joining("\n"));
                            
                            if (body != null && !body.trim().isEmpty() && !isInputSafe(body)) {
                                response.setStatus(400);
                                response.setContentType("application/json");
                                response.getWriter().write("{\"error\":\"Malicious content detected in request body\"}");
                                return;
                            }
                        } catch (Exception e) {
                            // If we can't read the body, allow the request to continue
                            // This prevents breaking legitimate requests due to stream issues
                        }
                    }
                }
                
                // SECURITY: Validate request parameters
                if (request.getParameterMap() != null) {
                    for (String paramName : request.getParameterMap().keySet()) {
                        String[] paramValues = request.getParameterValues(paramName);
                        if (paramValues != null) {
                            for (String paramValue : paramValues) {
                                if (paramValue != null && !isInputSafe(paramValue)) {
                                    response.setStatus(400);
                                    response.setContentType("application/json");
                                    response.getWriter().write("{\"error\":\"Invalid input detected\"}");
                                    return;
                                }
                            }
                        }
                    }
                }
                
                // SECURITY: Validate request headers for suspicious content
                if (request.getHeaderNames() != null) {
                    java.util.Enumeration<String> headerNames = request.getHeaderNames();
                    while (headerNames.hasMoreElements()) {
                        String headerName = headerNames.nextElement();
                        String headerValue = request.getHeader(headerName);
                        
                        // Skip validation for certain headers that may contain special chars
                        if (!"Authorization".equals(headerName) && 
                            !"Cookie".equals(headerName) && 
                            !"User-Agent".equals(headerName) &&
                            headerValue != null && !isHeaderSafe(headerValue)) {
                            response.setStatus(400);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\":\"Invalid header detected\"}");
                            return;
                        }
                    }
                }
                
                // SECURITY: Validate request URI for path traversal
                String requestURI = request.getRequestURI();
                if (requestURI != null && PATH_TRAVERSAL_PATTERN.matcher(requestURI).find()) {
                    response.setStatus(400);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\":\"Path traversal attempt detected\"}");
                    return;
                }
                
                filterChain.doFilter(request, response);
            }
        };
    }
    
    /**
     * SECURITY: Check if input is safe from common injection attacks
     */
    private boolean isInputSafe(String input) {
        if (input == null || input.trim().isEmpty()) {
            return true;
        }
        
        // SECURITY: Check for SQL injection patterns
        if (SQL_INJECTION_PATTERN.matcher(input).find()) {
            return false;
        }
        
        // SECURITY: Check for XSS patterns
        if (XSS_PATTERN.matcher(input).find()) {
            return false;
        }
        
        // SECURITY: Check for path traversal
        if (PATH_TRAVERSAL_PATTERN.matcher(input).find()) {
            return false;
        }
        
        // SECURITY: Check for null bytes (can bypass filters)
        if (input.contains("\0")) {
            return false;
        }
        
        // SECURITY: Check for excessive length (potential DoS)
        if (input.length() > 10000) {
            return false;
        }
        
        return true;
    }
    
    /**
     * SECURITY: Check if header value is safe
     */
    private boolean isHeaderSafe(String headerValue) {
        if (headerValue == null || headerValue.trim().isEmpty()) {
            return true;
        }
        
        // SECURITY: Check for header injection (CRLF)
        if (headerValue.contains("\r") || headerValue.contains("\n")) {
            return false;
        }
        
        // SECURITY: Check for XSS in headers
        if (XSS_PATTERN.matcher(headerValue).find()) {
            return false;
        }
        
        return true;
    }
}