# NGINX Stub Status Configuration

## The Problem
The NGINX exporter is trying to scrape `http://frontend:80/stub_status` but getting the HTML homepage instead. This means the NGINX configuration inside the frontend container doesn't have the stub_status endpoint configured.

## Solution Options

### Option 1: Add stub_status to NGINX Config (Recommended)

You need to modify the NGINX configuration in your **frontend image**. Add this location block to your NGINX config file:

```nginx
server {
    listen 80;
    
    # Your existing location blocks...
    location / {
        root /usr/share/nginx/html;
        try_files $uri $uri/ /index.html;
    }
    
    # Add this for Prometheus metrics
    location /stub_status {
        stub_status on;
        access_log off;
        allow 172.16.0.0/12;  # Allow Docker networks
        allow 127.0.0.1;       # Allow localhost
        deny all;
    }
}
```

**Then rebuild and push your frontend image.**

### Option 2: Use NGINX with Built-in Stub Status (Quick Test)

If you want to test without rebuilding, you can use a sidecar approach or modify the docker-compose to use a standard nginx image temporarily.

### Option 3: Disable NGINX Exporter (Temporary)

If you can't modify the NGINX config right now, you can temporarily disable the exporter:

```yaml
# Comment out or remove nginx-exporter service in docker-compose.yml
```

And update Prometheus config to remove the nginx scrape job.

## Verify stub_status Endpoint

After adding the configuration, test it:

```bash
# From inside the frontend container
docker exec frontend curl http://localhost/stub_status

# Expected output:
Active connections: 1 
server accepts handled requests
 23 23 45 
Reading: 0 Writing: 1 Waiting: 0
```

## Current Workaround

For now, I'll update the docker-compose to make the nginx-exporter optional so it doesn't spam errors.
