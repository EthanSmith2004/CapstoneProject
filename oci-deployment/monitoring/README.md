# Monitoring Stack Configuration

This directory contains the configuration files for the monitoring stack consisting of:
- **Prometheus**: Metrics collection and storage
- **Loki**: Log aggregation
- **Promtail**: Log shipping agent
- **Grafana**: Visualization and dashboards

## Directory Structure

```
monitoring/
├── prometheus/
│   └── prometheus.yml          # Prometheus scrape configuration
├── loki/
│   └── loki-config.yml         # Loki server configuration
├── promtail/
│   └── promtail-config.yml     # Promtail log collection configuration
└── grafana/
    └── provisioning/
        ├── datasources/
        │   └── datasources.yml # Auto-configured datasources
        └── dashboards/
            ├── dashboards.yml  # Dashboard provisioning config
            └── application-monitoring.json  # Main monitoring dashboard
```

## Services

### Prometheus (Port 9090)
Collects metrics from:
- Spring Boot application via `/actuator/prometheus` endpoint
- NGINX via nginx-exporter
- PostgreSQL via postgres-exporter

### Loki (Port 3100)
Aggregates logs from:
- Spring Boot application
- NGINX access and error logs

### Promtail (Port 9080)
Ships logs from:
- NGINX log files (`/var/log/nginx/`)
- Spring Boot log files (`/var/log/spring/`)

### Grafana (Port 3000)
- **Default credentials**: admin/admin (change via environment variables)
- Pre-configured with Prometheus and Loki datasources
- Includes comprehensive application monitoring dashboard

## Dashboard Features

The **Application Monitoring Dashboard** includes:

### NGINX Metrics
- Request rate (RPS) over time
- Total requests per second (gauge)
- Requests by HTTP status code
- Request distribution by endpoint

### API Metrics
- **Latency percentiles** (p50, p95, p99) by endpoint
- Request rate by endpoint and HTTP method
- Drill-down table showing all endpoints with:
  - Request rate
  - Mean latency
  - Method and path
- API availability percentage
- JVM memory usage

### Logs
- Unified log view from both Spring Boot and NGINX
- Filter by application, log level, and time range
- Click on log entries to see full details

## Spring Boot Configuration Required

Your Spring Boot application must have these properties configured:

```properties
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.endpoint.health.show-details=always
management.prometheus.metrics.export.enabled=true
management.endpoint.prometheus.access=unrestricted
```

### Logging Configuration (Optional)

If you want to write logs to files for Promtail to collect, add to your `application.properties` or `logback-spring.xml`:

```properties
logging.file.name=/var/log/spring/application.log
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss.SSS} %5p [%t] %c : %m%n
```

## NGINX Configuration Required

Your NGINX configuration needs to expose the stub_status endpoint. Add this to your NGINX config:

```nginx
server {
    listen 80;
    
    # Existing location blocks...
    
    # Prometheus metrics endpoint
    location /stub_status {
        stub_status on;
        access_log off;
        allow 172.16.0.0/12;  # Allow Docker network
        deny all;
    }
}
```

## Accessing the Services

After running `docker-compose up`:

- **Grafana**: http://localhost:3000
- **Prometheus**: http://localhost:9090
- **Loki**: http://localhost:3100
- **NGINX Exporter**: http://localhost:9113/metrics
- **PostgreSQL Exporter**: http://localhost:9187/metrics

## Environment Variables

You can customize Grafana credentials using environment variables:

```bash
GRAFANA_USER=admin
GRAFANA_PASSWORD=your-secure-password
```

## Data Persistence

The following volumes are created for data persistence:
- `prometheus_data`: Prometheus metrics storage
- `loki_data`: Loki log storage
- `grafana_data`: Grafana dashboards and settings
- `nginx_logs`: NGINX access and error logs
- `spring_logs`: Spring Boot application logs

## Troubleshooting

### No metrics showing in Grafana

1. Check if Prometheus is scraping targets:
   - Visit http://localhost:9090/targets
   - All targets should show as "UP"

2. Verify Spring Boot actuator endpoint:
   - Visit http://localhost:8080/actuator/prometheus
   - Should return metrics in Prometheus format

3. Check NGINX exporter:
   - Visit http://localhost:9113/metrics
   - Should return NGINX metrics

### No logs showing in Grafana

1. Check Promtail is running:
   ```bash
   docker logs promtail
   ```

2. Verify Loki is receiving logs:
   - Visit http://localhost:3100/ready
   - Should return "ready"

3. Check log file paths exist in containers:
   ```bash
   docker exec frontend ls -la /var/log/nginx/
   docker exec api ls -la /var/log/spring/
   ```

## Customizing the Dashboard

The dashboard is automatically provisioned from `monitoring/grafana/provisioning/dashboards/application-monitoring.json`. 

To customize:
1. Make changes in Grafana UI
2. Export the dashboard (Share → Export → Save to file)
3. Replace the JSON file
4. Restart Grafana container

## Performance Considerations

- **Prometheus retention**: Default is 15 days, configure with `--storage.tsdb.retention.time`
- **Loki retention**: Configured in `loki-config.yml` (currently 168h = 7 days)
- **Scrape intervals**: Can be adjusted in `prometheus.yml` based on load
