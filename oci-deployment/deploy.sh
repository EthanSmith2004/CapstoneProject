#!/bin/bash

# OCI Deployment Script
# This script handles the automated deployment of the application to OCI

set -e

# Configuration
REMOTE_USER="${REMOTE_USER:-ubuntu}"
REMOTE_HOST="${REMOTE_HOST}"
REMOTE_PATH="${REMOTE_PATH:-/opt/myapp}"
BACKEND_IMAGE="${BACKEND_IMAGE}"
FRONTEND_IMAGE="${FRONTEND_IMAGE}"

log() {
    echo -e "[$(date +'%Y-%m-%d %H:%M:%S')] $1"
}

warn() {
    echo -e "[$(date +'%Y-%m-%d %H:%M:%S')] WARNING: $1"
}

error() {
    echo -e "[$(date +'%Y-%m-%d %H:%M:%S')] ERROR: $1"
    exit 1
}

# Validate required environment variables
validate_env() {
    log "Validating environment variables..."
    
    if [ -z "$REMOTE_HOST" ]; then
        error "REMOTE_HOST environment variable is required"
    fi
    
    if [ -z "$BACKEND_IMAGE" ]; then
        error "BACKEND_IMAGE environment variable is required"
    fi
    
    if [ -z "$FRONTEND_IMAGE" ]; then
        error "FRONTEND_IMAGE environment variable is required"
    fi
    
    log "Environment validation passed"
}

# Test SSH connectivity
test_ssh() {
    log "Testing SSH connectivity to $REMOTE_USER@$REMOTE_HOST..."
    
    if ! ssh -o BatchMode=yes -o ConnectTimeout=10 "$REMOTE_USER@$REMOTE_HOST" "echo 'SSH connection successful'"; then
        error "Failed to connect to remote host. Please ensure SSH key is configured."
    fi
    
    log "SSH connectivity test passed"
}

# Prepare deployment files
prepare_deployment() {
    # Create temporary directory for deployment files
    TEMP_DIR=$(mktemp -d)
    
    # Copy docker-compose.yml and other necessary files
    cp docker-compose.yml "$TEMP_DIR/"
    cp .env.example "$TEMP_DIR/"
    cp -r monitoring "$TEMP_DIR/monitoring"
    
    # Create deployment-specific docker-compose override
    cat > "$TEMP_DIR/docker-compose.override.yml" << EOF
version: '3.8'
services:
  api:
    image: $BACKEND_IMAGE
  frontend:
    image: $FRONTEND_IMAGE
EOF
    
    echo "$TEMP_DIR"
}

# Deploy to remote server
deploy_to_remote() {
    local temp_dir="$1"
    
    log "Deploying to remote server $REMOTE_HOST..."
    
    # Create remote directory structure if it doesn't exist
    log "Creating remote directory structure..."
    ssh "$REMOTE_USER@$REMOTE_HOST" << EOF
        set -e
        
        # Create main directory with sudo, then change ownership
        sudo mkdir -p $REMOTE_PATH
        sudo chown $REMOTE_USER:$REMOTE_USER $REMOTE_PATH
        
        # Create monitoring subdirectories
        mkdir -p $REMOTE_PATH/monitoring/prometheus
        mkdir -p $REMOTE_PATH/monitoring/loki
        mkdir -p $REMOTE_PATH/monitoring/promtail
        mkdir -p $REMOTE_PATH/monitoring/grafana/provisioning/datasources
        mkdir -p $REMOTE_PATH/monitoring/grafana/provisioning/dashboards
        
        # Create init-db directory if needed
        mkdir -p $REMOTE_PATH/init-db
        
        echo "Remote directory structure created successfully"
EOF
    
    # Copy files to remote server
    log "Copying deployment files to remote server..."
    scp -rv "$temp_dir"/. "$REMOTE_USER@$REMOTE_HOST:$REMOTE_PATH/"
    
    # Execute deployment on remote server
    log "Executing deployment on remote server..."
    ssh "$REMOTE_USER@$REMOTE_HOST" << EOF
        set -e
        cd $REMOTE_PATH
        
        # Create .env file if it doesn't exist
        if [ ! -f .env ]; then
            echo "Creating .env file from example..."
            cp .env.example .env
            echo "Please update the .env file with your configuration"
        fi
        
        # Pull latest images
        echo "Pulling latest Docker images..."
        docker compose pull
        
        # Stop existing containers
        echo "Stopping existing containers..."
        docker compose down || true
        
        # Start new containers
        echo "Starting new containers..."
        docker compose up -d
        
        # Wait for services to be healthy
        echo "Waiting for services to be healthy..."
        timeout 300 docker compose exec -T api curl -f http://localhost:8080/actuator/health || echo "API health check timeout"
        timeout 60 docker compose exec -T react-frontend curl -f http://localhost:80 || echo "Frontend health check timeout"
        
        # Show status
        echo "Deployment completed. Service status:"
        docker compose ps
EOF
    
    log "Deployment completed successfully"
}

# Cleanup
cleanup() {
    if [ -n "$TEMP_DIR" ] && [ -d "$TEMP_DIR" ]; then
        rm -rf "$TEMP_DIR"
        log "Cleaned up temporary files"
    fi
}

# Main deployment function
main() {
    log "Starting deployment process..."
    
    validate_env
    test_ssh
    
    local temp_dir
    temp_dir=$(prepare_deployment)
    
    trap cleanup EXIT
    
    deploy_to_remote "$temp_dir"
    
    log "Deployment process completed successfully!"
}

# Run main function
main "$@"
