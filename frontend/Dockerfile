# Multi-stage build for Angular frontend with Bun
FROM oven/bun:1.2.18-alpine AS build

# Set working directory
WORKDIR /app

# Copy package files
COPY package.json bun.lock ./

# Install dependencies
RUN bun install --frozen-lockfile

# Copy source code
COPY . .

# Build the application
RUN bun run build

# Production stage with Nginx
FROM nginx:alpine

# Create non-root user for security (nginx user already exists in nginx:alpine)
RUN addgroup -g 1001 -S mdbuser && \
    adduser -S -D -H -u 1001 -h /var/cache/nginx -s /sbin/nologin -G mdbuser -g mdbuser mdbuser

# Copy built application from build stage
COPY --from=build /app/dist/mdb-frontend /usr/share/nginx/html

# Copy custom nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Set proper permissions
RUN chown -R mdbuser:mdbuser /usr/share/nginx/html && \
    chown -R mdbuser:mdbuser /var/cache/nginx && \
    chown -R mdbuser:mdbuser /var/log/nginx && \
    chown -R mdbuser:mdbuser /etc/nginx/conf.d && \
    touch /var/run/nginx.pid && \
    chown -R mdbuser:mdbuser /var/run/nginx.pid

# Switch to non-root user
USER mdbuser

# Expose port 80
EXPOSE 80

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:80/ || exit 1

# Start nginx
CMD ["nginx", "-g", "daemon off;"]