# OCI Application Deployment

This repository contains the deployment configuration for automatically deploying a Docker Compose application to Oracle Cloud Infrastructure (OCI).

## Architecture

The application consists of:
- **Database**: PostgreSQL container (`db`)
- **Backend**: Java application container (`api`) - pulls from GHCR
- **Frontend**: React application with Nginx container (`react-frontend`) - pulls from GHCR

## Prerequisites

1. **OCI Instance**: A compute instance with Docker and Docker Compose installed
2. **SSH Access**: SSH key-based authentication to the OCI instance
3. **GitHub Container Registry**: Backend and frontend images pushed to GHCR
4. **GitHub Secrets**: Required secrets configured in the repository

## Required GitHub Secrets

Configure the following secrets in your GitHub repository:

- `OCI_HOST`: The public IP or hostname of your OCI instance
- `OCI_USER`: SSH username (typically `opc` for Oracle Linux)
- `OCI_SSH_PRIVATE_KEY`: Private SSH key for accessing the OCI instance
- `OCI_DEPLOY_PATH`: Path on the OCI instance where the application will be deployed (e.g., `/opt/myapp`)

## Setup Instructions

### 1. Prepare OCI Instance

```bash
# Connect to your OCI instance
ssh opc@your-oci-instance

# Install Docker
sudo dnf install -y docker
sudo systemctl enable docker
sudo systemctl start docker
sudo usermod -aG docker opc

# Install Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Create deployment directory
sudo mkdir -p /opt/myapp
sudo chown opc:opc /opt/myapp
```

### 2. Configure Environment

1. Copy `.env.example` to `.env` on your OCI instance
2. Update the environment variables in `.env`:
   ```bash
   POSTGRES_DB=your_database_name
   POSTGRES_USER=your_db_user
   POSTGRES_PASSWORD=your_secure_password
   ```

### 3. Configure GitHub Repository

1. Add the required secrets to your GitHub repository
2. Update the image names in the workflow files to match your repositories
3. Commit and push the deployment configuration

## Deployment Methods

### Manual Deployment

Use the GitHub Actions workflow for manual deployment:

1. Go to the "Actions" tab in your GitHub repository
2. Select "Deploy to OCI" workflow
3. Click "Run workflow"
4. Choose environment and image tags
5. Click "Run workflow"

### Automatic Deployment

Set up automatic deployment triggered by your backend/frontend repositories:

Add this to your backend repository's workflow:
```yaml
- name: Trigger Deployment
  if: github.ref == 'refs/heads/main'
  run: |
    curl -X POST \
      -H "Authorization: token ${{ secrets.DEPLOY_TOKEN }}" \
      -H "Accept: application/vnd.github.v3+json" \
      https://api.github.com/repos/${{ github.repository_owner }}/oci-deployment/dispatches \
      -d '{"event_type":"deploy-backend","client_payload":{"backend_tag":"${{ github.sha }}"}}'
```

Add this to your frontend repository's workflow:
```yaml
- name: Trigger Deployment
  if: github.ref == 'refs/heads/main'
  run: |
    curl -X POST \
      -H "Authorization: token ${{ secrets.DEPLOY_TOKEN }}" \
      -H "Accept: application/vnd.github.v3+json" \
      https://api.github.com/repos/${{ github.repository_owner }}/oci-deployment/dispatches \
      -d '{"event_type":"deploy-frontend","client_payload":{"frontend_tag":"${{ github.sha }}"}}'
```

### Local Deployment

For local testing or manual deployment:

```bash
# Set environment variables
export REMOTE_HOST=your-oci-instance-ip
export REMOTE_USER=opc
export REMOTE_PATH=/opt/myapp
export BACKEND_IMAGE=ghcr.io/your-org/backend:latest
export FRONTEND_IMAGE=ghcr.io/your-org/frontend:latest

# Run deployment script
chmod +x deploy.sh
./deploy.sh
```

## Application Configuration

### Database Migrations

The Java backend handles database migrations automatically on startup. Ensure your backend application is configured to run migrations when it starts.

### Frontend API Proxy

The React frontend container should be configured to proxy API requests to `/api` to the backend service. Update your Nginx configuration in the frontend Docker image:

```nginx
location /api {
    proxy_pass http://api:8080;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
}
```

## Monitoring and Logs

View application logs:
```bash
# SSH to OCI instance
ssh opc@your-oci-instance

# Navigate to deployment directory
cd /opt/myapp

# View all logs
docker-compose logs

# View specific service logs
docker-compose logs api
docker-compose logs react-frontend
docker-compose logs db

# Follow logs in real-time
docker-compose logs -f
```

Check service status:
```bash
docker-compose ps
```

## Troubleshooting

### Common Issues

1. **SSH Connection Failed**: Ensure SSH key is properly configured and the OCI instance allows SSH connections on port 22
2. **Image Pull Failed**: Verify GHCR credentials and image names are correct
3. **Database Connection Failed**: Check database credentials and ensure the database container is healthy
4. **Port Already in Use**: Stop existing containers: `docker-compose down`

### Rollback

To rollback to a previous version:
```bash
# SSH to OCI instance
ssh opc@your-oci-instance
cd /opt/myapp

# Pull specific image versions
export BACKEND_IMAGE=ghcr.io/your-org/backend:previous-tag
export FRONTEND_IMAGE=ghcr.io/your-org/frontend:previous-tag

# Restart with previous images
docker-compose pull
docker-compose up -d
```

## Security Considerations

1. **SSH Keys**: Use strong SSH keys and restrict access to the OCI instance
2. **Secrets**: Never commit secrets to the repository
3. **Network**: Configure OCI security lists to only allow necessary ports
4. **Database**: Use strong database passwords and consider using OCI Database services for production
5. **SSL/TLS**: Consider adding SSL certificates for HTTPS access

## Scaling

For production environments, consider:
- Using OCI Container Engine for Kubernetes (OKE)
- Setting up load balancers
- Using OCI Database services
- Implementing proper backup strategies
- Setting up monitoring and alerting
