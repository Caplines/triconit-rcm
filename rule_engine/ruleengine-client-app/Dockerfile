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

# Accept ENV argument (local or prod)
ARG ENV=local

# Remove default config
RUN rm /etc/nginx/conf.d/default.conf

# Copy environment-specific nginx config as a template.
# nginx:1.21-alpine processes /etc/nginx/templates/*.template through envsubst
# at startup, substituting env vars (e.g. ${RE_DOMAIN}) while leaving nginx
# variables ($uri, $host, etc.) untouched.
COPY nginx.${ENV}.conf /etc/nginx/templates/default.conf.template

# Copy built Angular app
COPY --from=build /app/dist/ruleengine-client-app /usr/share/nginx/html

# Expose HTTP and HTTPS
EXPOSE 80
EXPOSE 443

CMD ["nginx", "-g", "daemon off;"]
