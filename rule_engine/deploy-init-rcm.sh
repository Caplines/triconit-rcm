#!/usr/bin/env bash
# ──────────────────────────────────────────────────────────────
# deploy-init-rcm.sh — Full deploy script for RCM
# ──────────────────────────────────────────────────────────────
#
# Usage:
#   ./deploy-init-rcm.sh --setup       Install Docker, Compose, certbot + create .env
#   ./deploy-init-rcm.sh --init-ssl    Obtain SSL certs via certbot
#   ./deploy-init-rcm.sh              Deploy with SSL (default)
#   ./deploy-init-rcm.sh --no-ssl     Deploy HTTP only (before certbot)
#   ./deploy-init-rcm.sh --status     Show container status
#   ./deploy-init-rcm.sh --logs       Tail container logs
#   ./deploy-init-rcm.sh --down       Stop all RCM + proxy services
#   ./deploy-init-rcm.sh --help       Show this help
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
            echo "Usage: ./deploy-init-rcm.sh [OPTIONS]"
            echo ""
            echo "Full deployment script for RCM (proxy + backend + frontend)."
            echo ""
            echo "First-time setup (run once on a fresh server):"
            echo "  --setup       Install Docker, Docker Compose, certbot, and create .env from template"
            echo "  --init-ssl    Obtain SSL certs via certbot for RCM_DOMAIN"
            echo ""
            echo "Deploy:"
            echo "  (no flags)    Deploy with SSL (default)"
            echo "  --no-ssl      Deploy HTTP only (skip SSL cert check)"
            echo ""
            echo "Manage:"
            echo "  --status      Show container health status"
            echo "  --logs        Tail container logs (Ctrl+C to stop)"
            echo "  --down        Stop all RCM + proxy services"
            echo ""
            echo "Typical fresh-server workflow:"
            echo "  1. ./deploy-init-rcm.sh --setup"
            echo "  2. nano .env                          # fill in your values"
            echo "  3. ./deploy-init-rcm.sh --no-ssl      # deploy HTTP first"
            echo "  4. ./deploy-init-rcm.sh --down        # stop to free port 80"
            echo "  5. ./deploy-init-rcm.sh --init-ssl    # get SSL certs"
            echo "  6. ./deploy-init-rcm.sh               # redeploy with SSL"
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
    echo "  Server Setup for RCM"
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
    echo "       ./deploy-init-rcm.sh --no-ssl"
    echo ""
    echo "    3. Or get SSL certs first, then deploy:"
    echo "       ./deploy-init-rcm.sh --init-ssl"
    echo "       ./deploy-init-rcm.sh"
    echo ""
    exit 0
fi

# ════════════════════════════════════════════════════════════
# --init-ssl: Obtain SSL certificates via certbot
# ════════════════════════════════════════════════════════════
if $INIT_SSL; then
    command -v certbot >/dev/null 2>&1 || { echo "[ERROR] certbot not installed. Run: ./deploy-init-rcm.sh --setup"; exit 1; }

    if [ ! -f .env ]; then
        echo "[ERROR] .env not found. Run --setup first, then edit .env."
        exit 1
    fi
    set -a; source .env; set +a

    if [ -z "${RCM_DOMAIN:-}" ]; then
        echo "[ERROR] RCM_DOMAIN is not set in .env"
        exit 1
    fi
    if [ -z "${RCM_SERVER_NAME:-}" ]; then
        echo "[ERROR] RCM_SERVER_NAME is not set in .env"
        exit 1
    fi

    CERT_DIR="/etc/letsencrypt/live/${RCM_DOMAIN}"
    if [ -d "$CERT_DIR" ]; then
        echo "[INFO] SSL cert already exists at ${CERT_DIR}"
        echo "       To renew: sudo certbot renew"
        echo "       To force new cert: sudo certbot certonly --force-renewal --standalone -d ${RCM_DOMAIN}"
        exit 0
    fi

    # Build certbot -d flags from RCM_SERVER_NAME (space-separated domains).
    # RCM_DOMAIN goes first so the cert directory name matches the nginx ssl_certificate path.
    CERTBOT_DOMAINS="-d ${RCM_DOMAIN}"
    for DOMAIN in ${RCM_SERVER_NAME}; do
        if [ "$DOMAIN" != "${RCM_DOMAIN}" ]; then
            CERTBOT_DOMAINS="${CERTBOT_DOMAINS} -d ${DOMAIN}"
        fi
    done

    echo "[INFO] Obtaining SSL certificate covering:"
    for DOMAIN in ${RCM_SERVER_NAME}; do
        echo "         - ${DOMAIN}"
    done
    echo ""
    echo "[INFO] Make sure:"
    echo "       1. Port 80 is free (stop proxy if running)"
    echo "       2. DNS for ALL domains above points to this server"
    echo ""

    docker compose -f docker-compose.proxy.yml down 2>/dev/null || true

    sudo certbot certonly --standalone ${CERTBOT_DOMAINS}

    if [ -d "$CERT_DIR" ]; then
        echo ""
        echo "[OK] SSL certificate obtained (covers all RCM_SERVER_NAME domains)"
        echo "     Cert location: ${CERT_DIR}/"
        echo "     Now deploy: ./deploy-init-rcm.sh"
    else
        echo "[ERROR] Certificate not found after certbot. Check certbot output above."
        exit 1
    fi
    exit 0
fi

# ════════════════════════════════════════════════════════════
# --status: Show container health status
# ════════════════════════════════════════════════════════════
if $STATUS; then
    echo "Service status:"
    for CONTAINER in nginx-proxy rcm-backend rcm-frontend; do
        STATE=$(docker inspect --format='{{.State.Status}}' "$CONTAINER" 2>/dev/null || echo "not found")
        HEALTH=$(docker inspect --format='{{.State.Health.Status}}' "$CONTAINER" 2>/dev/null || echo "-")
        printf "  %-25s state=%-10s health=%s\n" "${CONTAINER}" "${STATE}" "${HEALTH}"
    done
    echo ""
    docker compose -f docker-compose.rcm.yml ps 2>/dev/null || true
    exit 0
fi

# ════════════════════════════════════════════════════════════
# --logs: Tail container logs
# ════════════════════════════════════════════════════════════
if $LOGS; then
    echo "[INFO] Tailing RCM logs (Ctrl+C to stop)..."
    docker compose -f docker-compose.rcm.yml logs -f --tail 100
    exit 0
fi

# ════════════════════════════════════════════════════════════
# --down: Stop all services
# ════════════════════════════════════════════════════════════
if $DOWN; then
    echo "[INFO] Stopping RCM..."
    docker compose -f docker-compose.rcm.yml down 2>/dev/null || true
    echo "[INFO] Stopping proxy..."
    docker compose -f docker-compose.proxy.yml down 2>/dev/null || true
    echo "[OK] All services stopped."
    exit 0
fi

# ════════════════════════════════════════════════════════════
# Default: Deploy RCM
# ════════════════════════════════════════════════════════════
echo "========================================"
echo "  Deploying RCM"
echo "========================================"
echo ""

# ── Pre-flight checks ─────────────────────────────────────
echo "[INFO] Running pre-flight checks..."

command -v docker >/dev/null 2>&1 || { echo "[ERROR] Docker not installed. Run: ./deploy-init-rcm.sh --setup"; exit 1; }
docker compose version >/dev/null 2>&1 || { echo "[ERROR] Docker Compose v2 not installed. Run: ./deploy-init-rcm.sh --setup"; exit 1; }

if [ ! -f .env ]; then
    if [ -f .env.example ]; then
        cp .env.example .env
        echo "[INFO] Created .env from .env.example"
        echo ""
        echo "[ERROR] Please edit .env with your actual values before deploying:"
        echo "        nano .env"
        echo ""
        echo "  Required: DB_URL, DB_USERNAME, DB_PASSWORD, RCM_DOMAIN, RCM_SERVER_NAME"
        echo "  RCM-specific: MAIL_USERNAME, MAIL_PASSWORD, CAPTCHA_SECRET_KEY"
        exit 1
    else
        echo "[ERROR] .env file not found. Run: ./deploy-init-rcm.sh --setup"
        exit 1
    fi
fi

set -a; source .env; set +a

# ── Validate required variables ───────────────────────────
MISSING=()
[ -z "${RCM_DOMAIN:-}" ]      && MISSING+=("RCM_DOMAIN")
[ -z "${RCM_SERVER_NAME:-}" ] && MISSING+=("RCM_SERVER_NAME")
[ -z "${DB_URL:-}" ]          && MISSING+=("DB_URL")
[ -z "${DB_USERNAME:-}" ]     && MISSING+=("DB_USERNAME")
[ -z "${DB_PASSWORD:-}" ]     && MISSING+=("DB_PASSWORD")

if [ ${#MISSING[@]} -gt 0 ]; then
    echo "[ERROR] Missing required variables in .env: ${MISSING[*]}"
    exit 1
fi

echo "[OK] .env validated (RCM_DOMAIN=${RCM_DOMAIN})"

# ── Select nginx config ──────────────────────────────────
if $NO_SSL; then
    export NGINX_CONF=nginx-nossl.conf
    SCHEME="http"
    echo "[INFO] Mode: HTTP only (no SSL)"
else
    export NGINX_CONF=nginx-ssl-rcm-only.conf
    SCHEME="https"
    CERT_DIR="/etc/letsencrypt/live/${RCM_DOMAIN}"
    if [ ! -d "$CERT_DIR" ]; then
        echo "[WARN] SSL cert not found at: $CERT_DIR"
        echo ""
        echo "  Options:"
        echo "    1. Get certs:    ./deploy-init-rcm.sh --init-ssl"
        echo "    2. Deploy HTTP:  ./deploy-init-rcm.sh --no-ssl"
        exit 1
    fi
    echo "[OK] SSL certs found for ${RCM_DOMAIN}"
fi

# ── Create shared Docker network ─────────────────────────
if docker network create shared-proxy 2>/dev/null; then
    echo "[OK] Created 'shared-proxy' Docker network"
else
    echo "[OK] 'shared-proxy' network exists"
fi

# ── Start nginx-proxy ────────────────────────────────────
echo ""
echo "[STEP 1/3] Starting nginx-proxy (config: ${NGINX_CONF})..."
docker compose -f docker-compose.proxy.yml up -d --force-recreate

# ── Build & start backend ────────────────────────────────
echo ""
echo "[STEP 2/3] Building and starting RCM backend..."
echo "           (first build may take 5-10 minutes)"
docker compose -f docker-compose.rcm.yml build rcm-backend
docker compose -f docker-compose.rcm.yml up -d rcm-backend

# ── Build & start frontend ───────────────────────────────
echo ""
echo "[STEP 3/3] Building and starting RCM frontend..."
docker compose -f docker-compose.rcm.yml build rcm-frontend
docker compose -f docker-compose.rcm.yml up -d rcm-frontend

# ── Wait and check health ─────────────────────────────────
echo ""
echo "[INFO] Waiting for services to start..."
sleep 10

echo ""
echo "  Container status:"
for CONTAINER in nginx-proxy rcm-backend rcm-frontend; do
    STATE=$(docker inspect --format='{{.State.Status}}' "$CONTAINER" 2>/dev/null || echo "not found")
    HEALTH=$(docker inspect --format='{{.State.Health.Status}}' "$CONTAINER" 2>/dev/null || echo "-")
    printf "    %-25s state=%-10s health=%s\n" "${CONTAINER}" "${STATE}" "${HEALTH}"
done

# ── Summary ──────────────────────────────────────────────
echo ""
echo "============================================"
echo "  RCM Deployment Complete"
echo "============================================"
echo ""
echo "  Frontend : ${SCHEME}://${RCM_DOMAIN}"
echo "  Backend  : http://localhost:8082 (direct API)"
echo ""
echo "  Useful commands:"
echo "    Status   : ./deploy-init-rcm.sh --status"
echo "    Logs     : ./deploy-init-rcm.sh --logs"
echo "    Rebuild  : docker compose -f docker-compose.rcm.yml up --build -d"
echo "    Stop     : ./deploy-init-rcm.sh --down"
echo ""
if [ "$(docker inspect --format='{{.State.Health.Status}}' rcm-backend 2>/dev/null)" = "starting" ]; then
    echo "  [INFO] Backend is still starting up (Spring Boot takes ~90s)."
    echo "         Run './deploy-init-rcm.sh --status' in a minute to verify."
    echo ""
fi
echo "============================================"
