#!/bin/bash

# Health check script for the deployed application
# This script can be run to verify that all services are healthy

set -e

REMOTE_USER="${REMOTE_USER:-opc}"
REMOTE_HOST="${REMOTE_HOST}"
REMOTE_PATH="${REMOTE_PATH:-/opt/myapp}"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

log() {
    echo -e "${GREEN}[$(date +'%Y-%m-%d %H:%M:%S')] $1${NC}"
}

warn() {
    echo -e "${YELLOW}[$(date +'%Y-%m-%d %H:%M:%S')] WARNING: $1${NC}"
}

error() {
    echo -e "${RED}[$(date +'%Y-%m-%d %H:%M:%S')] ERROR: $1${NC}"
}

check_service_health() {
    log "Checking application health on $REMOTE_HOST..."
    
    ssh "$REMOTE_USER@$REMOTE_HOST" << 'EOF'
        set -e
        cd /opt/myapp
        
        echo "=== Container Status ==="
        docker compose ps
        
        echo -e "\n=== Service Health Checks ==="
        
        # Check database
        echo "Checking database health..."
        if docker compose exec -T db pg_isready -U postgres; then
            echo "✓ Database is healthy"
        else
            echo "✗ Database is not healthy"
        fi
        
        # Check API
        echo "Checking API health..."
        if docker compose exec -T api curl -f http://localhost:8080/actuator/health 2>/dev/null; then
            echo "✓ API is healthy"
        else
            echo "✗ API is not healthy"
        fi
        
        # Check frontend
        echo "Checking frontend health..."
        if docker compose exec -T react-frontend curl -f http://localhost:80 2>/dev/null; then
            echo "✓ Frontend is healthy"
        else
            echo "✗ Frontend is not healthy"
        fi
        
        echo -e "\n=== Resource Usage ==="
        docker stats --no-stream --format "table {{.Container}}\t{{.CPUPerc}}\t{{.MemUsage}}\t{{.NetIO}}"
        
        echo -e "\n=== Recent Logs (last 20 lines) ==="
        docker compose logs --tail=20
EOF
    
    log "Health check completed"
}

# Validate required environment variables
if [ -z "$REMOTE_HOST" ]; then
    error "REMOTE_HOST environment variable is required"
    exit 1
fi

check_service_health
