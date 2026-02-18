# Multi-stage build for Angular frontend
FROM node:10.24.1 AS build

WORKDIR /app

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm install --unsafe-perm

# Copy source code
COPY . .

# Build production bundle
RUN npm run build -- --configuration=production


# Runtime stage - Nginx
FROM nginx:1.21-alpine

# Remove default config
RUN rm /etc/nginx/conf.d/default.conf

# Copy custom nginx config
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copy built Angular app
COPY --from=build /app/dist/ruleengine-client-app /usr/share/nginx/html

# Expose HTTP and HTTPS
EXPOSE 80
EXPOSE 443

CMD ["nginx", "-g", "daemon off;"]