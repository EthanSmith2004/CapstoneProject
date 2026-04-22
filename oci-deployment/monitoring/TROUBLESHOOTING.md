# Troubleshooting Monitoring Stack

## NGINX Logs Not Being Collected

### Problem
Promtail shows errors like:
```
level=error msg="failed to start tailer" error="lstat /proc/1/fd/pipe:[56927]: no such file or directory"
```

### Root Cause
Docker containers typically log to stdout/stderr (pipes) instead of physical log files.

### Solution
✅ **Fixed** - Promtail now uses Docker service discovery to collect logs directly from container stdout/stderr.

### Configuration Changes Made:

1. **Promtail** now mounts Docker socket:
   ```yaml
   volumes:
     - /var/run/docker.sock:/var/run/docker.sock:ro
     - /var/lib/docker/containers:/var/lib/docker/containers:ro
   ```

2. **Containers** are labeled for log collection:
   ```yaml
   labels:
     logging: "promtail"
   ```

3. **Promtail config** uses Docker SD instead of file paths:
   ```yaml
   scrape_configs:
     - job_name: docker
       docker_sd_configs:
         - host: unix:///var/run/docker.sock
   ```

## How to Apply the Fix

### Option 1: Restart Promtail Only
```bash
docker-compose restart promtail
```

### Option 2: Full Restart (Recommended)
```bash
docker-compose down
docker-compose up -d
```

### Option 3: Rebuild if needed
```bash
docker-compose down
docker-compose up -d --force-recreate promtail
```

## Verify Logs are Being Collected

### 1. Check Promtail Logs
```bash
docker logs promtail
```

You should see:
- No more "failed to start tailer" errors
- Messages like "Successfully added target" for frontend and api containers

### 2. Check Loki has received logs
```bash
# Query Loki for NGINX logs
curl -G 'http://localhost:3100/loki/api/v1/query' --data-urlencode 'query={job="nginx"}' | jq

# Query Loki for Spring Boot logs
curl -G 'http://localhost:3100/loki/api/v1/query' --data-urlencode 'query={job="spring-boot"}' | jq
```

### 3. Check in Grafana
1. Go to http://localhost:3000
2. Navigate to Explore (compass icon)
3. Select "Loki" datasource
4. Run query: `{job="nginx"}` or `{job="spring-boot"}`
5. You should see logs appearing

## Common Issues

### Issue: No logs showing for a specific container

**Check container is labeled:**
```bash
docker inspect frontend | grep -A 5 Labels
docker inspect api | grep -A 5 Labels
```

Should show: `"logging": "promtail"`

### Issue: Promtail can't access Docker socket

**On Linux/Mac:**
```bash
# Check socket permissions
ls -la /var/run/docker.sock

# If needed, add read permissions
sudo chmod 666 /var/run/docker.sock
```

**On Windows with Docker Desktop:**
- Ensure "Expose daemon on tcp://localhost:2375 without TLS" is NOT enabled (security risk)
- Docker socket should be automatically available

### Issue: Still seeing old error messages

**Clear positions file:**
```bash
docker-compose stop promtail
docker exec promtail rm /tmp/positions.yaml
docker-compose start promtail
```

### Issue: Logs not parsed correctly

**Check pipeline stages in Grafana:**
1. Go to Explore
2. Run query with filters: `{job="nginx"} |= "GET"`
3. Click on a log line to see extracted labels
4. Should see: method, status, path labels

## Testing Log Collection

### Generate NGINX Logs
```bash
# Make some requests to generate logs
curl http://localhost/
curl http://localhost/api/health
curl http://localhost/nonexistent  # 404 error
```

### Generate Spring Boot Logs
```bash
# Hit various endpoints
curl http://localhost:8080/actuator/health
curl http://localhost:8080/actuator/prometheus
```

Then check in Grafana Explore within 5-10 seconds.

## Performance Tuning

If you have high log volume:

### Reduce scrape frequency
In `promtail-config.yml`:
```yaml
docker_sd_configs:
  - host: unix:///var/run/docker.sock
    refresh_interval: 30s  # Increase from 5s
```

### Add log filtering
```yaml
pipeline_stages:
  - match:
      selector: '{job="nginx"}'
      drop_regex:
        source: 'message'
        expression: '.*health.*'  # Drop health check logs
```

### Limit retention in Loki
In `loki-config.yml`:
```yaml
limits_config:
  reject_old_samples_max_age: 72h  # Reduce from 168h
```

## Debug Mode

Enable debug logging in Promtail:

```bash
docker-compose stop promtail
```

Update docker-compose.yml:
```yaml
promtail:
  command: 
    - -config.file=/etc/promtail/promtail-config.yml
    - -log.level=debug
```

```bash
docker-compose up -d promtail
docker logs -f promtail
```
