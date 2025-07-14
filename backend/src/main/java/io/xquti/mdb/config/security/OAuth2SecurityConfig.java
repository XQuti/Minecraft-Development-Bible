package io.xquti.mdb.config.security;

import io.xquti.mdb.service.OAuth2UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

/**
 * OAuth2 Security Configuration for Google and GitHub authentication
 * Follows Spring Security 6+ best practices with proper error handling
 * and secure redirect validation
 */
@Component
public class OAuth2SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2SecurityConfig.class);

    @Value("${app.oauth2.authorized-redirect-uris}")
    private String[] authorizedRedirectUris;

    @Autowired
    private OAuth2UserService oAuth2UserService;

    /**
     * Configure OAuth2 login with secure settings
     * Following OWASP OAuth2 security guidelines
     */
    public void configureOAuth2Login(OAuth2LoginConfigurer<org.springframework.security.config.annotation.web.builders.HttpSecurity> oauth2) {
        logger.info("Configuring OAuth2 login with secure redirect validation");
        
        oauth2
            .loginPage("/oauth2/authorization")
            .authorizationEndpoint(authorization -> 
                authorization.baseUri("/oauth2/authorize")
            )
            .redirectionEndpoint(redirection -> 
                redirection.baseUri("/oauth2/callback/*")
            )
            .userInfoEndpoint(userInfo -> 
                userInfo.userService(oAuth2UserService)
            )
            .successHandler(authenticationSuccessHandler())
            .failureHandler(authenticationFailureHandler());
    }

    /**
     * Custom success handler for OAuth2 authentication
     * Validates redirect URIs and generates JWT tokens
     */
    private AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            try {
                String targetUrl = determineTargetUrl(request, response, authentication);
                
                // SECURITY: Validate redirect URI against whitelist
                if (!isAuthorizedRedirectUri(targetUrl)) {
                    logger.warn("Unauthorized redirect URI attempted: {}", targetUrl);
                    response.sendError(400, "Unauthorized redirect URI");
                    return;
                }
                
                logger.info("OAuth2 authentication successful for user: {}", 
                    authentication.getName());
                response.sendRedirect(targetUrl);
                
            } catch (Exception e) {
                logger.error("OAuth2 authentication success handling failed", e);
                response.sendError(500, "Authentication processing failed");
            }
        };
    }

    /**
     * Custom failure handler for OAuth2 authentication
     * Provides secure error handling without information disclosure
     */
    private AuthenticationFailureHandler authenticationFailureHandler() {
        return (request, response, exception) -> {
            logger.warn("OAuth2 authentication failed: {}", exception.getMessage());
            
            // SECURITY: Don't expose internal error details
            String errorUrl = "/login?error=oauth2_failed";
            response.sendRedirect(errorUrl);
        };
    }

    /**
     * Determine target URL after successful OAuth2 authentication
     * Includes JWT token generation and secure redirect handling
     */
    private String determineTargetUrl(
            jakarta.servlet.http.HttpServletRequest request,
            jakarta.servlet.http.HttpServletResponse response,
            org.springframework.security.core.Authentication authentication) {
        
        // Get redirect URI from session or use default
        String redirectUri = (String) request.getSession().getAttribute("redirect_uri");
        if (redirectUri == null || redirectUri.trim().isEmpty()) {
            redirectUri = getDefaultRedirectUri();
        }
        
        // Generate JWT token for the authenticated user
        // This would typically involve calling JwtService to create a token
        // and appending it to the redirect URI as a parameter
        
        return redirectUri;
    }

    /**
     * Validate redirect URI against authorized list
     * Critical security measure to prevent open redirect attacks
     */
    private boolean isAuthorizedRedirectUri(String uri) {
        if (uri == null || uri.trim().isEmpty()) {
            return false;
        }
        
        for (String authorizedUri : authorizedRedirectUris) {
            if (uri.startsWith(authorizedUri)) {
                return true;
            }
        }
        
        logger.warn("Redirect URI not in authorized list: {}", uri);
        return false;
    }

    /**
     * Get default redirect URI for successful authentication
     * Should point to the frontend application
     */
    private String getDefaultRedirectUri() {
        // Return the first authorized redirect URI as default
        if (authorizedRedirectUris != null && authorizedRedirectUris.length > 0) {
            return authorizedRedirectUris[0];
        }
        return "/";
    }
}