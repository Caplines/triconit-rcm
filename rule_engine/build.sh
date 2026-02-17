#!/bin/bash

# Build script for Rule Engine Docker deployment
# Usage: ./build.sh [--no-cache]

set -e  # Exit on error

echo "================================================"
echo "Rule Engine Docker Build Script"
echo "================================================"
echo ""

# Parse arguments
NO_CACHE=""
if [ "$1" = "--no-cache" ]; then
    NO_CACHE="--no-cache"
    echo "Building with --no-cache flag"
fi

# Check if docker-compose.yml exists
if [ ! -f "docker-compose.yml" ]; then
    echo "Error: docker-compose.yml not found!"
    echo "Please run this script from the rule_engine directory"
    exit 1
fi

# Check if .env file exists
if [ ! -f ".env" ]; then
    echo "Warning: .env file not found!"
    echo "Creating from .env.example..."
    if [ -f ".env.example" ]; then
        cp .env.example .env
        echo "Please edit .env file with your configuration before deploying"
        exit 1
    else
        echo "Error: .env.example not found either!"
        exit 1
    fi
fi

echo "Step 1: Stopping existing containers..."
docker compose down

echo ""
echo "Step 2: Building Docker images..."
docker compose build $NO_CACHE

echo ""
echo "Step 3: Starting containers..."
docker compose up -d

echo ""
echo "Step 4: Waiting for services to start..."
sleep 10

echo ""
echo "Step 5: Checking container status..."
docker compose ps

echo ""
echo "================================================"
echo "Build Complete!"
echo "================================================"
echo ""
echo "Backend logs:   docker compose logs -f backend"
echo "Frontend logs:  docker compose logs -f frontend"
echo "All logs:       docker compose logs -f"
echo ""
echo "Application URLs:"
echo "  Frontend: http://localhost:80"
echo "  Backend:  http://localhost:8081"
echo ""
