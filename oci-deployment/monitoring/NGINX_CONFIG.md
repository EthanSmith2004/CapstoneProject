# NGINX Configuration for Monitoring

Add this location block to your NGINX configuration to enable the stub_status endpoint needed for Prometheus metrics collection.

## Configuration

Add this to your NGINX server block (usually in `/etc/nginx/conf.d/default.conf` or `/etc/nginx/sites-available/default`):

```nginx
# Prometheus metrics endpoint
location /stub_status {
    stub_status on;
    access_log off;
    allow 172.16.0.0/12;  # Allow Docker network
    deny all;
}
```

## Complete Example

Here's a complete example of an NGINX configuration with the monitoring endpoint:

```nginx
server {
    listen 80;
    server_name localhost;

    # Root directory for static files
    root /usr/share/nginx/html;
    index index.html index.htm;

    # API proxy
    location /api/ {
        proxy_pass http://api:8080/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # Frontend application
    location / {
        try_files $uri $uri/ /index.html;
    }

    # Prometheus metrics endpoint
    location /stub_status {
        stub_status on;
        access_log off;
        allow 172.16.0.0/12;  # Allow Docker network
        deny all;
    }

    # Health check endpoint
    location /health {
        access_log off;
        return 200 "healthy\n";
        add_header Content-Type text/plain;
    }
}
```

## Verification

After updating your NGINX configuration and restarting:

1. Test the stub_status endpoint from within the Docker network:
   ```bash
   docker exec frontend curl http://localhost/stub_status
   ```

2. You should see output like:
   ```
   Active connections: 1 
   server accepts handled requests
    23 23 45 
   Reading: 0 Writing: 1 Waiting: 0
   ```

3. Verify the nginx-exporter can access it:
   ```bash
   docker logs nginx-exporter
   ```
   Should show no errors about connecting to stub_status.

## Troubleshooting

If the exporter can't connect:

1. Check NGINX config syntax:
   ```bash
   docker exec frontend nginx -t
   ```

2. Reload NGINX:
   ```bash
   docker exec frontend nginx -s reload
   ```

3. Check the exporter logs:
   ```bash
   docker logs nginx-exporter
   ```

## Alternative: If you can't modify NGINX config

If your NGINX image doesn't allow configuration changes, you can use the `nginx/nginx-prometheus-exporter` in "basic" mode which scrapes general NGINX metrics without stub_status, though this provides limited metrics.

Update the nginx-exporter command in docker-compose.yml:
```yaml
nginx-exporter:
  image: nginx/nginx-prometheus-exporter:latest
  container_name: nginx-exporter
  command:
    - '-web.listen-address=:9113'
  # Remove the -nginx.scrape-uri flag
```
