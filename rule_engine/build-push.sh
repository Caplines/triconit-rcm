#!/usr/bin/env bash
# ============================================================
# build-push.sh — Build images locally and push to Docker Hub
#
# Run from rule_engine/ on your DEVELOPER MACHINE only.
# Never run this on the server.
#
# Usage:
#   ./build-push.sh re              # Build + push RuleEngine (BE + FE)
#   ./build-push.sh rcm             # Build + push RCM (BE + FE)
#   ./build-push.sh all             # Build + push everything
#   ./build-push.sh re --be-only    # RuleEngine backend only
#   ./build-push.sh re --fe-only    # RuleEngine frontend only
#   ./build-push.sh rcm --be-only   # RCM backend only
#   ./build-push.sh rcm --fe-only   # RCM frontend only
#
# Version tag is auto-generated as YYYY.MM.DD-HHmm by default.
# Override with: VERSION=1.2.3 ./build-push.sh re
# ============================================================
set -euo pipefail
cd "$(dirname "$0")"

# ── Config ───────────────────────────────────────────────────
# Set your Docker Hub repo here (e.g. johndoe/capline)
REPO="${DOCKER_HUB_REPO:-}"
if [ -z "$REPO" ]; then
    if [ -f .env ]; then
        set -a
        # shellcheck disable=SC1091
        source .env
        set +a
    fi
    REPO="${DOCKER_HUB_REPO:-}"
fi
if [ -z "$REPO" ]; then
    echo "[ERROR] Set DOCKER_HUB_REPO in your environment or .env:"
    echo "        export DOCKER_HUB_REPO=yourdockerhub/capline"
    exit 1
fi

# Auto-generate version tag based on timestamp, or use override
VERSION="${VERSION:-$(date +%Y.%m.%d-%H%M)}"

# ── Args ─────────────────────────────────────────────────────
TARGET="${1:-}"
FILTER="${2:-}"

if [ -z "$TARGET" ]; then
    echo "Usage: ./build-push.sh [re|rcm|all] [--be-only|--fe-only]"
    exit 1
fi

# ── Helpers ──────────────────────────────────────────────────
build_and_push() {
    local NAME="$1"    # e.g. re-backend
    local CONTEXT="$2" # build context path
    local DOCKERFILE="$3"
    shift 3
    local BUILD_ARGS=("$@") # remaining are --build-arg KEY=VAL

    local IMAGE_VERSIONED="${REPO}:${NAME}-${VERSION}"
    local IMAGE_LATEST="${REPO}:${NAME}-latest"

    echo ""
    echo "════════════════════════════════════════"
    echo "  Building: ${NAME}"
    echo "  Tag:      ${VERSION}"
    echo "════════════════════════════════════════"

    docker build \
        -f "$DOCKERFILE" \
        "${BUILD_ARGS[@]+"${BUILD_ARGS[@]}"}" \
        -t "$IMAGE_VERSIONED" \
        -t "$IMAGE_LATEST" \
        "$CONTEXT"

    echo ""
    echo "  Pushing ${IMAGE_VERSIONED}..."
    docker push "$IMAGE_VERSIONED"

    echo "  Pushing ${IMAGE_LATEST}..."
    docker push "$IMAGE_LATEST"

    echo "  [OK] ${NAME} pushed (version: ${VERSION})"
}

# ── Build functions ───────────────────────────────────────────
build_re_backend() {
    build_and_push \
        "re-backend" \
        ".." \
        "rule_engine/ruleengine/Dockerfile"
}

build_re_frontend() {
    build_and_push \
        "re-frontend" \
        "./ruleengine-client-app" \
        "ruleengine-client-app/Dockerfile" \
        "--build-arg" "ENV=prod"
}

build_rcm_backend() {
    build_and_push \
        "rcm-backend" \
        "." \
        "rcm/Dockerfile"
}

build_rcm_frontend() {
    build_and_push \
        "rcm-frontend" \
        "./rcm-client-app" \
        "rcm-client-app/Dockerfile" \
        "--build-arg" "ENV=prod" \
        "--build-arg" "BUILD_CONFIG=production"
}

# ── Main ─────────────────────────────────────────────────────
echo "========================================"
echo "  Docker Hub Repo : ${REPO}"
echo "  Version tag     : ${VERSION}"
echo "========================================"

# Ensure logged in
if ! docker info 2>/dev/null | grep -q "Username"; then
    echo ""
    echo "[INFO] Not logged in to Docker Hub. Running docker login..."
    docker login
fi

case "$TARGET" in
    re)
        case "$FILTER" in
            --be-only) build_re_backend ;;
            --fe-only) build_re_frontend ;;
            *)         build_re_backend; build_re_frontend ;;
        esac
        ;;
    rcm)
        case "$FILTER" in
            --be-only) build_rcm_backend ;;
            --fe-only) build_rcm_frontend ;;
            *)         build_rcm_backend; build_rcm_frontend ;;
        esac
        ;;
    all)
        build_re_backend
        build_re_frontend
        build_rcm_backend
        build_rcm_frontend
        ;;
    *)
        echo "[ERROR] Unknown target: $TARGET. Use: re | rcm | all"
        exit 1
        ;;
esac

echo ""
echo "========================================"
echo "  All images pushed successfully!"
echo "  Version: ${VERSION}"
echo ""
echo "  To deploy on the server:"
echo "    RE:  VERSION=${VERSION} docker compose -f docker-compose.prod.yml pull"
echo "         VERSION=${VERSION} docker compose -f docker-compose.prod.yml up -d"
echo ""
echo "    RCM: VERSION=${VERSION} docker compose -f docker-compose.rcm.prod.yml pull"
echo "         VERSION=${VERSION} docker compose -f docker-compose.rcm.prod.yml up -d"
echo "========================================"