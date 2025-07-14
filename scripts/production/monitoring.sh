#!/bin/bash

# MDB Production Monitoring Script
# This script monitors the health and performance of the MDB application

set -e

# Configuration
LOG_FILE="/var/log/mdb/monitoring.log"
ALERT_EMAIL="admin@example.com"
SLACK_WEBHOOK=""  # Configure your Slack webhook URL
CHECK_INTERVAL=60  # seconds

# Thresholds
CPU_THRESHOLD=80
MEMORY_THRESHOLD=80
DISK_THRESHOLD=85
RESPONSE_TIME_THRESHOLD=5000  # milliseconds

# Create log directory if it doesn't exist
mkdir -p "$(dirname "$LOG_FILE")"

# Function to log messages
log() {
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] $1" | tee -a "$LOG_FILE"
}

# Function to send alert
send_alert() {
    local severity=$1
    local message=$2
    local timestamp=$(date '+%Y-%m-%d %H:%M:%S')
    
    log "ALERT [$severity]: $message"
    
    # Send email alert (configure mail server)
    if command -v mail >/dev/null 2>&1; then
        echo "[$timestamp] MDB Alert [$severity]: $message" | mail -s "MDB Production Alert" "$ALERT_EMAIL"
    fi
    
    # Send Slack alert
    if [ -n "$SLACK_WEBHOOK" ]; then
        curl -X POST -H 'Content-type: application/json' \
            --data "{\"text\":\"🚨 MDB Alert [$severity] at $timestamp: $message\"}" \
            "$SLACK_WEBHOOK" >/dev/null 2>&1
    fi
}

# Function to check Docker container health
check_container_health() {
    log "Checking container health..."
    
    local containers=("mdb-postgres" "mdb-redis" "mdb-elasticsearch" "mdb-backend" "mdb-frontend")
    local unhealthy_containers=()
    
    for container in "${containers[@]}"; do
        if ! docker ps --format "table {{.Names}}\t{{.Status}}" | grep -q "$container.*Up"; then
            unhealthy_containers+=("$container")
        fi
    done
    
    if [ ${#unhealthy_containers[@]} -gt 0 ]; then
        send_alert "CRITICAL" "Containers not running: ${unhealthy_containers[*]}"
        return 1
    else
        log "✓ All containers are running"
        return 0
    fi
}

# Function to check application endpoints
check_endpoints() {
    log "Checking application endpoints..."
    
    # Check backend health endpoint
    local backend_response=$(curl -s -w "%{http_code}:%{time_total}" -o /dev/null http://localhost:8080/actuator/health || echo "000:0")
    local backend_status=$(echo "$backend_response" | cut -d: -f1)
    local backend_time=$(echo "$backend_response" | cut -d: -f2 | cut -d. -f1)
    
    if [ "$backend_status" != "200" ]; then
        send_alert "CRITICAL" "Backend health check failed (HTTP $backend_status)"
        return 1
    fi
    
    if [ "$backend_time" -gt $((RESPONSE_TIME_THRESHOLD / 1000)) ]; then
        send_alert "WARNING" "Backend response time high: ${backend_time}s"
    fi
    
    # Check frontend
    local frontend_response=$(curl -s -w "%{http_code}" -o /dev/null http://localhost:4200 || echo "000")
    if [ "$frontend_response" != "200" ]; then
        send_alert "CRITICAL" "Frontend not accessible (HTTP $frontend_response)"
        return 1
    fi
    
    log "✓ All endpoints are responding"
    return 0
}

# Function to check database connectivity
check_database() {
    log "Checking database connectivity..."
    
    # Check PostgreSQL
    if ! docker exec mdb-postgres pg_isready -U postgres >/dev/null 2>&1; then
        send_alert "CRITICAL" "PostgreSQL database is not ready"
        return 1
    fi
    
    # Check Redis
    if ! docker exec mdb-redis redis-cli ping >/dev/null 2>&1; then
        send_alert "CRITICAL" "Redis cache is not responding"
        return 1
    fi
    
    # Check Elasticsearch
    local es_health=$(curl -s http://localhost:9200/_cluster/health | grep -o '"status":"[^"]*"' | cut -d'"' -f4)
    if [ "$es_health" != "green" ] && [ "$es_health" != "yellow" ]; then
        send_alert "CRITICAL" "Elasticsearch cluster health is $es_health"
        return 1
    fi
    
    log "✓ All databases are healthy"
    return 0
}

# Function to check system resources
check_system_resources() {
    log "Checking system resources..."
    
    # Check CPU usage
    local cpu_usage=$(top -bn1 | grep "Cpu(s)" | awk '{print $2}' | cut -d'%' -f1)
    cpu_usage=${cpu_usage%.*}  # Remove decimal part
    
    if [ "$cpu_usage" -gt "$CPU_THRESHOLD" ]; then
        send_alert "WARNING" "High CPU usage: ${cpu_usage}%"
    fi
    
    # Check memory usage
    local memory_usage=$(free | grep Mem | awk '{printf "%.0f", $3/$2 * 100.0}')
    
    if [ "$memory_usage" -gt "$MEMORY_THRESHOLD" ]; then
        send_alert "WARNING" "High memory usage: ${memory_usage}%"
    fi
    
    # Check disk usage
    local disk_usage=$(df / | tail -1 | awk '{print $5}' | cut -d'%' -f1)
    
    if [ "$disk_usage" -gt "$DISK_THRESHOLD" ]; then
        send_alert "WARNING" "High disk usage: ${disk_usage}%"
    fi
    
    log "✓ System resources: CPU ${cpu_usage}%, Memory ${memory_usage}%, Disk ${disk_usage}%"
}

# Function to check log files for errors
check_logs() {
    log "Checking application logs for errors..."
    
    # Check for recent errors in backend logs
    local error_count=$(docker logs mdb-backend --since="5m" 2>&1 | grep -i "error\|exception\|failed" | wc -l)
    
    if [ "$error_count" -gt 10 ]; then
        send_alert "WARNING" "High error rate in backend logs: $error_count errors in last 5 minutes"
    fi
    
    # Check for database connection errors
    local db_error_count=$(docker logs mdb-backend --since="5m" 2>&1 | grep -i "connection.*failed\|database.*error" | wc -l)
    
    if [ "$db_error_count" -gt 0 ]; then
        send_alert "CRITICAL" "Database connection errors detected: $db_error_count errors"
    fi
    
    log "✓ Log analysis complete: $error_count total errors, $db_error_count database errors"
}

# Function to check SSL certificate expiration
check_ssl_certificate() {
    log "Checking SSL certificate..."
    
    # This is a placeholder - implement based on your SSL setup
    # Example for Let's Encrypt certificates:
    # local cert_expiry=$(openssl x509 -in /etc/letsencrypt/live/yourdomain.com/cert.pem -noout -dates | grep notAfter | cut -d= -f2)
    # local expiry_timestamp=$(date -d "$cert_expiry" +%s)
    # local current_timestamp=$(date +%s)
    # local days_until_expiry=$(( (expiry_timestamp - current_timestamp) / 86400 ))
    
    # if [ "$days_until_expiry" -lt 30 ]; then
    #     send_alert "WARNING" "SSL certificate expires in $days_until_expiry days"
    # fi
    
    log "✓ SSL certificate check skipped (configure based on your setup)"
}

# Function to generate health report
generate_health_report() {
    local timestamp=$(date '+%Y-%m-%d %H:%M:%S')
    local report_file="/var/log/mdb/health_report_$(date +%Y%m%d_%H%M%S).txt"
    
    cat > "$report_file" <<EOF
MDB Health Report
================
Generated: $timestamp
Hostname: $(hostname)

Container Status:
$(docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}")

System Resources:
CPU Usage: $(top -bn1 | grep "Cpu(s)" | awk '{print $2}')
Memory Usage: $(free -h | grep Mem)
Disk Usage: $(df -h /)

Database Status:
PostgreSQL: $(docker exec mdb-postgres pg_isready -U postgres 2>/dev/null && echo "Ready" || echo "Not Ready")
Redis: $(docker exec mdb-redis redis-cli ping 2>/dev/null || echo "Not Responding")
Elasticsearch: $(curl -s http://localhost:9200/_cluster/health | grep -o '"status":"[^"]*"' | cut -d'"' -f4 || echo "Unknown")

Recent Logs (last 100 lines):
$(docker logs mdb-backend --tail=100 2>&1 | tail -20)

EOF

    log "Health report generated: $report_file"
}

# Function to run all checks
run_all_checks() {
    log "Starting health checks..."
    
    local failed_checks=0
    
    check_container_health || failed_checks=$((failed_checks + 1))
    check_endpoints || failed_checks=$((failed_checks + 1))
    check_database || failed_checks=$((failed_checks + 1))
    check_system_resources
    check_logs
    check_ssl_certificate
    
    if [ $failed_checks -eq 0 ]; then
        log "✅ All health checks passed"
    else
        log "❌ $failed_checks health checks failed"
        send_alert "WARNING" "$failed_checks health checks failed"
    fi
    
    return $failed_checks
}

# Function to run continuous monitoring
run_continuous_monitoring() {
    log "Starting continuous monitoring (interval: ${CHECK_INTERVAL}s)"
    
    while true; do
        run_all_checks
        
        # Generate detailed report every hour
        if [ $(($(date +%M) % 60)) -eq 0 ]; then
            generate_health_report
        fi
        
        sleep $CHECK_INTERVAL
    done
}

# Main function
main() {
    case "${1:-check}" in
        "check")
            run_all_checks
            ;;
        "monitor")
            run_continuous_monitoring
            ;;
        "report")
            generate_health_report
            ;;
        *)
            echo "Usage: $0 [check|monitor|report]"
            echo "  check   - Run health checks once"
            echo "  monitor - Run continuous monitoring"
            echo "  report  - Generate health report"
            exit 1
            ;;
    esac
}

# Run main function with all arguments
main "$@"