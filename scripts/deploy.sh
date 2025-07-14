#!/bin/bash

# MDB Production Deployment Script
# This script handles production deployment with security checks

set -e

echo "🚀 Starting MDB Production Deployment..."
echo "========================================"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
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

# Check for required environment variables
print_info "Checking environment configuration..."

if [ ! -f ".env" ]; then
    print_error ".env file not found. Please copy .env.example to .env and configure it."
    exit 1
fi

required_vars=("GOOGLE_CLIENT_ID" "GOOGLE_CLIENT_SECRET" "GITHUB_CLIENT_ID" "GITHUB_CLIENT_SECRET" "JWT_SECRET" "DB_PASSWORD")

for var in "${required_vars[@]}"; do
    if ! grep -q "^${var}=" .env || grep -q "^${var}=$" .env; then
        print_error "$var is not configured in .env file"
        exit 1
    fi
done

print_status "Environment configuration validated"

# Run security audit
print_info "Running security audit..."
if [ -f "scripts/security-audit.sh" ]; then
    chmod +x scripts/security-audit.sh
    if ./scripts/security-audit.sh; then
        print_status "Security audit passed"
    else
        print_error "Security audit failed"
        exit 1
    fi
else
    print_warning "Security audit script not found - skipping"
fi

# Build and test backend
print_info "Building and testing backend..."
cd backend

if ./gradlew clean build --no-daemon; then
    print_status "Backend build successful"
else
    print_error "Backend build failed"
    exit 1
fi

cd ..

# Build frontend
print_info "Building frontend..."
cd frontend

if bun install && bun run build:prod; then
    print_status "Frontend build successful"
else
    print_error "Frontend build failed"
    exit 1
fi

cd ..

# Docker deployment
print_info "Starting Docker deployment..."

# Stop existing containers
print_info "Stopping existing containers..."
docker-compose down --remove-orphans

# Build new images
print_info "Building Docker images..."
if docker-compose build --no-cache; then
    print_status "Docker images built successfully"
else
    print_error "Docker image build failed"
    exit 1
fi

# Start services
print_info "Starting services..."
if docker-compose up -d; then
    print_status "Services started successfully"
else
    print_error "Failed to start services"
    exit 1
fi

# Wait for services to be ready
print_info "Waiting for services to be ready..."
sleep 30

# Health checks
print_info "Performing health checks..."

# Check backend health
if curl -f http://localhost:8080/actuator/health > /dev/null 2>&1; then
    print_status "Backend health check passed"
else
    print_error "Backend health check failed"
    docker-compose logs backend
    exit 1
fi

# Check frontend
if curl -f http://localhost:4200 > /dev/null 2>&1; then
    print_status "Frontend health check passed"
else
    print_error "Frontend health check failed"
    docker-compose logs frontend
    exit 1
fi

# Check database
if docker-compose exec -T postgres pg_isready -U postgres > /dev/null 2>&1; then
    print_status "Database health check passed"
else
    print_error "Database health check failed"
    docker-compose logs postgres
    exit 1
fi

# Check Redis
if docker-compose exec -T redis redis-cli ping > /dev/null 2>&1; then
    print_status "Redis health check passed"
else
    print_error "Redis health check failed"
    docker-compose logs redis
    exit 1
fi

echo ""
echo "🎉 Deployment Complete!"
echo "======================"
print_status "MDB application deployed successfully"
print_info "Application URLs:"
print_info "  - Frontend: http://localhost:4200"
print_info "  - Backend API: http://localhost:8080"
print_info "  - API Documentation: http://localhost:8080/swagger-ui.html"
print_info "  - Health Check: http://localhost:8080/actuator/health"

echo ""
print_info "Monitoring commands:"
print_info "  - View logs: docker-compose logs -f"
print_info "  - Check status: docker-compose ps"
print_info "  - Stop services: docker-compose down"

echo ""
print_warning "Post-deployment checklist:"
print_warning "  - Verify all OAuth2 providers are working"
print_warning "  - Test user registration and login"
print_warning "  - Check forum functionality"
print_warning "  - Monitor application logs for errors"
print_warning "  - Set up monitoring and alerting"