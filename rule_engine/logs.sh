#!/bin/bash

# Quick script to view logs
# Usage: ./logs.sh [backend|frontend|all]

SERVICE=${1:-all}

if [ "$SERVICE" = "all" ]; then
    docker compose logs -f
elif [ "$SERVICE" = "backend" ]; then
    docker compose logs -f backend
elif [ "$SERVICE" = "frontend" ]; then
    docker compose logs -f frontend
else
    echo "Usage: ./logs.sh [backend|frontend|all]"
    echo "Example: ./logs.sh backend"
    exit 1
fi
