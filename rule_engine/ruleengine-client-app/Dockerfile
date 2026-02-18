# Multi-stage build for Angular frontend
# Using Node 10 (supports ARM64/Apple Silicon)
FROM node:10.24.1 AS build

WORKDIR /app

# Copy package files
COPY package*.json ./

# Install dependencies (use npm install for legacy Angular 6 projects)
RUN npm install --unsafe-perm

# Copy source code
COPY . .

# Build for production with optimizations
RUN npm run build -- --configuration=production

# Runtime stage - use Nginx to serve static files (supports ARM64)
FROM nginx:1.21-alpine

# Remove default nginx config
RUN rm /etc/nginx/conf.d/default.conf

# Copy custom nginx config
COPY nginx.conf /etc/nginx/conf.d/

# Copy built Angular app from build stage
COPY --from=build /app/dist/ruleengine-client-app /usr/share/nginx/html

# Expose port 80
EXPOSE 80

COPY nginx.conf /etc/nginx/conf.d/default.conf

# Start Nginx
CMD ["nginx", "-g", "daemon off;"]
