# Contributing to Minecraft Development Bible (MDB)

Thank you for your interest in contributing to the Minecraft Development Bible! This document provides guidelines and information for contributors.

## 🚀 Getting Started

### Prerequisites

Before contributing, ensure you have:
- **Java 24+** installed and configured
- **Bun** package manager (not npm/yarn)
- **Docker & Docker Compose** for local development
- **PostgreSQL 15+** and **Redis 7+** (or use Docker)
- **Git** for version control

### Development Environment Setup

1. **Fork and clone the repository**
   ```bash
   git clone https://github.com/your-username/MDB.git
   cd MDB
   ```

2. **Set up environment variables**
   ```bash
   cp .env.example .env
   # Configure your OAuth2 credentials and database settings
   ```

3. **Start development environment**
   ```bash
   # Using Docker (recommended)
   docker-compose up -d
   
   # Or run services individually
   cd backend && ./gradlew bootRun
   cd frontend && bun install && bun start
   ```

## 📋 Development Standards

### Code Quality Requirements

All contributions must meet these standards:

#### Backend (Java/Spring Boot)
- **Java 24** features and syntax
- **Gradle Kotlin DSL** for build configuration
- **Spring Boot 3.5.3+** best practices
- **Clean Architecture** patterns (Controller → Service → Repository)
- **Comprehensive JavaDoc** for public APIs
- **Unit tests** with >80% coverage
- **Integration tests** for REST endpoints
- **Checkstyle** compliance (run `./gradlew checkstyleMain`)

#### Frontend (Angular/TypeScript)
- **Angular 19.2.14+** with TypeScript
- **Bun** as the only package manager
- **Tailwind CSS** for styling
- **Atomic Design** component structure
- **ESLint** compliance (run `bun run lint`)
- **Unit tests** for components and services
- **Accessibility** (WCAG 2.1 AA compliance)

### Security Requirements

All code must follow security best practices:
- **OWASP Top 10** protection
- **Input validation** on all endpoints
- **SQL injection** prevention (parameterized queries)
- **XSS protection** (proper sanitization)
- **CSRF protection** enabled
- **Secure headers** implementation
- **Dependency scanning** (no critical vulnerabilities)

## 🔧 Development Workflow

### Branch Strategy

1. **Create a feature branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. **Make your changes**
   - Follow existing code patterns
   - Write comprehensive tests
   - Update documentation

3. **Test your changes**
   ```bash
   # Backend tests
   cd backend && ./gradlew test
   
   # Frontend tests
   cd frontend && bun test
   
   # Code quality checks
   cd backend && ./gradlew checkstyleMain
   cd frontend && bun run lint
   ```

4. **Security validation**
   ```bash
   # Run security audit
   ./scripts/security-audit.sh
   
   # OWASP dependency check
   cd backend && ./gradlew dependencyCheckAnalyze
   ```

### Commit Guidelines

Follow conventional commit format:
```
type(scope): description

[optional body]

[optional footer]
```

Types:
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes
- `refactor`: Code refactoring
- `test`: Test additions/modifications
- `chore`: Build/tooling changes
- `security`: Security improvements

Examples:
```bash
git commit -m "feat(auth): add OAuth2 GitHub integration"
git commit -m "fix(forum): resolve thread pagination issue"
git commit -m "security(api): implement rate limiting"
```

## 🧪 Testing Requirements

### Backend Testing
- **Unit Tests**: Test all service methods with mocked dependencies
- **Integration Tests**: Test REST endpoints with proper HTTP status codes
- **Security Tests**: Validate authentication and authorization
- **Performance Tests**: Ensure acceptable response times

### Frontend Testing
- **Component Tests**: Test component rendering and user interactions
- **Service Tests**: Test HTTP service calls with mocked responses
- **E2E Tests**: Test critical user flows
- **Accessibility Tests**: Validate WCAG compliance

### Test Commands
```bash
# Backend
cd backend
./gradlew test                    # Run all tests
./gradlew test --tests "*Forum*"  # Run specific tests
./gradlew jacocoTestReport        # Generate coverage report

# Frontend
cd frontend
bun test                          # Run all tests
bun test --watch                  # Run tests in watch mode
bun run test:ci                   # Run tests in CI mode
```

## 📚 Documentation Standards

### Code Documentation
- **JavaDoc** for all public Java methods and classes
- **TSDoc** for TypeScript interfaces and services
- **Inline comments** for complex business logic
- **README updates** for new features

### API Documentation
- **OpenAPI/Swagger** annotations for all endpoints
- **Request/Response examples** in API docs
- **Error code documentation** with proper HTTP status codes

## 🔒 Security Guidelines

### Secure Coding Practices
- **Never commit secrets** (use environment variables)
- **Validate all inputs** (backend and frontend)
- **Use parameterized queries** (prevent SQL injection)
- **Implement proper error handling** (don't expose internals)
- **Follow principle of least privilege**

### Security Testing
- **OWASP ZAP** scanning for web vulnerabilities
- **Dependency vulnerability** scanning
- **Static code analysis** with security rules
- **Authentication/Authorization** testing

## 🚀 Pull Request Process

### Before Submitting
1. **Ensure all tests pass**
2. **Run security scans**
3. **Update documentation**
4. **Verify Docker builds work**
5. **Check CI/CD pipeline passes**

### PR Requirements
- **Clear description** of changes made
- **Link to related issues**
- **Screenshots** for UI changes
- **Breaking changes** clearly documented
- **Security implications** noted

### Review Process
1. **Automated checks** must pass (CI/CD, security scans)
2. **Code review** by maintainers
3. **Security review** for sensitive changes
4. **Documentation review** for user-facing changes

## 🐛 Bug Reports

### Bug Report Template
```markdown
**Bug Description**
Clear description of the bug

**Steps to Reproduce**
1. Step one
2. Step two
3. Step three

**Expected Behavior**
What should happen

**Actual Behavior**
What actually happens

**Environment**
- OS: [e.g., Windows 11, Ubuntu 22.04]
- Java Version: [e.g., Java 24]
- Browser: [e.g., Chrome 120]
- Version: [e.g., v1.0.0]

**Additional Context**
Screenshots, logs, etc.
```

## 💡 Feature Requests

### Feature Request Template
```markdown
**Feature Description**
Clear description of the proposed feature

**Use Case**
Why is this feature needed?

**Proposed Solution**
How should this feature work?

**Alternatives Considered**
Other approaches considered

**Additional Context**
Mockups, examples, etc.
```

## 🛠️ Development Tools

### Recommended IDE Setup
- **IntelliJ IDEA** or **VS Code**
- **Java 24** language support
- **Angular Language Service**
- **ESLint** and **Prettier** extensions
- **Docker** extension for container management

### Required Tools
- **Checkstyle** plugin for Java
- **ESLint** for TypeScript/Angular
- **OWASP Dependency Check**
- **Git hooks** for pre-commit validation

## 📞 Getting Help

### Communication Channels
- **GitHub Issues**: Bug reports and feature requests
- **GitHub Discussions**: General questions and discussions
- **Security Issues**: Email security@mdb-project.com

### Documentation Resources
- [Backend README](backend/README.md)
- [Frontend README](frontend/README.md)
- [Security Policy](docs/security/SECURITY.md)
- [Deployment Guide](docs/DEPLOYMENT.md)

## 📄 License

By contributing to MDB, you agree that your contributions will be licensed under the MIT License.

## 🙏 Recognition

Contributors will be recognized in:
- **CONTRIBUTORS.md** file
- **Release notes** for significant contributions
- **Project documentation** for major features

---

Thank you for contributing to the Minecraft Development Bible! Your efforts help make this platform better for the entire Minecraft development community.