# Minecraft Development Bible - Backend API

A comprehensive Spring Boot REST API for the Minecraft Development Bible platform, providing forum functionality, tutorial management, and user authentication.

## Technologies Used

- **Java 21** - Modern Java features and performance improvements
- **Spring Boot 3.4.1** - Application framework with auto-configuration
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Database abstraction layer
- **PostgreSQL** - Primary database
- **Redis** - Caching and session storage
- **JWT (JSON Web Tokens)** - Stateless authentication
- **OAuth2** - Social login integration
- **OpenAPI/Swagger** - API documentation
- **JUnit 5** - Unit testing framework
- **Mockito** - Mocking framework for tests
- **Gradle** - Build automation tool

## Getting Started

### Prerequisites

- Java 21 or higher
- PostgreSQL 12+ running locally or accessible remotely
- Redis server (optional, for caching)
- Gradle 8.5+ (included via wrapper)

### Local Development Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd MDB/backend
   ```

2. **Configure the database**
   
   Create a PostgreSQL database:
   ```sql
   CREATE DATABASE minecraft_dev_bible;
   CREATE USER mdb_user WITH PASSWORD 'your_password';
   GRANT ALL PRIVILEGES ON DATABASE minecraft_dev_bible TO mdb_user;
   ```

3. **Configure application properties**
   
   Update `src/main/resources/application.yml` with your database credentials:
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/minecraft_dev_bible
       username: mdb_user
       password: your_password
   ```

4. **Set up OAuth2 credentials**
   
   Configure OAuth2 providers in `application.yml`:
   ```yaml
   spring:
     security:
       oauth2:
         client:
           registration:
             github:
               client-id: your_github_client_id
               client-secret: your_github_client_secret
   ```

5. **Configure JWT settings**
   
   Set JWT secret and expiration:
   ```yaml
   jwt:
     secret: your_jwt_secret_key_here
     expiration: 86400000  # 24 hours in milliseconds
   ```

### Running the Application

1. **Using Gradle wrapper (recommended)**
   ```bash
   # On Windows
   gradlew.bat bootRun
   
   # On Unix/Linux/macOS
   ./gradlew bootRun
   ```

2. **Using your IDE**
   - Import the project as a Gradle project
   - Run the `MdbApplication` main class

3. **Using built JAR**
   ```bash
   # Build the application
   gradlew.bat build
   
   # Run the JAR
   java -jar build/libs/mdb-0.0.1-SNAPSHOT.jar
   ```

The application will start on `http://localhost:8080`

### API Documentation

Once the application is running, you can access the interactive API documentation at:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## Testing

### Current Test Status

⚠️ **Known Issue**: Tests currently fail due to Mockito compatibility issues with Java 21. The test structure and logic are complete, but execution is blocked by bytecode generation errors.

### Running Tests

```bash
# Attempt to run all tests (currently failing due to Java 21/Mockito compatibility)
# Windows
gradlew.bat test

# Unix/Linux/macOS
./gradlew test

# Run application without tests
gradlew.bat bootRun

# Build application without running tests
gradlew.bat build -x test
```

### Test Structure

- **Unit Tests**: Located in `src/test/java/io/xquti/mdb/service/`
  - `ForumServiceTest.java`: Comprehensive tests for forum business logic
  - `UserServiceTest.java`: Tests for user management operations
  
- **Integration Tests**: Located in `src/test/java/io/xquti/mdb/controller/`
  - `ForumControllerTest.java`: Tests for forum REST endpoints
  - `AuthControllerTest.java`: Tests for authentication endpoints

### Test Coverage (When Working)

The test suite is designed to cover:
- Service layer business logic with mocked dependencies using Mockito
- REST API endpoints with proper HTTP status codes validation
- Input validation and error handling scenarios
- Authentication and authorization flows

### Known Issues

1. **Mockito Java 21 Compatibility**: Current Mockito version has bytecode generation issues with Java 21
2. **Workaround**: Tests are structurally complete and will work once Mockito compatibility is resolved
3. **Alternative**: Consider using TestContainers for integration testing as an alternative to heavy mocking

### Test Reports (When Working)

After running tests successfully, view the reports:
- **Test Results**: `build/reports/tests/test/index.html`
- **Coverage Report**: `build/reports/jacoco/test/html/index.html`

## Project Structure

```
src/
├── main/
│   ├── java/io/xquti/mdb/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # REST controllers
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── exception/      # Custom exceptions
│   │   ├── model/          # JPA entities
│   │   ├── repository/     # Data access layer
│   │   └── service/        # Business logic layer
│   └── resources/
│       ├── application.yml # Application configuration
│       └── schema.sql      # Database schema
└── test/
    └── java/io/xquti/mdb/
        ├── controller/     # Controller integration tests
        └── service/        # Service unit tests
```

## API Endpoints

### Forum API
- `GET /api/forums/threads` - Get paginated forum threads
- `POST /api/forums/threads` - Create new thread (authenticated)
- `GET /api/forums/threads/{id}/posts` - Get thread posts
- `POST /api/forums/threads/{id}/posts` - Create new post (authenticated)

### Authentication API
- `GET /api/auth/me` - Get current user info
- `POST /api/auth/logout` - Logout user
- `GET /oauth2/authorization/{provider}` - OAuth2 login

### Tutorial API
- `GET /api/tutorials/modules` - Get tutorial modules
- `GET /api/tutorials/modules/{id}/lessons` - Get module lessons
- `GET /api/tutorials/lessons/{id}` - Get lesson content

## Database Schema

The application uses PostgreSQL with the following main entities:
- **User** - User accounts and profiles
- **ForumThread** - Forum discussion threads
- **ForumPost** - Individual posts within threads
- **TutorialModule** - Tutorial course modules
- **TutorialLesson** - Individual lessons within modules

## Security

- **JWT Authentication** - Stateless token-based authentication
- **OAuth2 Integration** - Social login with GitHub, Google, etc.
- **CORS Configuration** - Configured for frontend integration
- **Input Validation** - Request validation using Bean Validation
- **SQL Injection Protection** - JPA/Hibernate parameterized queries

## Development Guidelines

### Code Style
- Follow Java naming conventions
- Use meaningful variable and method names
- Add JavaDoc comments for public APIs
- Keep methods focused and concise

### Testing
- Write unit tests for all service methods
- Create integration tests for controllers
- Aim for >80% code coverage
- Use descriptive test method names

### Error Handling
- Use custom exceptions for business logic errors
- Implement global exception handling
- Return appropriate HTTP status codes
- Provide meaningful error messages

## Troubleshooting

### Common Issues

1. **Database Connection Failed**
   - Verify PostgreSQL is running
   - Check database credentials in `application.yml`
   - Ensure database exists and user has permissions

2. **JWT Token Issues**
   - Verify JWT secret is configured
   - Check token expiration settings
   - Ensure proper Authorization header format

3. **OAuth2 Login Problems**
   - Verify OAuth2 client credentials
   - Check redirect URIs in OAuth2 provider settings
   - Ensure proper CORS configuration

### Logging

Enable debug logging for troubleshooting:
```yaml
logging:
  level:
    io.xquti.mdb: DEBUG
    org.springframework.security: DEBUG
```

## Contributing

1. Create a feature branch from `main`
2. Write tests for new functionality
3. Ensure all tests pass
4. Follow the existing code style
5. Submit a pull request with clear description

## License

This project is licensed under the MIT License - see the LICENSE file for details.