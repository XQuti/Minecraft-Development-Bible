#!/bin/bash

# MDB Production Backup Script
# This script creates backups of the database and application data

set -e

# Configuration
BACKUP_DIR="/var/backups/mdb"
RETENTION_DAYS=30
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")

# Create backup directory if it doesn't exist
mkdir -p "$BACKUP_DIR"

echo "========================================="
echo "  MDB Production Backup - $TIMESTAMP"
echo "========================================="

# Function to log messages
log() {
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] $1"
}

# Function to cleanup old backups
cleanup_old_backups() {
    log "Cleaning up backups older than $RETENTION_DAYS days..."
    find "$BACKUP_DIR" -name "*.tar.gz" -mtime +$RETENTION_DAYS -delete
    find "$BACKUP_DIR" -name "*.sql" -mtime +$RETENTION_DAYS -delete
}

# Function to backup PostgreSQL database
backup_postgres() {
    log "Backing up PostgreSQL database..."
    
    # Get database connection details from environment
    source .env
    
    # Create database backup
    docker exec mdb-postgres pg_dump -U "$DB_USERNAME" -d "$DB_NAME" > "$BACKUP_DIR/postgres_backup_$TIMESTAMP.sql"
    
    # Compress the backup
    gzip "$BACKUP_DIR/postgres_backup_$TIMESTAMP.sql"
    
    log "PostgreSQL backup completed: postgres_backup_$TIMESTAMP.sql.gz"
}

# Function to backup Redis data
backup_redis() {
    log "Backing up Redis data..."
    
    # Create Redis backup by copying the dump file
    docker exec mdb-redis redis-cli BGSAVE
    sleep 5  # Wait for background save to complete
    
    docker cp mdb-redis:/data/dump.rdb "$BACKUP_DIR/redis_backup_$TIMESTAMP.rdb"
    gzip "$BACKUP_DIR/redis_backup_$TIMESTAMP.rdb"
    
    log "Redis backup completed: redis_backup_$TIMESTAMP.rdb.gz"
}

# Function to backup Elasticsearch data
backup_elasticsearch() {
    log "Backing up Elasticsearch data..."
    
    # Create Elasticsearch snapshot (requires snapshot repository to be configured)
    # For now, we'll backup the data directory
    docker exec mdb-elasticsearch tar -czf /tmp/elasticsearch_backup_$TIMESTAMP.tar.gz /usr/share/elasticsearch/data
    docker cp mdb-elasticsearch:/tmp/elasticsearch_backup_$TIMESTAMP.tar.gz "$BACKUP_DIR/"
    docker exec mdb-elasticsearch rm /tmp/elasticsearch_backup_$TIMESTAMP.tar.gz
    
    log "Elasticsearch backup completed: elasticsearch_backup_$TIMESTAMP.tar.gz"
}

# Function to backup application files
backup_application() {
    log "Backing up application files..."
    
    # Backup configuration files and scripts
    tar -czf "$BACKUP_DIR/application_backup_$TIMESTAMP.tar.gz" \
        --exclude='node_modules' \
        --exclude='build' \
        --exclude='dist' \
        --exclude='.git' \
        --exclude='*.log' \
        .
    
    log "Application backup completed: application_backup_$TIMESTAMP.tar.gz"
}

# Function to create backup manifest
create_manifest() {
    log "Creating backup manifest..."
    
    cat > "$BACKUP_DIR/backup_manifest_$TIMESTAMP.txt" <<EOF
MDB Backup Manifest
==================
Timestamp: $TIMESTAMP
Date: $(date)
Hostname: $(hostname)

Files included in this backup:
- postgres_backup_$TIMESTAMP.sql.gz (PostgreSQL database)
- redis_backup_$TIMESTAMP.rdb.gz (Redis cache data)
- elasticsearch_backup_$TIMESTAMP.tar.gz (Elasticsearch search data)
- application_backup_$TIMESTAMP.tar.gz (Application files and configuration)

Backup location: $BACKUP_DIR
Retention policy: $RETENTION_DAYS days

To restore:
1. Stop the application: docker-compose down
2. Restore database: gunzip -c postgres_backup_$TIMESTAMP.sql.gz | docker exec -i mdb-postgres psql -U postgres -d mdb
3. Restore Redis: gunzip -c redis_backup_$TIMESTAMP.rdb.gz > dump.rdb && docker cp dump.rdb mdb-redis:/data/
4. Restore Elasticsearch: docker cp elasticsearch_backup_$TIMESTAMP.tar.gz mdb-elasticsearch:/tmp/ && docker exec mdb-elasticsearch tar -xzf /tmp/elasticsearch_backup_$TIMESTAMP.tar.gz -C /
5. Restore application: tar -xzf application_backup_$TIMESTAMP.tar.gz
6. Start the application: docker-compose up -d
EOF

    log "Backup manifest created: backup_manifest_$TIMESTAMP.txt"
}

# Function to verify backup integrity
verify_backups() {
    log "Verifying backup integrity..."
    
    # Check if all backup files exist and are not empty
    local errors=0
    
    for file in "postgres_backup_$TIMESTAMP.sql.gz" "redis_backup_$TIMESTAMP.rdb.gz" "elasticsearch_backup_$TIMESTAMP.tar.gz" "application_backup_$TIMESTAMP.tar.gz"; do
        if [ ! -f "$BACKUP_DIR/$file" ] || [ ! -s "$BACKUP_DIR/$file" ]; then
            log "ERROR: Backup file $file is missing or empty"
            errors=$((errors + 1))
        else
            log "✓ $file verified"
        fi
    done
    
    if [ $errors -eq 0 ]; then
        log "All backups verified successfully"
        return 0
    else
        log "Backup verification failed with $errors errors"
        return 1
    fi
}

# Function to send notification (placeholder for email/Slack integration)
send_notification() {
    local status=$1
    local message=$2
    
    # Log the notification (extend this to send actual notifications)
    log "NOTIFICATION [$status]: $message"
    
    # Example: Send email notification (uncomment and configure)
    # echo "$message" | mail -s "MDB Backup $status" admin@example.com
    
    # Example: Send Slack notification (uncomment and configure)
    # curl -X POST -H 'Content-type: application/json' \
    #     --data "{\"text\":\"MDB Backup $status: $message\"}" \
    #     YOUR_SLACK_WEBHOOK_URL
}

# Main backup process
main() {
    log "Starting MDB backup process..."
    
    # Check if Docker containers are running
    if ! docker ps | grep -q mdb-postgres; then
        log "ERROR: PostgreSQL container is not running"
        send_notification "FAILED" "PostgreSQL container is not running"
        exit 1
    fi
    
    # Perform backups
    backup_postgres
    backup_redis
    backup_elasticsearch
    backup_application
    create_manifest
    
    # Verify backups
    if verify_backups; then
        log "Backup process completed successfully"
        send_notification "SUCCESS" "Backup completed successfully at $TIMESTAMP"
    else
        log "Backup process completed with errors"
        send_notification "WARNING" "Backup completed with verification errors at $TIMESTAMP"
        exit 1
    fi
    
    # Cleanup old backups
    cleanup_old_backups
    
    # Display backup summary
    log "Backup summary:"
    log "Location: $BACKUP_DIR"
    log "Files created:"
    ls -lh "$BACKUP_DIR"/*_$TIMESTAMP.*
    
    log "Backup process finished"
}

# Run main function
main "$@"