package io.xquti.mdb.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket configuration for real-time features.
 * Enables STOMP messaging over WebSocket for forum updates and notifications.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable a simple memory-based message broker to carry messages back to the client
        config.enableSimpleBroker("/topic", "/queue");
        
        // Designate the "/app" prefix for messages that are bound for @MessageMapping-annotated methods
        config.setApplicationDestinationPrefixes("/app");
        
        // Set user destination prefix for private messages
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // SECURITY: Get allowed origins from environment variable
        String allowedOrigins = System.getenv("WEBSOCKET_ALLOWED_ORIGINS");
        String[] origins;
        
        if (allowedOrigins != null && !allowedOrigins.trim().isEmpty()) {
            // SECURITY: Validate each origin strictly
            origins = allowedOrigins.split(",");
            for (String origin : origins) {
                String trimmedOrigin = origin.trim();
                // SECURITY: Reject wildcards and validate HTTPS in production
                if (trimmedOrigin.contains("*") || 
                    (System.getenv("SPRING_PROFILES_ACTIVE") != null && 
                     System.getenv("SPRING_PROFILES_ACTIVE").contains("prod") && 
                     !trimmedOrigin.startsWith("https://"))) {
                    throw new IllegalArgumentException("Invalid WebSocket origin: " + trimmedOrigin);
                }
            }
        } else {
            // SECURITY: Secure defaults for development only
            origins = new String[]{"http://localhost:4200", "http://localhost:3000"};
        }
        
        // Register the "/ws" endpoint for WebSocket connections
        registry.addEndpoint("/ws")
                .setAllowedOrigins(origins) // Use setAllowedOrigins for strict validation
                .withSockJS(); // Enable SockJS fallback options
        
        // Register endpoint without SockJS for native WebSocket clients
        registry.addEndpoint("/ws-native")
                .setAllowedOrigins(origins);
    }
}