# Minecraft Development Bible (MDB)

A comprehensive platform for Minecraft plugin development tutorials, community forums, and resources.

## 🚀 Quick Start

### Prerequisites
- Docker and Docker Compose
- Java 21+ (for local backend development)
- Bun (for local frontend development)

### Running with Docker (Recommended)

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd MDB
   ```

2. **Configure environment variables**
   ```bash
   cp .env.example .env
   # Edit .env with your OAuth2 credentials
   ```

3. **Start the application**
   ```bash
   docker-compose up -d
   ```

4. **Access the application**
   - Frontend: http://localhost:4200
   - Backend API: http://localhost:8080
   - API Documentation: http://localhost:8080/swagger-ui.html

### Local Development

#### Backend
```bash
cd backend
./gradlew bootRun
```

#### Frontend
```bash
cd frontend
bun install
bun run start
```

## 🏗️ Architecture

- **Backend**: Spring Boot 3.4.1 with Java 21
- **Frontend**: Angular 20.1.0 with TypeScript
- **Database**: PostgreSQL 15
- **Cache**: Redis 7
- **Authentication**: OAuth2 (Google, GitHub) + JWT
- **Styling**: Tailwind CSS
- **Build Tools**: Gradle (backend), Bun (frontend)

## 📚 Features

- **Forum System**: Threaded discussions with pagination
- **Tutorial Platform**: Structured learning modules
- **OAuth2 Authentication**: Social login integration
- **Responsive Design**: Mobile-first approach
- **API Documentation**: OpenAPI/Swagger integration

## 🔧 Development Status

### ✅ Completed
- Core architecture and project structure
- Docker containerization
- CI/CD pipeline setup
- Database schema and migrations
- OAuth2 authentication flow
- Basic forum and tutorial functionality

### 🚧 In Progress
- Test suite fixes (temporarily disabled in CI)
- Frontend component logic improvements
- API consistency enhancements

### 📋 TODO
- Re-enable test suites once fixed
- Add comprehensive error handling
- Implement real-time features
- Add admin panel
- Performance optimizations

## 🧪 Testing

Tests are currently disabled in the CI pipeline while being fixed. To run tests locally:

```bash
# Backend (currently failing - being fixed)
cd backend
./gradlew test

# Frontend (currently failing - being fixed)  
cd frontend
bun run test
```

## 🚀 Deployment

The project includes a complete CI/CD pipeline with GitHub Actions:
- Automated builds for backend and frontend
- Docker image creation
- Environment-specific deployments

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Ensure builds pass
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License.