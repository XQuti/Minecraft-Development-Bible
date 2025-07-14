#!/bin/bash

# MDB Security Audit Script
# This script performs comprehensive security checks on the MDB application

set -e

echo "🔒 Starting MDB Security Audit..."
echo "=================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

# Check if we're in the project root
if [ ! -f "docker-compose.yml" ]; then
    print_error "Please run this script from the project root directory"
    exit 1
fi

echo "1. Backend Security Audit"
echo "========================="

cd backend

# Run OWASP Dependency Check
echo "Running OWASP Dependency Check..."
if ./gradlew dependencyCheckAnalyze --no-daemon; then
    print_status "OWASP Dependency Check completed"
else
    print_warning "OWASP Dependency Check had issues - check reports"
fi

# Run Checkstyle
echo "Running Checkstyle analysis..."
if ./gradlew checkstyleMain checkstyleTest --no-daemon; then
    print_status "Checkstyle analysis passed"
else
    print_warning "Checkstyle found issues - check reports"
fi

# Run tests
echo "Running backend tests..."
if ./gradlew test --no-daemon; then
    print_status "All backend tests passed"
else
    print_error "Backend tests failed"
    exit 1
fi

cd ..

echo ""
echo "2. Frontend Security Audit"
echo "=========================="

cd frontend

# Run npm audit
echo "Running Bun security audit..."
if bun audit; then
    print_status "No frontend security vulnerabilities found"
else
    print_warning "Frontend security issues found - run 'bun audit --fix'"
fi

# Run linting
echo "Running ESLint..."
if bun run lint; then
    print_status "Frontend linting passed"
else
    print_warning "Frontend linting issues found"
fi

# Build frontend
echo "Building frontend..."
if bun run build; then
    print_status "Frontend build successful"
else
    print_error "Frontend build failed"
    exit 1
fi

cd ..

echo ""
echo "3. Docker Security Check"
echo "========================"

# Check Docker configuration
echo "Validating Docker configuration..."
if docker-compose config > /dev/null 2>&1; then
    print_status "Docker Compose configuration is valid"
else
    print_error "Docker Compose configuration has errors"
    exit 1
fi

echo ""
echo "4. Environment Security Check"
echo "============================="

# Check for .env file
if [ -f ".env" ]; then
    print_status ".env file exists"
    
    # Check for required environment variables
    required_vars=("GOOGLE_CLIENT_ID" "GOOGLE_CLIENT_SECRET" "GITHUB_CLIENT_ID" "GITHUB_CLIENT_SECRET" "JWT_SECRET")
    
    for var in "${required_vars[@]}"; do
        if grep -q "^${var}=" .env; then
            print_status "$var is configured"
        else
            print_warning "$var is not configured in .env"
        fi
    done
else
    print_warning ".env file not found - copy .env.example to .env"
fi

echo ""
echo "5. Security Configuration Review"
echo "==============================="

# Check Spring Boot version
spring_version=$(grep 'id("org.springframework.boot")' backend/build.gradle.kts | grep -o '[0-9]\+\.[0-9]\+\.[0-9]\+')
print_status "Spring Boot version: $spring_version"

# Check Angular version
angular_version=$(grep '"@angular/core"' frontend/package.json | grep -o '[0-9]\+\.[0-9]\+\.[0-9]\+')
print_status "Angular version: $angular_version"

echo ""
echo "🎉 Security Audit Complete!"
echo "=========================="
print_status "Security audit completed successfully"
print_status "Check the generated reports in:"
print_status "  - Backend: backend/build/reports/"
print_status "  - Frontend: frontend/dist/"

echo ""
echo "📋 Next Steps:"
echo "- Review OWASP dependency check report"
echo "- Address any linting issues"
echo "- Ensure all environment variables are properly configured"
echo "- Run regular security audits as part of CI/CD pipeline"