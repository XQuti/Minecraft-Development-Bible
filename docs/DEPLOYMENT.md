# Deployment Guide

## Overview

This guide covers deployment options for the Minecraft Development Bible (MDB) application, including Docker-based deployment, cloud deployment, and production configuration.

## Prerequisites

- Docker and Docker Compose
- PostgreSQL 15+
- Redis 7+
- SSL certificates for HTTPS
- Domain name (for production)

## Environment Configuration

### Required Environment Variables

Create a `.env` file in the project root:

```bash
# Database Configuration
DB_HOST=postgres
DB_PORT=5432
DB_NAME=minecraft_dev_bible
DB_USERNAME=mdb_user
DB_PASSWORD=your-secure-database-password

# Redis Configuration
REDIS_HOST=redis
REDIS_PORT=6379
REDIS_PASSWORD=your-redis-password

# JWT Configuration
JWT_SECRET=your-secure-jwt-secret-key-minimum-32-characters-long
JWT_EXPIRATION=86400000

# OAuth2 Configuration
GOOGLE_CLIENT_ID=your-google-oauth-client-id
GOOGLE_CLIENT_SECRET=your-google-oauth-client-secret
GITHUB_CLIENT_ID=your-github-oauth-client-id
GITHUB_CLIENT_SECRET=your-github-oauth-client-secret

# CORS Configuration
ALLOWED_ORIGINS=https://yourdomain.com,https://www.yourdomain.com

# Application URLs
FRONTEND_URL=https://yourdomain.com
BACKEND_URL=https://api.yourdomain.com

# SSL Configuration (for production)
SSL_CERT_PATH=/etc/ssl/certs/yourdomain.crt
SSL_KEY_PATH=/etc/ssl/private/yourdomain.key
```

## Docker Deployment

### Development Deployment

```bash
# Clone the repository
git clone <repository-url>
cd MDB

# Copy environment template
cp .env.example .env
# Edit .env with your configuration

# Start services
docker-compose up -d

# Check service status
docker-compose ps

# View logs
docker-compose logs -f
```

### Production Deployment

1. **Prepare production environment file**:
```bash
cp .env.example .env.production
# Edit with production values
```

2. **Build and deploy**:
```bash
# Build production images
docker-compose -f docker-compose.yml -f docker-compose.prod.yml build

# Deploy to production
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```

3. **Initialize database**:
```bash
# Run database migrations
docker-compose exec backend java -jar app.jar --spring.jpa.hibernate.ddl-auto=create
```

## Cloud Deployment

### AWS Deployment

#### Using AWS ECS

1. **Create ECR repositories**:
```bash
aws ecr create-repository --repository-name mdb-backend
aws ecr create-repository --repository-name mdb-frontend
```

2. **Build and push images**:
```bash
# Get login token
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin <account-id>.dkr.ecr.us-east-1.amazonaws.com

# Build and tag images
docker build -t mdb-backend ./backend
docker tag mdb-backend:latest <account-id>.dkr.ecr.us-east-1.amazonaws.com/mdb-backend:latest

docker build -t mdb-frontend ./frontend
docker tag mdb-frontend:latest <account-id>.dkr.ecr.us-east-1.amazonaws.com/mdb-frontend:latest

# Push images
docker push <account-id>.dkr.ecr.us-east-1.amazonaws.com/mdb-backend:latest
docker push <account-id>.dkr.ecr.us-east-1.amazonaws.com/mdb-frontend:latest
```

3. **Create ECS task definitions and services** (use AWS Console or CLI)

#### Using AWS Elastic Beanstalk

1. **Prepare Dockerrun.aws.json**:
```json
{
  "AWSEBDockerrunVersion": 2,
  "containerDefinitions": [
    {
      "name": "backend",
      "image": "<account-id>.dkr.ecr.us-east-1.amazonaws.com/mdb-backend:latest",
      "memory": 512,
      "portMappings": [
        {
          "hostPort": 8080,
          "containerPort": 8080
        }
      ]
    },
    {
      "name": "frontend",
      "image": "<account-id>.dkr.ecr.us-east-1.amazonaws.com/mdb-frontend:latest",
      "memory": 256,
      "portMappings": [
        {
          "hostPort": 80,
          "containerPort": 80
        }
      ]
    }
  ]
}
```

### Google Cloud Platform

#### Using Google Cloud Run

1. **Build and push to Container Registry**:
```bash
# Configure Docker for GCP
gcloud auth configure-docker

# Build and push backend
docker build -t gcr.io/your-project-id/mdb-backend ./backend
docker push gcr.io/your-project-id/mdb-backend

# Build and push frontend
docker build -t gcr.io/your-project-id/mdb-frontend ./frontend
docker push gcr.io/your-project-id/mdb-frontend
```

2. **Deploy to Cloud Run**:
```bash
# Deploy backend
gcloud run deploy mdb-backend \
  --image gcr.io/your-project-id/mdb-backend \
  --platform managed \
  --region us-central1 \
  --allow-unauthenticated

# Deploy frontend
gcloud run deploy mdb-frontend \
  --image gcr.io/your-project-id/mdb-frontend \
  --platform managed \
  --region us-central1 \
  --allow-unauthenticated
```

## Database Setup

### PostgreSQL Configuration

1. **Create database and user**:
```sql
CREATE DATABASE minecraft_dev_bible;
CREATE USER mdb_user WITH PASSWORD 'your-secure-password';
GRANT ALL PRIVILEGES ON DATABASE minecraft_dev_bible TO mdb_user;
```

2. **Configure connection pooling** (recommended for production):
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
```

### Redis Configuration

1. **Configure Redis for production**:
```bash
# In redis.conf
bind 127.0.0.1
port 6379
requirepass your-redis-password
maxmemory 256mb
maxmemory-policy allkeys-lru
```

## SSL/TLS Configuration

### Using Let's Encrypt with Nginx

1. **Install Certbot**:
```bash
sudo apt-get install certbot python3-certbot-nginx
```

2. **Obtain SSL certificate**:
```bash
sudo certbot --nginx -d yourdomain.com -d www.yourdomain.com
```

3. **Configure auto-renewal**:
```bash
sudo crontab -e
# Add: 0 12 * * * /usr/bin/certbot renew --quiet
```

### Manual SSL Configuration

Update `nginx.conf` in the frontend:
```nginx
server {
    listen 443 ssl http2;
    server_name yourdomain.com;
    
    ssl_certificate /etc/ssl/certs/yourdomain.crt;
    ssl_certificate_key /etc/ssl/private/yourdomain.key;
    
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers ECDHE-RSA-AES256-GCM-SHA512:DHE-RSA-AES256-GCM-SHA512;
    ssl_prefer_server_ciphers off;
    
    # Your existing configuration
}
```

## Monitoring and Logging

### Application Monitoring

1. **Configure Spring Boot Actuator**:
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: when-authorized
```

2. **Set up Prometheus and Grafana** (optional):
```yaml
# docker-compose.monitoring.yml
version: '3.8'
services:
  prometheus:
    image: prom/prometheus
    ports:
      - "9090:9090"
    volumes:
      - ./monitoring/prometheus.yml:/etc/prometheus/prometheus.yml
  
  grafana:
    image: grafana/grafana
    ports:
      - "3000:3000"
    environment:
      - GF_SECURITY_ADMIN_PASSWORD=admin
```

### Logging Configuration

1. **Configure structured logging**:
```yaml
logging:
  level:
    io.xquti.mdb: INFO
    org.springframework.security: WARN
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: /var/log/mdb/application.log
```

## Performance Optimization

### Backend Optimization

1. **JVM Configuration**:
```bash
JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
```

2. **Database Connection Pooling**:
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
```

### Frontend Optimization

1. **Enable gzip compression** in nginx:
```nginx
gzip on;
gzip_vary on;
gzip_min_length 1024;
gzip_types text/plain text/css application/json application/javascript text/xml application/xml;
```

2. **Configure caching headers**:
```nginx
location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
    expires 1y;
    add_header Cache-Control "public, immutable";
}
```

## Security Hardening

### Network Security

1. **Configure firewall**:
```bash
# Allow only necessary ports
sudo ufw allow 22/tcp
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw enable
```

2. **Use Docker secrets** for sensitive data:
```yaml
services:
  backend:
    secrets:
      - db_password
      - jwt_secret
    environment:
      DB_PASSWORD_FILE: /run/secrets/db_password
      JWT_SECRET_FILE: /run/secrets/jwt_secret

secrets:
  db_password:
    file: ./secrets/db_password.txt
  jwt_secret:
    file: ./secrets/jwt_secret.txt
```

### Application Security

1. **Configure security headers**:
```yaml
server:
  servlet:
    session:
      cookie:
        secure: true
        http-only: true
        same-site: strict
```

2. **Enable HTTPS redirect**:
```yaml
server:
  ssl:
    enabled: true
  port: 8443
```

## Backup and Recovery

### Database Backup

1. **Automated backup script**:
```bash
#!/bin/bash
BACKUP_DIR="/backups"
DATE=$(date +%Y%m%d_%H%M%S)
pg_dump -h localhost -U mdb_user minecraft_dev_bible > $BACKUP_DIR/mdb_backup_$DATE.sql
```

2. **Schedule backups**:
```bash
# Add to crontab
0 2 * * * /path/to/backup-script.sh
```

### Application Data Backup

1. **Backup user uploads and data**:
```bash
#!/bin/bash
tar -czf /backups/app_data_$(date +%Y%m%d).tar.gz /app/data
```

## Troubleshooting

### Common Issues

1. **Database connection issues**:
   - Check database credentials
   - Verify network connectivity
   - Check firewall rules

2. **OAuth2 authentication failures**:
   - Verify OAuth2 client credentials
   - Check redirect URIs
   - Ensure HTTPS in production

3. **Memory issues**:
   - Monitor JVM heap usage
   - Adjust memory limits
   - Check for memory leaks

### Health Checks

1. **Backend health check**:
```bash
curl http://localhost:8080/actuator/health
```

2. **Frontend health check**:
```bash
curl http://localhost:4200/
```

### Log Analysis

1. **Check application logs**:
```bash
docker-compose logs -f backend
docker-compose logs -f frontend
```

2. **Monitor system resources**:
```bash
docker stats
```

## Maintenance

### Regular Maintenance Tasks

1. **Update dependencies** (monthly)
2. **Review security logs** (weekly)
3. **Monitor performance metrics** (daily)
4. **Test backup restoration** (quarterly)
5. **Security vulnerability scanning** (weekly)

### Update Procedure

1. **Test updates in staging environment**
2. **Create backup before updates**
3. **Deploy during maintenance window**
4. **Monitor application after deployment**
5. **Rollback if issues occur**

## Support

For deployment support and questions:
- Documentation: [Project Wiki]
- Issues: [GitHub Issues]
- Email: support@xquti.io