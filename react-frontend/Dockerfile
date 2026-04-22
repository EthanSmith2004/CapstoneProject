# Multi-stage build for React frontend with nginx

# Stage 1: Build the React application
FROM node:18-alpine AS builder

# Set working directory
WORKDIR /app

# Copy package files
COPY package.json package-lock.json* ./

# Install dependencies
RUN npm ci

# Copy source code
COPY . .

# Build the application
RUN npm run build

# Stage 2: Serve with nginx
FROM nginx:alpine

# Remove all existing nginx configuration files
RUN rm -f /etc/nginx/conf.d/*.conf

# Copy custom nginx configuration template
COPY nginx.conf /etc/nginx/templates/default.conf.template

# Set default environment variable
ENV SERVER_NAME=localhost

# Copy built application from builder stage
COPY --from=builder /app/dist /usr/share/nginx/html

# Expose port 80
EXPOSE 80

# Start nginx
CMD ["nginx", "-g", "daemon off;"]
