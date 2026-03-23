#!/usr/bin/env bash
# ──────────────────────────────────────────────────────────────
# deploy-init-all.sh — Full deploy script for both RuleEngine + RCM
# ──────────────────────────────────────────────────────────────
#
# Usage:
#   ./deploy-init-all.sh --setup       Install Docker, Compose, certbot + create .env
#   ./deploy-init-all.sh --init-ssl    Obtain SSL certs for both domains
#   ./deploy-init-all.sh              Deploy both apps with SSL
#   ./deploy-init-all.sh --no-ssl     Deploy both apps HTTP only
#   ./deploy-init-all.sh --status     Show container status
#   ./deploy-init-all.sh --logs       Tail container logs
#   ./deploy-init-all.sh --down       Stop everything
#   ./deploy-init-all.sh --help       Show this help
#
set -euo pipefail
cd "$(dirname "$0")"

NO_SSL=false
DOWN=false
SETUP=false
INIT_SSL=false
STATUS=false
LOGS=false

for arg in "$@"; do
    case $arg in
        --no-ssl)   NO_SSL=true ;;
        --down)     DOWN=true ;;
        --setup)    SETUP=true ;;
        --init-ssl) INIT_SSL=true ;;
        --status)   STATUS=true ;;
        --logs)     LOGS=true ;;
        -h|--help)
            echo "Usage: ./deploy-init-all.sh [OPTIONS]"
            echo ""
            echo "Full deployment script for both RuleEngine and RCM."
            echo ""
            echo "First-time setup (run once on a fresh server):"
            echo "  --setup       Install Docker, Docker Compose, certbot, and create .env from template"
            echo "  --init-ssl    Obtain SSL certs via certbot for BOTH domains"
            echo ""
            echo "Deploy:"
            echo "  (no flags)    Deploy both apps with SSL (default)"
            echo "  --no-ssl      Deploy both apps HTTP only (skip SSL cert check)"
            echo ""
            echo "Manage:"
            echo "  --status      Show container health status"
            echo "  --logs        Tail all container logs (Ctrl+C to stop)"
            echo "  --down        Stop all services (RE + RCM + proxy)"
            echo ""
            echo "Typical fresh-server workflow:"
            echo "  1. ./deploy-init-all.sh --setup"
            echo "  2. nano .env                          # fill in your values"
            echo "  3. ./deploy-init-all.sh --no-ssl      # deploy HTTP first"
            echo "  4. ./deploy-init-all.sh --down        # stop to free port 80"
            echo "  5. ./deploy-init-all.sh --init-ssl    # get SSL certs for both domains"
            echo "  6. ./deploy-init-all.sh               # redeploy with SSL"
            exit 0
            ;;
        *) echo "[ERROR] Unknown option: $arg (use --help)"; exit 1 ;;
    esac
done

# ════════════════════════════════════════════════════════════
# --setup: Install Docker, Docker Compose, certbot, create .env
# ════════════════════════════════════════════════════════════
if $SETUP; then
    echo "========================================"
    echo "  Server Setup for RuleEngine + RCM"
    echo "========================================"
    echo ""

    # ── Install Docker ────────────────────────────────────
    if command -v docker >/dev/null 2>&1; then
        echo "[OK] Docker is already installed: $(docker --version)"
    else
        echo "[INFO] Installing Docker..."
        curl -fsSL https://get.docker.com | sh
        sudo systemctl enable docker
        sudo systemctl start docker
        echo "[OK] Docker installed: $(docker --version)"

        if [ "$(id -u)" -ne 0 ]; then
            sudo usermod -aG docker "$USER"
            echo "[INFO] Added $USER to docker group. Log out and back in for this to take effect,"
            echo "       or run: newgrp docker"
        fi
    fi

    # ── Verify Docker Compose v2 ──────────────────────────
    if docker compose version >/dev/null 2>&1; then
        echo "[OK] Docker Compose v2 is available: $(docker compose version --short)"
    else
        echo "[INFO] Installing Docker Compose plugin..."
        sudo apt-get update && sudo apt-get install -y docker-compose-plugin 2>/dev/null \
            || { echo "[ERROR] Could not install docker-compose-plugin. Install manually."; exit 1; }
        echo "[OK] Docker Compose installed: $(docker compose version --short)"
    fi

    # ── Install certbot ───────────────────────────────────
    if command -v certbot >/dev/null 2>&1; then
        echo "[OK] certbot is already installed: $(certbot --version 2>&1)"
    else
        echo "[INFO] Installing certbot..."
        if command -v snap >/dev/null 2>&1; then
            sudo snap install --classic certbot 2>/dev/null || true
            sudo ln -sf /snap/bin/certbot /usr/bin/certbot 2>/dev/null || true
        elif command -v apt-get >/dev/null 2>&1; then
            sudo apt-get update && sudo apt-get install -y certbot
        elif command -v yum >/dev/null 2>&1; then
            sudo yum install -y certbot
        else
            echo "[WARN] Could not auto-install certbot. Install manually:"
            echo "       https://certbot.eff.org/instructions"
        fi

        if command -v certbot >/dev/null 2>&1; then
            echo "[OK] certbot installed: $(certbot --version 2>&1)"
        fi
    fi

    # ── Create .env from template ─────────────────────────
    if [ -f .env ]; then
        echo "[OK] .env already exists (not overwritten)"
    elif [ -f .env.example ]; then
        cp .env.example .env
        echo "[OK] Created .env from .env.example"
    else
        echo "[WARN] .env.example not found — cannot create .env"
    fi

    # ── Create certs directory ────────────────────────────
    mkdir -p certs
    echo "[OK] certs/ directory ready"

    echo ""
    echo "========================================"
    echo "  Setup complete!"
    echo "========================================"
    echo ""
    echo "  Next steps:"
    echo "    1. Edit .env with your values:"
    echo "       nano .env"
    echo ""
    echo "    2. Deploy (HTTP first, then get SSL):"
    echo "       ./deploy-init-all.sh --no-ssl"
    echo ""
    echo "    3. Or get SSL certs first, then deploy:"
    echo "       ./deploy-init-all.sh --init-ssl"
    echo "       ./deploy-init-all.sh"
    echo ""
    exit 0
fi

# ════════════════════════════════════════════════════════════
# --init-ssl: Obtain SSL certificates for BOTH domains
# ════════════════════════════════════════════════════════════
if $INIT_SSL; then
    command -v certbot >/dev/null 2>&1 || { echo "[ERROR] certbot not installed. Run: ./deploy-init-all.sh --setup"; exit 1; }

    if [ ! -f .env ]; then
        echo "[ERROR] .env not found. Run --setup first, then edit .env."
        exit 1
    fi
    set -a; source .env; set +a

    [ -z "${RE_DOMAIN:-}" ]       && { echo "[ERROR] RE_DOMAIN is not set in .env"; exit 1; }
    [ -z "${RE_SERVER_NAME:-}" ]  && { echo "[ERROR] RE_SERVER_NAME is not set in .env"; exit 1; }
    [ -z "${RCM_DOMAIN:-}" ]      && { echo "[ERROR] RCM_DOMAIN is not set in .env"; exit 1; }
    [ -z "${RCM_SERVER_NAME:-}" ] && { echo "[ERROR] RCM_SERVER_NAME is not set in .env"; exit 1; }

    echo "[INFO] Make sure:"
    echo "       1. Port 80 is free (stop proxy if running)"
    echo "       2. DNS for ALL domains points to this server"
    echo ""
    docker compose -f docker-compose.proxy.yml down 2>/dev/null || true

    # ── RuleEngine cert ───────────────────────────────────
    RE_CERT_DIR="/etc/letsencrypt/live/${RE_DOMAIN}"
    if [ -d "$RE_CERT_DIR" ]; then
        echo "[OK] SSL cert already exists for RuleEngine at ${RE_CERT_DIR}"
    else
        # Build certbot -d flags from RE_SERVER_NAME; RE_DOMAIN first for cert dir name
        CERTBOT_DOMAINS="-d ${RE_DOMAIN}"
        for DOMAIN in ${RE_SERVER_NAME}; do
            [ "$DOMAIN" != "${RE_DOMAIN}" ] && CERTBOT_DOMAINS="${CERTBOT_DOMAINS} -d ${DOMAIN}"
        done

        echo "[INFO] Obtaining RuleEngine SSL certificate covering:"
        for DOMAIN in ${RE_SERVER_NAME}; do echo "         - ${DOMAIN}"; done
        echo ""

        sudo certbot certonly --standalone ${CERTBOT_DOMAINS}

        if [ -d "$RE_CERT_DIR" ]; then
            echo "[OK] RuleEngine SSL certificate obtained"
        else
            echo "[ERROR] Certificate not found for RE. Check certbot output above."
            exit 1
        fi
    fi

    # ── RCM cert ──────────────────────────────────────────
    RCM_CERT_DIR="/etc/letsencrypt/live/${RCM_DOMAIN}"
    if [ -d "$RCM_CERT_DIR" ]; then
        echo "[OK] SSL cert already exists for RCM at ${RCM_CERT_DIR}"
    else
        CERTBOT_DOMAINS="-d ${RCM_DOMAIN}"
        for DOMAIN in ${RCM_SERVER_NAME}; do
            [ "$DOMAIN" != "${RCM_DOMAIN}" ] && CERTBOT_DOMAINS="${CERTBOT_DOMAINS} -d ${DOMAIN}"
        done

        echo ""
        echo "[INFO] Obtaining RCM SSL certificate covering:"
        for DOMAIN in ${RCM_SERVER_NAME}; do echo "         - ${DOMAIN}"; done
        echo ""

        sudo certbot certonly --standalone ${CERTBOT_DOMAINS}

        if [ -d "$RCM_CERT_DIR" ]; then
            echo "[OK] RCM SSL certificate obtained"
        else
            echo "[ERROR] Certificate not found for RCM. Check certbot output above."
            exit 1
        fi
    fi

    echo ""
    echo "[OK] SSL certificates ready for both apps."
    echo "     Now deploy: ./deploy-init-all.sh"
    exit 0
fi

# ════════════════════════════════════════════════════════════
# --status: Show container health status
# ════════════════════════════════════════════════════════════
if $STATUS; then
    echo "Service status:"
    for CONTAINER in nginx-proxy ruleengine-backend ruleengine-frontend rcm-backend rcm-frontend; do
        STATE=$(docker inspect --format='{{.State.Status}}' "$CONTAINER" 2>/dev/null || echo "not found")
        HEALTH=$(docker inspect --format='{{.State.Health.Status}}' "$CONTAINER" 2>/dev/null || echo "-")
        printf "  %-25s state=%-10s health=%s\n" "${CONTAINER}" "${STATE}" "${HEALTH}"
    done
    echo ""
    echo "RuleEngine:"
    docker compose -f docker-compose.yml ps 2>/dev/null || true
    echo ""
    echo "RCM:"
    docker compose -f docker-compose.rcm.yml ps 2>/dev/null || true
    exit 0
fi

# ════════════════════════════════════════════════════════════
# --logs: Tail container logs
# ════════════════════════════════════════════════════════════
if $LOGS; then
    echo "[INFO] Tailing all logs (Ctrl+C to stop)..."
    echo "[INFO] For app-specific logs use:"
    echo "         docker compose -f docker-compose.yml logs -f       # RuleEngine"
    echo "         docker compose -f docker-compose.rcm.yml logs -f   # RCM"
    echo ""
    # Show both stacks interleaved via docker logs on individual containers
    docker logs -f --tail 50 ruleengine-backend &
    PID_RE=$!
    docker logs -f --tail 50 rcm-backend &
    PID_RCM=$!
    trap "kill $PID_RE $PID_RCM 2>/dev/null; exit 0" INT TERM
    wait
    exit 0
fi

# ════════════════════════════════════════════════════════════
# --down: Stop all services
# ════════════════════════════════════════════════════════════
if $DOWN; then
    echo "[INFO] Stopping RuleEngine..."
    docker compose -f docker-compose.yml down 2>/dev/null || true
    echo "[INFO] Stopping RCM..."
    docker compose -f docker-compose.rcm.yml down 2>/dev/null || true
    echo "[INFO] Stopping proxy..."
    docker compose -f docker-compose.proxy.yml down 2>/dev/null || true
    echo "[OK] All services stopped."
    exit 0
fi

# ════════════════════════════════════════════════════════════
# Default: Deploy both apps
# ════════════════════════════════════════════════════════════
echo "========================================"
echo "  Deploying RuleEngine + RCM"
echo "========================================"
echo ""

# ── Pre-flight checks ─────────────────────────────────────
echo "[INFO] Running pre-flight checks..."

command -v docker >/dev/null 2>&1 || { echo "[ERROR] Docker not installed. Run: ./deploy-init-all.sh --setup"; exit 1; }
docker compose version >/dev/null 2>&1 || { echo "[ERROR] Docker Compose v2 not installed. Run: ./deploy-init-all.sh --setup"; exit 1; }

if [ ! -f .env ]; then
    if [ -f .env.example ]; then
        cp .env.example .env
        echo "[INFO] Created .env from .env.example"
        echo ""
        echo "[ERROR] Please edit .env with your actual values before deploying:"
        echo "        nano .env"
        exit 1
    else
        echo "[ERROR] .env file not found. Run: ./deploy-init-all.sh --setup"
        exit 1
    fi
fi

set -a; source .env; set +a

# ── Validate required variables ───────────────────────────
MISSING=()
[ -z "${RE_DOMAIN:-}" ]       && MISSING+=("RE_DOMAIN")
[ -z "${RE_SERVER_NAME:-}" ]  && MISSING+=("RE_SERVER_NAME")
[ -z "${RCM_DOMAIN:-}" ]      && MISSING+=("RCM_DOMAIN")
[ -z "${RCM_SERVER_NAME:-}" ] && MISSING+=("RCM_SERVER_NAME")
[ -z "${DB_URL:-}" ]          && MISSING+=("DB_URL")
[ -z "${DB_USERNAME:-}" ]     && MISSING+=("DB_USERNAME")
[ -z "${DB_PASSWORD:-}" ]     && MISSING+=("DB_PASSWORD")

if [ ${#MISSING[@]} -gt 0 ]; then
    echo "[ERROR] Missing required variables in .env: ${MISSING[*]}"
    exit 1
fi

echo "[OK] .env validated (RE=${RE_DOMAIN}, RCM=${RCM_DOMAIN})"

# ── Select nginx config ──────────────────────────────────
if $NO_SSL; then
    export NGINX_CONF=nginx-nossl.conf
    SCHEME="http"
    echo "[INFO] Mode: HTTP only (no SSL)"
else
    export NGINX_CONF=nginx-ssl.conf
    SCHEME="https"
    for DOMAIN in "$RE_DOMAIN" "$RCM_DOMAIN"; do
        CERT_DIR="/etc/letsencrypt/live/${DOMAIN}"
        if [ ! -d "$CERT_DIR" ]; then
            echo "[WARN] SSL cert not found at: $CERT_DIR"
            echo ""
            echo "  Options:"
            echo "    1. Get certs:    ./deploy-init-all.sh --init-ssl"
            echo "    2. Deploy HTTP:  ./deploy-init-all.sh --no-ssl"
            exit 1
        fi
    done
    echo "[OK] SSL certs found for both domains"
fi

# ── EagleSoft certs check ────────────────────────────────
if [ "${ES_SSL_CLIENT_TRUSTALL:-false}" = "false" ]; then
    if [ ! -f "${CERTS_PATH:-./certs}/cacerts.jks" ]; then
        echo "[WARN] EagleSoft truststore not found at ${CERTS_PATH:-./certs}/cacerts.jks"
        echo "       RuleEngine backend will fail to connect to EagleSoft."
        echo "       Set ES_SSL_CLIENT_TRUSTALL=true in .env for testing without certs."
    else
        echo "[OK] EagleSoft certs found"
    fi
else
    echo "[INFO] EagleSoft SSL: trust-all mode (no certs required)"
fi

# ── Create shared Docker network ─────────────────────────
if docker network create shared-proxy 2>/dev/null; then
    echo "[OK] Created 'shared-proxy' Docker network"
else
    echo "[OK] 'shared-proxy' network exists"
fi

# ── Start nginx-proxy ────────────────────────────────────
echo ""
echo "[STEP 1/5] Starting nginx-proxy (config: ${NGINX_CONF})..."
docker compose -f docker-compose.proxy.yml up -d --force-recreate

# ── Build & start RuleEngine backend ──────────────────────
echo ""
echo "[STEP 2/5] Building RuleEngine backend..."
echo "           (first build may take 5-10 minutes)"
docker compose -f docker-compose.yml build backend
docker compose -f docker-compose.yml up -d backend

# ── Build & start RuleEngine frontend ─────────────────────
echo ""
echo "[STEP 3/5] Building RuleEngine frontend..."
docker compose -f docker-compose.yml build frontend
docker compose -f docker-compose.yml up -d frontend

# ── Build & start RCM backend ────────────────────────────
echo ""
echo "[STEP 4/5] Building RCM backend..."
echo "           (first build may take 5-10 minutes)"
docker compose -f docker-compose.rcm.yml build rcm-backend
docker compose -f docker-compose.rcm.yml up -d rcm-backend

# ── Build & start RCM frontend ───────────────────────────
echo ""
echo "[STEP 5/5] Building RCM frontend..."
docker compose -f docker-compose.rcm.yml build rcm-frontend
docker compose -f docker-compose.rcm.yml up -d rcm-frontend

# ── Wait and check health ─────────────────────────────────
echo ""
echo "[INFO] Waiting for services to start..."
sleep 10

echo ""
echo "  Container status:"
for CONTAINER in nginx-proxy ruleengine-backend ruleengine-frontend rcm-backend rcm-frontend; do
    STATE=$(docker inspect --format='{{.State.Status}}' "$CONTAINER" 2>/dev/null || echo "not found")
    HEALTH=$(docker inspect --format='{{.State.Health.Status}}' "$CONTAINER" 2>/dev/null || echo "-")
    printf "    %-25s state=%-10s health=%s\n" "${CONTAINER}" "${STATE}" "${HEALTH}"
done

# ── Summary ──────────────────────────────────────────────
echo ""
echo "============================================"
echo "  Both Apps Deployed Successfully"
echo "============================================"
echo ""
echo "  RuleEngine:"
echo "    Frontend : ${SCHEME}://${RE_DOMAIN}"
echo "    Backend  : http://localhost:8081 (direct API)"
echo ""
echo "  RCM:"
echo "    Frontend : ${SCHEME}://${RCM_DOMAIN}"
echo "    Backend  : http://localhost:8082 (direct API)"
echo ""
echo "  Useful commands:"
echo "    Status   : ./deploy-init-all.sh --status"
echo "    Logs     : ./deploy-init-all.sh --logs"
echo "    Stop all : ./deploy-init-all.sh --down"
echo ""
echo "============================================"
