# Multi-stage build for RCM Angular frontend (Angular 15)
FROM node:18-alpine AS build

WORKDIR /app

# BUILD_CONFIG: 'production' for prod, 'docker' for local Docker
ARG BUILD_CONFIG=production

# Install dependencies
COPY package*.json ./
RUN npm install --legacy-peer-deps

# Copy source and build
COPY . .
RUN npm run build -- --configuration=${BUILD_CONFIG}


# Runtime stage - Nginx
FROM nginx:1.21-alpine

# Accept ENV argument: 'local' for development, 'prod' for production
ARG ENV=local

# Remove default nginx config
RUN rm /etc/nginx/conf.d/default.conf

# Copy environment-specific nginx config
COPY nginx.${ENV}.conf /etc/nginx/conf.d/default.conf

# Copy built Angular app
COPY --from=build /app/dist/rcm-client-app /usr/share/nginx/html

EXPOSE 80
EXPOSE 443

CMD ["nginx", "-g", "daemon off;"]
