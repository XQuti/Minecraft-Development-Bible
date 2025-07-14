package io.xquti.mdb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class RateLimitConfig {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitConfig.class);
    private final RedisTemplate<String, String> redisTemplate;

    public RateLimitConfig(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Bean
    public Filter rateLimitFilter() {
        return new Filter() {
            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                    throws IOException, ServletException {
                
                HttpServletRequest httpRequest = (HttpServletRequest) request;
                HttpServletResponse httpResponse = (HttpServletResponse) response;
                
                String clientIp = getClientIpAddress(httpRequest);
                String requestUri = httpRequest.getRequestURI();
                
                // SECURITY: Apply strict rate limiting to authentication endpoints
                if (requestUri.startsWith("/oauth2/") || requestUri.startsWith("/api/auth/")) {
                    if (!isRequestAllowed(clientIp, "auth", 5, 60)) { // Reduced to 5 requests per minute for security
                        httpResponse.setStatus(429);
                        httpResponse.setContentType("application/json");
                        httpResponse.addHeader("Retry-After", "60");
                        httpResponse.getWriter().write("{\"error\":\"Too many authentication requests. Please try again later.\"}");
                        return;
                    }
                }
                
                // SECURITY: Apply rate limiting to API endpoints with different limits
                if (requestUri.startsWith("/api/forums/") && "POST".equals(httpRequest.getMethod())) {
                    // Stricter limits for forum posting to prevent spam
                    if (!isRequestAllowed(clientIp, "forum_post", 10, 300)) { // 10 posts per 5 minutes
                        httpResponse.setStatus(429);
                        httpResponse.setContentType("application/json");
                        httpResponse.addHeader("Retry-After", "300");
                        httpResponse.getWriter().write("{\"error\":\"Too many posts. Please wait before posting again.\"}");
                        return;
                    }
                } else if (requestUri.startsWith("/api/")) {
                    if (!isRequestAllowed(clientIp, "api", 60, 60)) { // Reduced from 100 to 60 requests per minute
                        httpResponse.setStatus(429);
                        httpResponse.setContentType("application/json");
                        httpResponse.addHeader("Retry-After", "60");
                        httpResponse.getWriter().write("{\"error\":\"Too many API requests. Please slow down.\"}");
                        return;
                    }
                }
                
                chain.doFilter(request, response);
            }
        };
    }

    private String getClientIpAddress(HttpServletRequest request) {
        // SECURITY: Get the actual remote address first (most reliable)
        String remoteAddr = request.getRemoteAddr();
        
        // SECURITY: Only trust proxy headers if we're behind a known proxy
        String trustedProxies = System.getenv("TRUSTED_PROXIES");
        boolean trustProxyHeaders = trustedProxies != null && !trustedProxies.trim().isEmpty();
        
        if (trustProxyHeaders) {
            // SECURITY: Validate that the request is coming from a trusted proxy
            if (isTrustedProxy(remoteAddr, trustedProxies)) {
                String xForwardedFor = request.getHeader("X-Forwarded-For");
                if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                    // SECURITY: Take the first IP (original client) and validate strictly
                    String clientIp = xForwardedFor.split(",")[0].trim();
                    
                    // SECURITY: Strict validation to prevent header injection attacks
                    if (isValidIpAddress(clientIp) && !isPrivateOrReservedIp(clientIp)) {
                        return clientIp;
                    }
                }
                
                String xRealIp = request.getHeader("X-Real-IP");
                if (xRealIp != null && !xRealIp.isEmpty() && 
                    isValidIpAddress(xRealIp) && !isPrivateOrReservedIp(xRealIp)) {
                    return xRealIp;
                }
            }
        }
        
        return remoteAddr;
    }
    
    private boolean isTrustedProxy(String remoteAddr, String trustedProxies) {
        String[] proxies = trustedProxies.split(",");
        for (String proxy : proxies) {
            if (remoteAddr.equals(proxy.trim())) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isValidIpAddress(String ip) {
        // SECURITY: Strict IP validation to prevent header injection
        if (ip == null || ip.trim().isEmpty() || ip.length() > 45) {
            return false;
        }
        
        // IPv4 validation with range checking
        if (ip.matches("^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$")) {
            String[] parts = ip.split("\\.");
            for (String part : parts) {
                int num = Integer.parseInt(part);
                if (num < 0 || num > 255) {
                    return false;
                }
            }
            return true;
        }
        
        // IPv6 validation (simplified)
        if (ip.matches("^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$")) {
            return true;
        }
        
        return false;
    }
    
    private boolean isPrivateOrReservedIp(String ip) {
        // SECURITY: Reject private/reserved IPs that could be spoofed
        if (ip.startsWith("10.") || 
            ip.startsWith("192.168.") || 
            ip.matches("^172\\.(1[6-9]|2[0-9]|3[0-1])\\..*") ||
            ip.startsWith("127.") ||
            ip.equals("0.0.0.0") ||
            ip.startsWith("169.254.") ||
            ip.startsWith("::1") ||
            ip.startsWith("fc00:") ||
            ip.startsWith("fe80:")) {
            return true;
        }
        return false;
    }

    private boolean isRequestAllowed(String clientIp, String endpoint, int maxRequests, int windowSeconds) {
        try {
            String key = "rate_limit:" + endpoint + ":" + clientIp;
            String currentCount = redisTemplate.opsForValue().get(key);
            
            if (currentCount == null) {
                redisTemplate.opsForValue().set(key, "1", Duration.ofSeconds(windowSeconds));
                return true;
            }
            
            int count = Integer.parseInt(currentCount);
            if (count >= maxRequests) {
                return false;
            }
            
            redisTemplate.opsForValue().increment(key);
            return true;
        } catch (Exception e) {
            // SECURITY: Always fail secure to prevent abuse when Redis is down
            // This prevents rate limit bypass attacks via Redis DoS
            logger.error("Rate limiting failed for endpoint {} and IP {}: {}", endpoint, clientIp, e.getMessage());
            return false;
        }
    }
}