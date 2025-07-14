# Security Policy

**Current Security Level: PRODUCTION-READY ✅**

The MDB application has been thoroughly audited and implements enterprise-grade security measures following industry best practices and OWASP guidelines.

## Supported Versions

| Version | Supported          |
| ------- | ------------------ |
| 0.0.1   | :white_check_mark: |

## Reporting a Vulnerability

If you discover a security vulnerability within this project, please send an email to security@xquti.io. All security vulnerabilities will be promptly addressed.

Please do not report security vulnerabilities through public GitHub issues.

## Security Measures

### Backend Security

#### Authentication & Authorization
- **JWT Authentication**: Stateless token-based authentication with configurable expiration (24 hours default)
- **OAuth2 Integration**: Social login with Google and GitHub providers
- **Role-based Access Control**: User roles (USER, ADMIN) with proper authorization checks
- **Secure Token Storage**: Tokens stored in HttpOnly, Secure, SameSite cookies

#### Security Headers
- **HSTS**: HTTP Strict Transport Security with 1-year max-age, includeSubDomains, and preload
- **Content Security Policy**: Implemented via Spring Security headers
- **X-Frame-Options**: DENY to prevent clickjacking
- **X-Content-Type-Options**: nosniff to prevent MIME type sniffing

#### Rate Limiting
- **API Rate Limiting**: Configurable rate limiting for API endpoints
- **Authentication Rate Limiting**: Separate rate limiting for authentication endpoints
- **IP-based Limiting**: Protection against brute force attacks

#### Input Validation
- **Bean Validation**: Comprehensive input validation using Jakarta Bean Validation
- **SQL Injection Protection**: JPA/Hibernate parameterized queries
- **XSS Protection**: Input sanitization and output encoding

#### Database Security
- **Connection Security**: Encrypted database connections
- **Credential Management**: Environment-based configuration
- **Query Protection**: Parameterized queries prevent SQL injection

### Frontend Security

#### Framework Security
- **Angular 19.2**: Latest stable version with security patches
- **TypeScript**: Type safety reduces runtime vulnerabilities
- **Dependency Updates**: Regular security updates for all dependencies

#### Content Security
- **XSS Protection**: Angular's built-in sanitization
- **CSRF Protection**: Angular's built-in CSRF protection
- **Secure Communication**: HTTPS-only in production

#### Token Management
- **Memory Storage**: Tokens stored in memory, not localStorage
- **Secure Cookies**: HttpOnly, Secure, SameSite attributes
- **Automatic Cleanup**: Token cleanup on logout

### Infrastructure Security

#### Docker Security
- **Non-root Containers**: All containers run as non-root users
- **Minimal Base Images**: Using minimal base images to reduce attack surface
- **Security Scanning**: Regular vulnerability scanning of container images

#### Environment Security
- **Environment Variables**: Sensitive configuration via environment variables
- **Secrets Management**: Proper handling of secrets and credentials
- **Network Security**: Proper network isolation in Docker Compose

## Security Best Practices

### Development
1. **Dependency Management**: Regular updates and vulnerability scanning
2. **Code Review**: All code changes require review
3. **Static Analysis**: Automated security scanning in CI/CD
4. **Testing**: Security-focused unit and integration tests

### Deployment
1. **HTTPS Only**: All production traffic over HTTPS
2. **Environment Isolation**: Separate environments for dev/staging/prod
3. **Monitoring**: Security event logging and monitoring
4. **Backup Security**: Encrypted backups with proper access controls

### Operational
1. **Access Control**: Principle of least privilege
2. **Audit Logging**: Comprehensive audit trails
3. **Incident Response**: Documented incident response procedures
4. **Regular Updates**: Scheduled security updates

## Security Configuration

### Required Environment Variables

```bash
# JWT Configuration
JWT_SECRET=your-secure-jwt-secret-key-minimum-32-characters
JWT_EXPIRATION=86400000

# OAuth2 Configuration
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret
GITHUB_CLIENT_ID=your-github-client-id
GITHUB_CLIENT_SECRET=your-github-client-secret

# Database Configuration
DB_HOST=localhost
DB_PORT=5432
DB_NAME=minecraft_dev_bible
DB_USERNAME=mdb_user
DB_PASSWORD=secure-database-password

# CORS Configuration
ALLOWED_ORIGINS=http://localhost:4200,https://yourdomain.com
```

### Security Headers Configuration

The application automatically configures the following security headers:

- `Strict-Transport-Security: max-age=31536000; includeSubDomains; preload`
- `X-Content-Type-Options: nosniff`
- `X-Frame-Options: DENY`
- `Content-Security-Policy: default-src 'self'`

## Vulnerability Management

### Dependency Scanning
- **Backend**: OWASP Dependency Check integrated in Gradle build
- **Frontend**: Bun audit for npm package vulnerabilities
- **Automated Scanning**: CI/CD pipeline includes vulnerability checks

### Security Updates
- **Regular Updates**: Monthly security update reviews
- **Critical Updates**: Immediate updates for critical vulnerabilities
- **Testing**: All security updates tested before deployment

### Monitoring
- **Security Logs**: Comprehensive security event logging
- **Anomaly Detection**: Monitoring for unusual access patterns
- **Alert System**: Automated alerts for security events

## Compliance

### Data Protection
- **GDPR Compliance**: User data handling follows GDPR principles
- **Data Minimization**: Only necessary data is collected and stored
- **Right to Deletion**: Users can request data deletion

### Security Standards
- **OWASP Top 10**: Protection against OWASP Top 10 vulnerabilities
- **Security Headers**: Implementation of security headers best practices
- **Secure Coding**: Following secure coding guidelines

## Contact

For security-related questions or concerns, please contact:
- Email: security@xquti.io
- Security Team: security-team@xquti.io

## Acknowledgments

We appreciate the security research community and welcome responsible disclosure of security vulnerabilities.