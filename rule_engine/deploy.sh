#!/usr/bin/env bash
# ============================================================
# deploy.sh — Server-side deploy script
#
# Pulls latest images from Docker Hub and restarts containers.
# NO builds happen here. Run this on the server only.
#
# Usage:
#   ./deploy.sh re                     # Deploy RuleEngine (latest)
#   ./deploy.sh rcm                    # Deploy RCM (latest)
#   ./deploy.sh all                    # Deploy both
#   ./deploy.sh re --service backend   # Update only the backend
#   ./deploy.sh re --service frontend  # Update only the frontend
#   ./deploy.sh re --version 2024.06.01-1430   # Deploy specific version
#   ./deploy.sh --down re              # Stop RuleEngine
#   ./deploy.sh --down all             # Stop everything
#   ./deploy.sh --status               # Show container status
#   ./deploy.sh --rollback re 2024.06.01-1200  # Roll back to a specific version
# ============================================================
set -euo pipefail
cd "$(dirname "$0")"

# ── Config ───────────────────────────────────────────────────
REPO="${DOCKER_HUB_REPO:-}"
if [ -z "$REPO" ]; then
    if [ -f .env ]; then
        set -a; source .env; set +a
    fi
fi
if [ -z "${DOCKER_HUB_REPO:-}" ]; then
    echo "[ERROR] DOCKER_HUB_REPO not set. Add it to .env:"
    echo "        DOCKER_HUB_REPO=yourdockerhub/capline"
    exit 1
fi
REPO="${DOCKER_HUB_REPO}"

RE_VERSION="${RE_VERSION:-latest}"
RCM_VERSION="${RCM_VERSION:-latest}"

# ── Args ─────────────────────────────────────────────────────
TARGET="${1:-}"
DOWN=false
STATUS=false
ROLLBACK=false
SINGLE_SERVICE=""
VERSION_OVERRIDE=""
ROLLBACK_VERSION=""

if [ -z "$TARGET" ]; then
    echo "Usage: ./deploy.sh [re|rcm|all] [options]"
    echo "       ./deploy.sh --status"
    echo "       ./deploy.sh --down [re|rcm|all]"
    echo "       ./deploy.sh --rollback [re|rcm] <version>"
    exit 1
fi

# Handle flag-first invocations (--status, --down, --rollback)
if [ "$TARGET" = "--status" ]; then
    STATUS=true
    TARGET=""
elif [ "$TARGET" = "--down" ]; then
    DOWN=true
    TARGET="${2:-all}"
elif [ "$TARGET" = "--rollback" ]; then
    ROLLBACK=true
    TARGET="${2:-}"
    ROLLBACK_VERSION="${3:-}"
    [ -z "$TARGET" ] && { echo "[ERROR] --rollback requires target (re|rcm) and version"; exit 1; }
    [ -z "$ROLLBACK_VERSION" ] && { echo "[ERROR] --rollback requires a version tag"; exit 1; }
else
    # Parse remaining args
    shift || true
    while [[ $# -gt 0 ]]; do
        case "$1" in
            --service)   SINGLE_SERVICE="${2:-}"; shift 2 ;;
            --version)   VERSION_OVERRIDE="${2:-}"; shift 2 ;;
            *) echo "[ERROR] Unknown option: $1"; exit 1 ;;
        esac
    done
fi

# ── Helpers ──────────────────────────────────────────────────
load_env() {
    if [ -f .env ]; then
        set -a; source .env; set +a
    fi
    REPO="${DOCKER_HUB_REPO:-$REPO}"
}

show_status() {
    echo "Container status:"
    for C in nginx-proxy ruleengine-backend ruleengine-frontend rcm-backend rcm-frontend; do
        STATE=$(docker inspect --format='{{.State.Status}}' "$C" 2>/dev/null || echo "not found")
        HEALTH=$(docker inspect --format='{{.State.Health.Status}}' "$C" 2>/dev/null || echo "-")
        IMAGE=$(docker inspect --format='{{.Config.Image}}' "$C" 2>/dev/null || echo "-")
        printf "  %-25s state=%-10s health=%-10s image=%s\n" "$C" "$STATE" "$HEALTH" "$IMAGE"
    done
}

pull_and_restart_re() {
    local VER="${VERSION_OVERRIDE:-$RE_VERSION}"
    export RE_VERSION="$VER"

    if [ -n "$SINGLE_SERVICE" ]; then
        echo "[INFO] Updating RE ${SINGLE_SERVICE} only (version: ${VER})..."
        docker compose -f docker-compose.prod.yml pull "$SINGLE_SERVICE"
        docker compose -f docker-compose.prod.yml up -d --no-deps "$SINGLE_SERVICE"
    else
        echo "[INFO] Deploying RuleEngine (version: ${VER})..."
        docker compose -f docker-compose.prod.yml pull
        docker compose -f docker-compose.prod.yml up -d
    fi
    echo "[OK] RuleEngine deployed (version: ${VER})"
}

pull_and_restart_rcm() {
    local VER="${VERSION_OVERRIDE:-$RCM_VERSION}"
    export RCM_VERSION="$VER"

    if [ -n "$SINGLE_SERVICE" ]; then
        echo "[INFO] Updating RCM ${SINGLE_SERVICE} only (version: ${VER})..."
        docker compose -f docker-compose.rcm.prod.yml pull "$SINGLE_SERVICE"
        docker compose -f docker-compose.rcm.prod.yml up -d --no-deps "$SINGLE_SERVICE"
    else
        echo "[INFO] Deploying RCM (version: ${VER})..."
        docker compose -f docker-compose.rcm.prod.yml pull
        docker compose -f docker-compose.rcm.prod.yml up -d
    fi
    echo "[OK] RCM deployed (version: ${VER})"
}

# ── Status ────────────────────────────────────────────────────
if $STATUS; then
    show_status
    exit 0
fi

# ── Down ─────────────────────────────────────────────────────
if $DOWN; then
    load_env
    case "$TARGET" in
        re)
            echo "[INFO] Stopping RuleEngine..."
            docker compose -f docker-compose.prod.yml down
            ;;
        rcm)
            echo "[INFO] Stopping RCM..."
            docker compose -f docker-compose.rcm.prod.yml down
            ;;
        all)
            echo "[INFO] Stopping all services..."
            docker compose -f docker-compose.prod.yml down 2>/dev/null || true
            docker compose -f docker-compose.rcm.prod.yml down 2>/dev/null || true
            docker compose -f docker-compose.proxy.yml down 2>/dev/null || true
            ;;
    esac
    echo "[OK] Done."
    exit 0
fi

# ── Rollback ─────────────────────────────────────────────────
if $ROLLBACK; then
    load_env
    echo "[INFO] Rolling back ${TARGET} to version: ${ROLLBACK_VERSION}"
    case "$TARGET" in
        re)
            VERSION_OVERRIDE="$ROLLBACK_VERSION"
            pull_and_restart_re
            ;;
        rcm)
            VERSION_OVERRIDE="$ROLLBACK_VERSION"
            pull_and_restart_rcm
            ;;
    esac
    echo "[OK] Rollback complete."
    exit 0
fi

# ── Deploy ───────────────────────────────────────────────────
load_env

# Ensure shared-proxy network exists
if ! docker network inspect shared-proxy >/dev/null 2>&1; then
    echo "[INFO] Creating shared-proxy network..."
    docker network create shared-proxy
fi

# Start proxy if not running
if ! docker ps --filter name=nginx-proxy --filter status=running -q | grep -q .; then
    echo "[INFO] Starting nginx-proxy..."
    docker compose -f docker-compose.proxy.yml up -d
fi

case "$TARGET" in
    re)  pull_and_restart_re ;;
    rcm) pull_and_restart_rcm ;;
    all)
        pull_and_restart_re
        pull_and_restart_rcm
        ;;
    *)
        echo "[ERROR] Unknown target: $TARGET. Use: re | rcm | all"
        exit 1
        ;;
esac

echo ""
echo "════════════════════════════════════════"
show_status
echo "════════════════════════════════════════"