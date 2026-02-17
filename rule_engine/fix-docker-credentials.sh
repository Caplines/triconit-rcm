#!/bin/bash

echo "Fixing Docker credentials on macOS..."
echo ""

# Remove existing credential helper config
echo "Step 1: Removing old credential helper config..."
rm -f ~/.docker/config.json

# Create new config without credential helper
echo "Step 2: Creating new Docker config..."
mkdir -p ~/.docker
cat > ~/.docker/config.json << 'EOF'
{
  "auths": {},
  "credsStore": ""
}
EOF

echo ""
echo "Step 3: Verifying Docker is running..."
if ! docker info > /dev/null 2>&1; then
    echo "ERROR: Docker is not running!"
    echo "Please start Docker Desktop and try again."
    exit 1
fi

echo "✅ Docker is running"
echo ""
echo "Step 4: Testing image pull..."
docker pull hello-world

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Docker credentials fixed!"
    echo ""
    echo "Now you can run: ./build.sh"
else
    echo ""
    echo "❌ Still having issues. Try these steps:"
    echo "1. Open Docker Desktop"
    echo "2. Go to Settings > General"
    echo "3. Uncheck 'Use osxkeychain for credentials'"
    echo "4. Restart Docker Desktop"
    echo "5. Run this script again"
fi
