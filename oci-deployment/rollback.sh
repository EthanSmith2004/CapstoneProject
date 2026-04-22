#!/bin/bash

# Rollback script for reverting to a previous deployment
# Usage: ./rollback.sh [backend_tag] [frontend_tag]

set -e

REMOTE_USER="${REMOTE_USER:-opc}"
REMOTE_HOST="${REMOTE_HOST}"
REMOTE_PATH="${REMOTE_PATH:-/opt/myapp}"
BACKEND_TAG="${1:-latest}"
FRONTEND_TAG="${2:-latest}"
BACKEND_IMAGE="${BACKEND_IMAGE_BASE}:${BACKEND_TAG}"
FRONTEND_IMAGE="${FRONTEND_IMAGE_BASE}:${FRONTEND_TAG}"

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
    exit 1
}

# Validate required environment variables
if [ -z "$REMOTE_HOST" ]; then
    error "REMOTE_HOST environment variable is required"
fi

log "Starting rollback process..."
log "Backend image: $BACKEND_IMAGE"
log "Frontend image: $FRONTEND_IMAGE"

# Confirm rollback
read -p "Are you sure you want to rollback to these versions? (y/N): " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    log "Rollback cancelled"
    exit 0
fi

# Perform rollback
ssh "$REMOTE_USER@$REMOTE_HOST" << EOF
    set -e
    cd $REMOTE_PATH
    
    echo "Creating backup of current docker-compose.override.yml..."
    if [ -f docker-compose.override.yml ]; then
        cp docker-compose.override.yml docker-compose.override.yml.backup.\$(date +%Y%m%d_%H%M%S)
    fi
    
    echo "Creating new override file with rollback images..."
    cat > docker-compose.override.yml << OVERRIDE_EOF
version: '3.8'
services:
  api:
    image: $BACKEND_IMAGE
  react-frontend:
    image: $FRONTEND_IMAGE
OVERRIDE_EOF
    
    echo "Pulling rollback images..."
    docker compose pull
    
    echo "Stopping current containers..."
    docker compose down
    
    echo "Starting containers with rollback images..."
    docker compose up -d
    
    echo "Waiting for services to be healthy..."
    sleep 30
    
    echo "Rollback completed. Service status:"
    docker compose ps
EOF

log "Rollback process completed successfully!"
