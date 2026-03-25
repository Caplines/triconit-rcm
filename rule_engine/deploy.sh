#!/bin/bash

# Deployment script for production
# Usage: ./deploy.sh

set -e  # Exit on error

echo "================================================"
echo "Rule Engine Production Deployment"
echo "================================================"
echo ""

# Check if .env exists
if [ ! -f ".env" ]; then
    echo "Error: .env file not found!"
    echo "Please create .env from .env.example and configure it first"
    exit 1
fi

# Pull latest code
echo "Step 1: Pulling latest code from git..."
git pull

echo ""
echo "Step 2: Stopping existing containers..."
docker compose down

echo ""
echo "Step 3: Pulling latest base images..."
docker compose pull

echo ""
echo "Step 4: Building application images..."
docker compose build

echo ""
echo "Step 5: Starting containers..."
docker compose up -d

echo ""
echo "Step 6: Waiting for services to be healthy..."
sleep 15

echo ""
echo "Step 7: Checking container status..."
docker compose ps

echo ""
echo "Step 8: Displaying recent logs..."
docker compose logs --tail=50

echo ""
echo "================================================"
echo "Deployment Complete!"
echo "================================================"
echo ""
echo "Monitor logs with: docker compose logs -f"
echo ""
