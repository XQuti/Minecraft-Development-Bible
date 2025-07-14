#!/bin/bash

# MDB Production Setup Script
# This script sets up the production environment for the Minecraft Development Bible

set -e

echo "========================================="
echo "  MDB Production Environment Setup"
echo "========================================="
echo

# Check if running as root
if [[ $EUID -eq 0 ]]; then
   echo "This script should not be run as root for security reasons"
   exit 1
fi

# Function to check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Function to install Docker if not present
install_docker() {
    echo "Installing Docker..."
    curl -fsSL https://get.docker.com -o get-docker.sh
    sudo sh get-docker.sh
    sudo usermod -aG docker $USER
    rm get-docker.sh
    echo "Docker installed. Please log out and log back in for group changes to take effect."
}

# Function to install Docker Compose if not present
install_docker_compose() {
    echo "Installing Docker Compose..."
    sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
    sudo chmod +x /usr/local/bin/docker-compose
}

# Check system requirements
echo "Checking system requirements..."

# Check Docker
if ! command_exists docker; then
    echo "Docker not found. Installing..."
    install_docker
else
    echo "✓ Docker found: $(docker --version)"
fi

# Check Docker Compose
if ! command_exists docker-compose; then
    echo "Docker Compose not found. Installing..."
    install_docker_compose
else
    echo "✓ Docker Compose found: $(docker-compose --version)"
fi

# Check if .env file exists
if [ ! -f ".env" ]; then
    echo "Creating production .env file from template..."
    cp .env.example .env
    echo
    echo "⚠️  IMPORTANT: Please edit .env file with production values:"
    echo "   - Set strong JWT_SECRET (minimum 32 characters)"
    echo "   - Configure OAuth2 client IDs and secrets"
    echo "   - Set production database credentials"
    echo "   - Configure allowed origins for CORS"
    echo
    read -p "Press Enter to continue after editing .env file..."
fi

# Validate critical environment variables
echo "Validating environment configuration..."
source .env

if [ ${#JWT_SECRET} -lt 32 ]; then
    echo "❌ JWT_SECRET must be at least 32 characters long"
    exit 1
fi

if [ "$GOOGLE_CLIENT_ID" = "your-google-client-id" ]; then
    echo "⚠️  Warning: Google OAuth2 not configured"
fi

if [ "$GITHUB_CLIENT_ID" = "your-github-client-id" ]; then
    echo "⚠️  Warning: GitHub OAuth2 not configured"
fi

# Create production directories
echo "Creating production directories..."
sudo mkdir -p /var/log/mdb
sudo mkdir -p /var/lib/mdb/postgres
sudo mkdir -p /var/lib/mdb/redis
sudo mkdir -p /var/lib/mdb/elasticsearch
sudo chown -R $USER:$USER /var/lib/mdb
sudo chown -R $USER:$USER /var/log/mdb

# Set up log rotation
echo "Setting up log rotation..."
sudo tee /etc/logrotate.d/mdb > /dev/null <<EOF
/var/log/mdb/*.log {
    daily
    missingok
    rotate 30
    compress
    delaycompress
    notifempty
    create 644 $USER $USER
}
EOF

# Build production images
echo "Building production Docker images..."
docker-compose -f docker-compose.yml build --no-cache

# Set up systemd service for auto-start
echo "Setting up systemd service..."
sudo tee /etc/systemd/system/mdb.service > /dev/null <<EOF
[Unit]
Description=Minecraft Development Bible
Requires=docker.service
After=docker.service

[Service]
Type=oneshot
RemainAfterExit=yes
WorkingDirectory=$(pwd)
ExecStart=/usr/local/bin/docker-compose up -d
ExecStop=/usr/local/bin/docker-compose down
TimeoutStartSec=0
User=$USER
Group=$USER

[Install]
WantedBy=multi-user.target
EOF

sudo systemctl daemon-reload
sudo systemctl enable mdb.service

# Set up firewall rules (if ufw is available)
if command_exists ufw; then
    echo "Configuring firewall..."
    sudo ufw allow 80/tcp
    sudo ufw allow 443/tcp
    sudo ufw allow 22/tcp
    echo "Firewall configured to allow HTTP, HTTPS, and SSH"
fi

# Set up SSL certificate placeholder (user should configure with Let's Encrypt)
echo "Setting up SSL certificate directory..."
mkdir -p ssl
echo "⚠️  Remember to configure SSL certificates for production"
echo "   Recommended: Use Let's Encrypt with certbot"

# Final security checks
echo "Running final security checks..."

# Check file permissions
find . -name "*.sh" -exec chmod +x {} \;
chmod 600 .env

# Check for default passwords
if grep -q "password" .env; then
    echo "⚠️  Warning: Default passwords detected in .env file"
fi

echo
echo "========================================="
echo "  Production Setup Complete!"
echo "========================================="
echo
echo "Next steps:"
echo "1. Review and update .env file with production values"
echo "2. Configure SSL certificates (recommended: Let's Encrypt)"
echo "3. Set up monitoring and alerting"
echo "4. Configure backup strategy"
echo "5. Start the application: sudo systemctl start mdb"
echo
echo "To start the application now:"
echo "  docker-compose up -d"
echo
echo "To check status:"
echo "  docker-compose ps"
echo "  docker-compose logs -f"
echo
echo "Application will be available at:"
echo "  Frontend: http://localhost:4200"
echo "  Backend API: http://localhost:8080"
echo