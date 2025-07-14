package io.xquti.mdb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class WebSecurityConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String allowedOrigins = System.getenv("ALLOWED_ORIGINS");
        if (allowedOrigins != null) {
            registry.addMapping("/api/**")
                    .allowedOriginPatterns(allowedOrigins.split(","))
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowedHeaders("*")
                    .allowCredentials(true)
                    .maxAge(3600);
        } else {
            registry.addMapping("/api/**")
                    .allowedOriginPatterns("http://localhost:4200", "http://localhost:3000")
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowedHeaders("*")
                    .allowCredentials(true)
                    .maxAge(3600);
        }
    }

    @Bean
    public Filter basicSecurityHeadersFilter() {
        return new Filter() {
            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                    throws IOException, ServletException {
                
                HttpServletResponse httpResponse = (HttpServletResponse) response;
                
                // Content Security Policy
                httpResponse.setHeader("Content-Security-Policy", 
                    "default-src 'self'; " +
                    "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
                    "style-src 'self' 'unsafe-inline'; " +
                    "img-src 'self' data: https:; " +
                    "font-src 'self' data:; " +
                    "connect-src 'self' http://localhost:8080 ws://localhost:4200; " +
                    "frame-ancestors 'none';"
                );
                
                // Additional security headers
                httpResponse.setHeader("Permissions-Policy", 
                    "geolocation=(), microphone=(), camera=(), payment=(), usb=()");
                httpResponse.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
                httpResponse.setHeader("X-Content-Type-Options", "nosniff");
                httpResponse.setHeader("X-Frame-Options", "DENY");
                httpResponse.setHeader("X-XSS-Protection", "1; mode=block");
                
                chain.doFilter(request, response);
            }
        };
    }
}