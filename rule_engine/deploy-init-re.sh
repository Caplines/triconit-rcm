#!/usr/bin/env bash
# ──────────────────────────────────────────────────────────────
# deploy-init-re.sh — Full deploy script for RuleEngine
# ──────────────────────────────────────────────────────────────
#
# Usage:
#   ./deploy-init-re.sh --setup       Install Docker, Compose, certbot + create .env
#   ./deploy-init-re.sh --init-ssl    Obtain SSL certs via certbot
#   ./deploy-init-re.sh              Deploy with SSL (default)
#   ./deploy-init-re.sh --no-ssl     Deploy HTTP only (before certbot)
#   ./deploy-init-re.sh --status     Show container status
#   ./deploy-init-re.sh --logs       Tail container logs
#   ./deploy-init-re.sh --down       Stop all RuleEngine + proxy services
#   ./deploy-init-re.sh --help       Show this help
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
            echo "Usage: ./deploy-init-re.sh [OPTIONS]"
            echo ""
            echo "Full deployment script for RuleEngine (proxy + backend + frontend)."
            echo ""
            echo "First-time setup (run once on a fresh server):"
            echo "  --setup       Install Docker, Docker Compose, certbot, and create .env from template"
            echo "  --init-ssl    Obtain SSL certs via certbot for RE_DOMAIN"
            echo ""
            echo "Deploy:"
            echo "  (no flags)    Deploy with SSL (default)"
            echo "  --no-ssl      Deploy HTTP only (skip SSL cert check)"
            echo ""
            echo "Manage:"
            echo "  --status      Show container health status"
            echo "  --logs        Tail container logs (Ctrl+C to stop)"
            echo "  --down        Stop all RuleEngine + proxy services"
            echo ""
            echo "Typical fresh-server workflow:"
            echo "  1. ./deploy-init-re.sh --setup"
            echo "  2. nano .env                          # fill in your values"
            echo "  3. ./deploy-init-re.sh --no-ssl       # deploy HTTP first"
            echo "  4. ./deploy-init-re.sh --down         # stop to free port 80"
            echo "  5. ./deploy-init-re.sh --init-ssl     # get SSL certs"
            echo "  6. ./deploy-init-re.sh                # redeploy with SSL"
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
    echo "  Server Setup for RuleEngine"
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

        # Add current user to docker group (avoids needing sudo for docker commands)
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
    echo "       ./deploy-init-re.sh --no-ssl"
    echo ""
    echo "    3. Or get SSL certs first, then deploy:"
    echo "       ./deploy-init-re.sh --init-ssl"
    echo "       ./deploy-init-re.sh"
    echo ""
    exit 0
fi

# ════════════════════════════════════════════════════════════
# --init-ssl: Obtain SSL certificates via certbot
# ════════════════════════════════════════════════════════════
if $INIT_SSL; then
    command -v certbot >/dev/null 2>&1 || { echo "[ERROR] certbot not installed. Run: ./deploy-init-re.sh --setup"; exit 1; }

    if [ ! -f .env ]; then
        echo "[ERROR] .env not found. Run --setup first, then edit .env."
        exit 1
    fi
    set -a; source .env; set +a

    if [ -z "${RE_DOMAIN:-}" ]; then
        echo "[ERROR] RE_DOMAIN is not set in .env"
        exit 1
    fi
    if [ -z "${RE_SERVER_NAME:-}" ]; then
        echo "[ERROR] RE_SERVER_NAME is not set in .env"
        exit 1
    fi

    CERT_DIR="/etc/letsencrypt/live/${RE_DOMAIN}"
    if [ -d "$CERT_DIR" ]; then
        echo "[INFO] SSL cert already exists at ${CERT_DIR}"
        echo "       To renew: sudo certbot renew"
        echo "       To force new cert: sudo certbot certonly --force-renewal --standalone -d ${RE_DOMAIN}"
        exit 0
    fi

    # Build certbot -d flags from RE_SERVER_NAME (space-separated domains).
    # RE_DOMAIN goes first so the cert directory name matches the nginx ssl_certificate path.
    CERTBOT_DOMAINS="-d ${RE_DOMAIN}"
    for DOMAIN in ${RE_SERVER_NAME}; do
        if [ "$DOMAIN" != "${RE_DOMAIN}" ]; then
            CERTBOT_DOMAINS="${CERTBOT_DOMAINS} -d ${DOMAIN}"
        fi
    done

    echo "[INFO] Obtaining SSL certificate covering:"
    for DOMAIN in ${RE_SERVER_NAME}; do
        echo "         - ${DOMAIN}"
    done
    echo ""
    echo "[INFO] Make sure:"
    echo "       1. Port 80 is free (stop proxy if running)"
    echo "       2. DNS for ALL domains above points to this server"
    echo ""

    # Stop proxy if running (needs port 80)
    docker compose -f docker-compose.proxy.yml down 2>/dev/null || true

    sudo certbot certonly --standalone ${CERTBOT_DOMAINS}

    if [ -d "$CERT_DIR" ]; then
        echo ""
        echo "[OK] SSL certificate obtained (covers all RE_SERVER_NAME domains)"
        echo "     Cert location: ${CERT_DIR}/"
        echo "     Now deploy: ./deploy-init-re.sh"
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
    for CONTAINER in nginx-proxy ruleengine-backend ruleengine-frontend; do
        STATE=$(docker inspect --format='{{.State.Status}}' "$CONTAINER" 2>/dev/null || echo "not found")
        HEALTH=$(docker inspect --format='{{.State.Health.Status}}' "$CONTAINER" 2>/dev/null || echo "-")
        printf "  %-25s state=%-10s health=%s\n" "${CONTAINER}" "${STATE}" "${HEALTH}"
    done
    echo ""
    docker compose -f docker-compose.yml ps 2>/dev/null || true
    exit 0
fi

# ════════════════════════════════════════════════════════════
# --logs: Tail container logs
# ════════════════════════════════════════════════════════════
if $LOGS; then
    echo "[INFO] Tailing RuleEngine logs (Ctrl+C to stop)..."
    docker compose -f docker-compose.yml logs -f --tail 100
    exit 0
fi

# ════════════════════════════════════════════════════════════
# --down: Stop all services
# ════════════════════════════════════════════════════════════
if $DOWN; then
    echo "[INFO] Stopping RuleEngine..."
    docker compose -f docker-compose.yml down 2>/dev/null || true
    echo "[INFO] Stopping proxy..."
    docker compose -f docker-compose.proxy.yml down 2>/dev/null || true
    echo "[OK] All services stopped."
    exit 0
fi

# ════════════════════════════════════════════════════════════
# Default: Deploy RuleEngine
# ════════════════════════════════════════════════════════════
echo "========================================"
echo "  Deploying RuleEngine"
echo "========================================"
echo ""

# ── Pre-flight checks ─────────────────────────────────────
echo "[INFO] Running pre-flight checks..."

command -v docker >/dev/null 2>&1 || { echo "[ERROR] Docker not installed. Run: ./deploy-init-re.sh --setup"; exit 1; }
docker compose version >/dev/null 2>&1 || { echo "[ERROR] Docker Compose v2 not installed. Run: ./deploy-init-re.sh --setup"; exit 1; }

if [ ! -f .env ]; then
    if [ -f .env.example ]; then
        cp .env.example .env
        echo "[INFO] Created .env from .env.example"
        echo ""
        echo "[ERROR] Please edit .env with your actual values before deploying:"
        echo "        nano .env"
        echo ""
        echo "  Required: DB_URL, DB_USERNAME, DB_PASSWORD, RE_DOMAIN, RE_SERVER_NAME, JWT_SECRET"
        exit 1
    else
        echo "[ERROR] .env file not found. Run: ./deploy-init-re.sh --setup"
        exit 1
    fi
fi

set -a; source .env; set +a

# ── Validate required variables ───────────────────────────
MISSING=()
[ -z "${RE_DOMAIN:-}" ]      && MISSING+=("RE_DOMAIN")
[ -z "${RE_SERVER_NAME:-}" ] && MISSING+=("RE_SERVER_NAME")
[ -z "${DB_URL:-}" ]         && MISSING+=("DB_URL")
[ -z "${DB_USERNAME:-}" ]    && MISSING+=("DB_USERNAME")
[ -z "${DB_PASSWORD:-}" ]    && MISSING+=("DB_PASSWORD")

if [ ${#MISSING[@]} -gt 0 ]; then
    echo "[ERROR] Missing required variables in .env: ${MISSING[*]}"
    exit 1
fi

echo "[OK] .env validated (RE_DOMAIN=${RE_DOMAIN})"

# ── Select nginx config ──────────────────────────────────
if $NO_SSL; then
    export NGINX_CONF=nginx-nossl.conf
    SCHEME="http"
    echo "[INFO] Mode: HTTP only (no SSL)"
else
    export NGINX_CONF=nginx-ssl-re-only.conf
    SCHEME="https"
    CERT_DIR="/etc/letsencrypt/live/${RE_DOMAIN}"
    if [ ! -d "$CERT_DIR" ]; then
        echo "[WARN] SSL cert not found at: $CERT_DIR"
        echo ""
        echo "  Options:"
        echo "    1. Get certs:    ./deploy-init-re.sh --init-ssl"
        echo "    2. Deploy HTTP:  ./deploy-init-re.sh --no-ssl"
        exit 1
    fi
    echo "[OK] SSL certs found for ${RE_DOMAIN}"
fi

# ── EagleSoft certs check ────────────────────────────────
if [ "${ES_SSL_CLIENT_TRUSTALL:-false}" = "false" ]; then
    if [ ! -f "${CERTS_PATH:-./certs}/cacerts.jks" ]; then
        echo "[WARN] EagleSoft truststore not found at ${CERTS_PATH:-./certs}/cacerts.jks"
        echo "       Backend will fail to connect to EagleSoft."
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
echo "[STEP 1/3] Starting nginx-proxy (config: ${NGINX_CONF})..."
docker compose -f docker-compose.proxy.yml up -d --force-recreate

# ── Build & start backend ────────────────────────────────
echo ""
echo "[STEP 2/3] Building and starting RuleEngine backend..."
echo "           (first build may take 5-10 minutes)"
docker compose -f docker-compose.yml build backend
docker compose -f docker-compose.yml up -d backend

# ── Build & start frontend ───────────────────────────────
echo ""
echo "[STEP 3/3] Building and starting RuleEngine frontend..."
docker compose -f docker-compose.yml build frontend
docker compose -f docker-compose.yml up -d frontend

# ── Wait and check health ─────────────────────────────────
echo ""
echo "[INFO] Waiting for services to start..."
sleep 10

echo ""
echo "  Container status:"
for CONTAINER in nginx-proxy ruleengine-backend ruleengine-frontend; do
    STATE=$(docker inspect --format='{{.State.Status}}' "$CONTAINER" 2>/dev/null || echo "not found")
    HEALTH=$(docker inspect --format='{{.State.Health.Status}}' "$CONTAINER" 2>/dev/null || echo "-")
    printf "    %-25s state=%-10s health=%s\n" "${CONTAINER}" "${STATE}" "${HEALTH}"
done

# ── Summary ──────────────────────────────────────────────
echo ""
echo "============================================"
echo "  RuleEngine Deployment Complete"
echo "============================================"
echo ""
echo "  Frontend : ${SCHEME}://${RE_DOMAIN}"
echo "  Backend  : http://localhost:8081 (direct API)"
echo ""
echo "  Useful commands:"
echo "    Status   : ./deploy-init-re.sh --status"
echo "    Logs     : ./deploy-init-re.sh --logs"
echo "    Rebuild  : docker compose -f docker-compose.yml up --build -d"
echo "    Stop     : ./deploy-init-re.sh --down"
echo ""
if [ "$(docker inspect --format='{{.State.Health.Status}}' ruleengine-backend 2>/dev/null)" = "starting" ]; then
    echo "  [INFO] Backend is still starting up (Spring Boot takes ~60s)."
    echo "         Run './deploy-init-re.sh --status' in a minute to verify."
    echo ""
fi
echo "============================================"
