#!/bin/bash

# Stop all containers
echo "Stopping Rule Engine containers..."
docker compose down

echo ""
echo "Containers stopped."
echo "To remove volumes as well, run: docker compose down -v"
