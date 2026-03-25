# Deployment Guide — RuleEngine & RCM

This document is the single source of truth for how both applications are built, pushed, and deployed. Read it fully before touching anything on a server.

---

## Table of Contents

1. [How the System Works](#1-how-the-system-works)
2. [Repository Layout](#2-repository-layout)
3. [Architecture Overview](#3-architecture-overview)
4. [Environment Variables](#4-environment-variables)
5. [Developer Machine Setup](#5-developer-machine-setup)
6. [Building and Pushing Images](#6-building-and-pushing-images)
7. [Server Setup (First Time)](#7-server-setup-first-time)
8. [Deploying](#8-deploying)
9. [Updating a Running Deployment](#9-updating-a-running-deployment)
10. [Rolling Back](#10-rolling-back)
11. [SSL / HTTPS Setup](#11-ssl--https-setup)
12. [Local Development](#12-local-development)
13. [EagleSoft SSL Certs (RuleEngine only)](#13-eaglesoft-ssl-certs-ruleengine-only)
14. [Ports & URLs Reference](#14-ports--urls-reference)
15. [Troubleshooting](#15-troubleshooting)

---

## 1. How the System Works

### The golden rule

> **Builds happen on the developer's machine. Servers only pull and run images.**

Servers never run `docker build`. This keeps server CPU free, deployments fast, and production stable.

### The flow

```
Developer machine                    Docker Hub                    Server
─────────────────                    ──────────────                ──────
Write code
  │
  ▼
./build-push.sh rcm    ──────────►  capline/app:rcm-backend-latest
                                    capline/app:rcm-backend-2024.06.01-1430
                                    capline/app:rcm-frontend-latest
                                    capline/app:rcm-frontend-2024.06.01-1430
                                                                      │
                                                                      ▼
                                                                ./deploy.sh rcm
                                                                (pulls + restarts)
```

### Two independent apps, two independent servers

| App | Server | Compose file used on server |
|-----|--------|-----------------------------|
| RuleEngine | Server A | `docker-compose.prod.yml` |
| RCM | Server B | `docker-compose.rcm.prod.yml` |

Each server is completely independent. They share nothing at runtime. The proxy, containers, and volumes on Server A have no relationship to Server B.

### What runs on each server

```
Server (either one)
│
├── nginx-proxy          ← single entry point, owns ports 80 and 443
│   └── routes by domain → app frontend container
│
├── app-frontend         ← serves Angular over HTTP on port 80 (internal only)
│   └── proxies /api/v1/* → app backend
│
└── app-backend          ← Spring Boot, also exposed on a host port for direct API access
```

---

## 2. Repository Layout

All commands are run from the `rule_engine/` directory unless stated otherwise.

```
rule_engine/
├── .env.example                    # Template — copy to .env and fill in values
├── .env                            # Your actual config (NEVER commit this)
│
├── build-push.sh                   # DEV MACHINE ONLY — build images and push to Docker Hub
├── deploy.sh                       # SERVER ONLY — pull images and restart containers
│
├── docker-compose.prod.yml         # RuleEngine production stack (image: only, no build:)
├── docker-compose.rcm.prod.yml     # RCM production stack (image: only, no build:)
├── docker-compose.proxy.yml        # Shared nginx reverse proxy (used on both servers)
│
├── docker-compose.local.yml        # RuleEngine local development (uses build:)
├── docker-compose.rcm.local.yml    # RCM local development (uses build:)
│
├── nginx-proxy/
│   ├── nginx-ssl.conf              # HTTPS, both apps
│   ├── nginx-ssl-re-only.conf      # HTTPS, RuleEngine only
│   ├── nginx-ssl-rcm-only.conf     # HTTPS, RCM only
│   └── nginx-nossl.conf            # HTTP only (no SSL — use before domain/certbot)
│
├── ruleengine/
│   └── Dockerfile
├── ruleengine-client-app/
│   ├── Dockerfile
│   ├── nginx.prod.conf
│   └── nginx.local.conf
├── rcm/
│   └── Dockerfile
└── rcm-client-app/
    ├── Dockerfile
    ├── nginx.prod.conf
    ├── nginx.prod-nossl.conf
    └── nginx.local.conf
```

### Key distinction: prod vs local compose files

| File | Has `build:` | Has `image:` | Use on |
|------|-------------|--------------|--------|
| `docker-compose.prod.yml` | No | Yes | Server A |
| `docker-compose.rcm.prod.yml` | No | Yes | Server B |
| `docker-compose.local.yml` | Yes | No | Dev machine |
| `docker-compose.rcm.local.yml` | Yes | No | Dev machine |

---

## 3. Architecture Overview

```
                    Internet (HTTPS :443 / HTTP :80)
                                    │
                                    ▼
                  ┌──────────────────────────────────┐
                  │         nginx-proxy              │
                  │   Listens on ports 80 and 443    │
                  │   Routes traffic by domain name  │
                  │   Handles SSL termination        │
                  └────────────────┬─────────────────┘
                                   │
            ┌──────────────────────┼─────────────────────┐
            │                                            │
     RE_SERVER_NAME                               RCM_SERVER_NAME
            │                                            │
            ▼                                            ▼
   ┌──────────────────┐                       ┌──────────────────┐
   │ ruleengine-      │                       │ rcm-frontend     │
   │ frontend         │                       │ port 80 internal │
   │ port 80 internal │                       └────────┬─────────┘
   └────────┬─────────┘                                │
            │  /api/v1/* →                             │  /api/v1/* →
            │  backend:8080                            │  rcm-backend:8081
            ▼                                          ▼
   ┌──────────────────┐                       ┌──────────────────┐
   │ ruleengine-      │                       │ rcm-backend      │
   │ backend          │                       │ host 8082:8081   │
   │ host 8081:8080   │                       └──────────────────┘
   └──────────────────┘

   All containers join the Docker network: shared-proxy
```

**Key points:**

- `nginx-proxy` is the only container that binds host ports 80 and 443. Nothing else is publicly exposed except the direct backend ports (8081/8082) for API tools like Postman.
- App frontend containers are only reachable via the proxy — they have no host port binding.
- The proxy uses Docker's internal DNS (`127.0.0.11`) with lazy resolution, so it starts successfully even if app containers aren't running yet. A missing app returns 502 until it comes up.
- Nginx configs are mounted as templates; environment variables like `${RE_SERVER_NAME}` are substituted at container startup.

---

## 4. Environment Variables

### Setup

```bash
cp .env.example .env
nano .env
```

Never commit `.env` to git. The `.env.example` file is the committed template.

### Variable reference

| Group | Variable | Example | Notes |
|-------|----------|---------|-------|
| **Docker Hub** | `DOCKER_HUB_REPO` | `capline/myapp` | Repo where all images are pushed |
| **Versions** | `RE_VERSION` | `2024.06.01-1430` | Image tag to pull for RE. Use `latest` or a specific version. |
| | `RCM_VERSION` | `latest` | Same for RCM |
| **Database** | `DB_URL` | `jdbc:mysql://host:3306/db` | Shared by both apps |
| | `DB_USERNAME` | `capline_admin` | |
| | `DB_PASSWORD` | `...` | |
| **Domains** | `RE_DOMAIN` | `ruleengine.example.com` | Single domain — used for SSL cert path |
| | `RE_SERVER_NAME` | `ruleengine.com www.ruleengine.com` | Space-separated — all domains nginx responds to |
| | `RCM_DOMAIN` | `rcm.example.com` | Single domain — used for SSL cert path |
| | `RCM_SERVER_NAME` | `rcm.example.com` | Same as above but for RCM |
| **Proxy** | `NGINX_CONF` | `nginx-ssl-rcm-only.conf` | Which nginx config the proxy loads |
| **JWT** | `JWT_SECRET` | `change-this` | Auth token signing key |
| | `JWT_EXPIRATION` | `600000` | Token expiry in ms |
| **EagleSoft SSL** | `ES_SSL_CLIENT_TRUSTALL` | `true` | Set `true` for dev/testing, `false` for production |
| | `ES_SSL_CLIENT_PASSWORD` | `changeit` | Keystore password |
| **JVM** | `JAVA_OPTS` | `-Xms512m -Xmx2048m` | RE backend heap size |
| **CORS** | `CORS_ALLOWED_ORIGINS` | `https://domain.com` | Comma-separated allowed origins |
| **Mail** | `MAIL_USERNAME` | `you@gmail.com` | RCM email sending |
| | `MAIL_PASSWORD` | `app-password` | Gmail app password |
| **reCAPTCHA** | `CAPTCHA_SECRET_KEY` | `6Ld...` | RCM login captcha |
| **Timeouts** | `PROXY_READ_TIMEOUT` | `1800s` | Nginx backend timeout |
| | `PROXY_SEND_TIMEOUT` | `1800s` | |

### Domain variables explained

`RE_DOMAIN` is a **single domain** — it's used only to build the SSL cert path:
```
/etc/letsencrypt/live/${RE_DOMAIN}/fullchain.pem
```
It must match exactly what you used with certbot.

`RE_SERVER_NAME` is a **space-separated list** of all domains nginx should respond to. It maps to the nginx `server_name` directive. If you only have one domain, set both to the same value.

### NGINX_CONF options

| Value | SSL needed for | When to use |
|-------|---------------|-------------|
| `nginx-ssl.conf` | Both RE + RCM | Both apps on same server, HTTPS |
| `nginx-ssl-re-only.conf` | RE domain only | RuleEngine server, HTTPS |
| `nginx-ssl-rcm-only.conf` | RCM domain only | RCM server, HTTPS |
| `nginx-nossl.conf` | None | HTTP only — no domain yet, or before certbot |

### Server A vs Server B .env differences

The two servers share most variables but differ on:

| Variable | Server A (RuleEngine) | Server B (RCM) |
|----------|-----------------------|----------------|
| `NGINX_CONF` | `nginx-ssl-re-only.conf` | `nginx-ssl-rcm-only.conf` |
| `RE_DOMAIN` / `RE_SERVER_NAME` | Your RE domain | Not needed |
| `RCM_DOMAIN` / `RCM_SERVER_NAME` | Not needed | Your RCM domain |
| `ES_SSL_CLIENT_TRUSTALL` | `false` in prod | Not used |
| `JAVA_OPTS` | Set heap size | Not needed |

---

## 5. Developer Machine Setup

This is a one-time setup on your local machine.

### Prerequisites

- Docker Desktop installed and running
- Logged in to Docker Hub: `docker login`

### Create the buildx builder (one time only)

```bash
docker buildx create --name multibuilder --use
docker buildx inspect --bootstrap
```

This creates a multi-platform builder that can produce images for both AMD64 (Linux servers) and ARM64 (Apple Silicon Macs). **This step is required.** Without it, images built on a Mac will crash on Linux servers with `exec format error`.

### Make scripts executable

```bash
chmod +x build-push.sh deploy.sh
```

### Set your Docker Hub repo

Add to `.env` (or export in your shell):
```bash
DOCKER_HUB_REPO=yourdockerhub/capline
```

---

## 6. Building and Pushing Images

Run `build-push.sh` from your **developer machine** only. Never run this on a server.

### Usage

```bash
./build-push.sh re              # Build + push RuleEngine (backend + frontend)
./build-push.sh rcm             # Build + push RCM (backend + frontend)
./build-push.sh all             # Build + push everything

./build-push.sh re --be-only    # RuleEngine backend only
./build-push.sh re --fe-only    # RuleEngine frontend only
./build-push.sh rcm --be-only   # RCM backend only
./build-push.sh rcm --fe-only   # RCM frontend only
```

### Versioning

By default, images are tagged with a timestamp: `YYYY.MM.DD-HHmm`

```bash
# Default — auto timestamp
./build-push.sh rcm
# Pushes: capline/app:rcm-backend-2024.06.01-1430
#         capline/app:rcm-backend-latest
#         capline/app:rcm-frontend-2024.06.01-1430
#         capline/app:rcm-frontend-latest

# Override the version
VERSION=1.2.3 ./build-push.sh rcm
```

Every push updates **both** tags:
- `latest` — always points to the most recently pushed build
- `2024.06.01-1430` — permanent snapshot, used for rollbacks

### Why multi-platform builds?

If you develop on an Apple Silicon Mac (ARM64) and deploy to a standard Linux VPS (AMD64), the binary formats are incompatible. The script uses `docker buildx --platform linux/amd64,linux/arm64` to build for both architectures at once. Docker Hub stores both variants under the same tag and the server automatically pulls the right one.

**Important:** With buildx multi-platform builds, `--push` is mandatory. The image goes directly to Docker Hub — it cannot be loaded into your local Docker daemon. You cannot run a multi-platform build locally, only push it.

---

## 7. Server Setup (First Time)

Run these steps once on each fresh server. Server A and Server B follow the same process — just use the correct app target (`re` or `rcm`).

### Step 1 — Install Docker

```bash
curl -fsSL https://get.docker.com | sh
sudo systemctl enable docker
sudo systemctl start docker
sudo usermod -aG docker $USER
newgrp docker
```

### Step 2 — Clone the repo

```bash
git clone <your-repo-url>
cd rule_engine
chmod +x deploy.sh
```

### Step 3 — Configure .env

```bash
cp .env.example .env
nano .env
```

**Minimum required for RCM server:**
```env
DOCKER_HUB_REPO=capline/myapp
RCM_DOMAIN=<your-server-ip-or-domain>
RCM_SERVER_NAME=<your-server-ip-or-domain>
NGINX_CONF=nginx-nossl.conf
DB_URL=jdbc:mysql://your-db-host:3306/dbname?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
DB_USERNAME=your_db_user
DB_PASSWORD=your_db_password
MAIL_USERNAME=your-email@example.com
MAIL_PASSWORD=your-app-password
CAPTCHA_SECRET_KEY=your-recaptcha-key
```

**Minimum required for RuleEngine server:**
```env
DOCKER_HUB_REPO=capline/myapp
RE_DOMAIN=<your-server-ip-or-domain>
RE_SERVER_NAME=<your-server-ip-or-domain>
NGINX_CONF=nginx-nossl.conf
DB_URL=jdbc:mysql://...
DB_USERNAME=...
DB_PASSWORD=...
ES_SSL_CLIENT_TRUSTALL=true
JAVA_OPTS=-Xms512m -Xmx2048m
```

> **No domain yet?** Use the server's public IP address for both `*_DOMAIN` and `*_SERVER_NAME`, and set `NGINX_CONF=nginx-nossl.conf`. The app will be accessible at `http://<ip>`. See [Section 11](#11-ssl--https-setup) for adding SSL later.

### Step 4 — Create the Docker network

```bash
docker network create shared-proxy
```

This only needs to be done once. The proxy and app containers communicate through this network.

### Step 5 — Deploy

```bash
# For RCM server:
./deploy.sh rcm

# For RuleEngine server:
./deploy.sh re
```

The script will: start the proxy, pull images from Docker Hub, start containers, and print status.

### First-time deploy checklist

```
[ ] Docker installed
[ ] Repo cloned
[ ] .env filled in with real values
[ ] shared-proxy network created
[ ] docker login run (if repo is private)
[ ] ./deploy.sh rcm (or re) run successfully
[ ] Containers show state=running in status output
```

---

## 8. Deploying

### Standard deploy (latest images)

```bash
# On RCM server
./deploy.sh rcm

# On RuleEngine server
./deploy.sh re
```

### Deploy a specific version

```bash
./deploy.sh rcm --version 2024.06.01-1430
```

This is useful when you want to deploy a specific build rather than whatever `latest` points to.

### What deploy.sh does

1. Loads `.env`
2. Ensures `shared-proxy` network exists (creates it if not)
3. Starts the proxy if it isn't already running
4. Runs `docker compose pull` to fetch the image from Docker Hub
5. Runs `docker compose up -d` to restart containers with the new image
6. Prints container status

### Check status

```bash
./deploy.sh --status
```

Output shows each container's state, health, and which image it's running.

---

## 9. Updating a Running Deployment

### Update everything

```bash
# On developer machine — build and push new images
./build-push.sh rcm

# On server — pull and restart
./deploy.sh rcm
```

### Update only the backend (frontend unchanged)

If only backend code changed, rebuild and push just the backend:

```bash
# Dev machine
./build-push.sh rcm --be-only

# Server — update only the backend container, leave frontend running
./deploy.sh rcm --service rcm-backend
```

The `--service` flag uses `--no-deps` internally, so only that one container is restarted.

### Update only the frontend

```bash
# Dev machine
./build-push.sh rcm --fe-only

# Server
./deploy.sh rcm --service rcm-frontend
```

### Minimising downtime

Docker replaces containers in place. The window where a service is unavailable is typically 5–15 seconds — the time it takes to stop the old container and start the new one. The proxy returns 502 during this window. For most use cases this is acceptable. If zero downtime is required, that needs a blue-green or replica setup which is a larger change.

---

## 10. Rolling Back

Every build pushes a versioned tag. To roll back, deploy the previous version:

```bash
# Roll back RCM to a specific version
./deploy.sh --rollback rcm 2024.06.01-1200

# Roll back RuleEngine
./deploy.sh --rollback re 2024.05.30-0900
```

Find available versions by checking Docker Hub tags for your repo.

---

## 11. SSL / HTTPS Setup

### If you don't have a domain yet

Use `NGINX_CONF=nginx-nossl.conf` and set `*_DOMAIN` and `*_SERVER_NAME` to the server's IP address. The app runs over HTTP on port 80. This is fine for testing.

### When you get a domain

1. Point your domain's DNS A-record to the server's public IP
2. Wait for DNS to propagate (check with `ping your-domain.com`)
3. Install certbot and get a certificate:

```bash
sudo apt install certbot -y

# Stop proxy first — certbot needs port 80
docker compose -f docker-compose.proxy.yml down

# Get the cert
sudo certbot certonly --standalone -d your-domain.com

# Verify cert was created
ls /etc/letsencrypt/live/your-domain.com/
```

4. Update `.env`:

```env
NGINX_CONF=nginx-ssl-rcm-only.conf    # or nginx-ssl-re-only.conf for RuleEngine
RCM_DOMAIN=your-domain.com
RCM_SERVER_NAME=your-domain.com
```

5. Redeploy:

```bash
./deploy.sh rcm
```

### Multiple domains on one server

If you're running both apps on one server, both need certs:

```bash
sudo certbot certonly --standalone -d ruleengine.example.com
sudo certbot certonly --standalone -d rcm.example.com
```

Then set `NGINX_CONF=nginx-ssl.conf` (serves both apps).

### Certificate renewal

Certbot sets up auto-renewal. After renewal, restart the proxy to pick up the new cert:

```bash
docker compose -f docker-compose.proxy.yml restart
```

---

## 12. Local Development

Local dev uses `docker-compose.local.yml` files which build images from source. No Docker Hub involved.

### RuleEngine

```bash
cd rule_engine
docker compose -f docker-compose.local.yml up --build
```

| URL | Purpose |
|-----|---------|
| `http://localhost:4200` | Angular frontend |
| `http://localhost:8081` | Backend API |

### RCM

```bash
cd rule_engine
docker compose -f docker-compose.rcm.local.yml up --build
```

| URL | Purpose |
|-----|---------|
| `http://localhost:4201` | Angular frontend |
| `http://localhost:8082` | Backend API |

### Running both locally

```bash
# Terminal 1
docker compose -f docker-compose.local.yml up --build

# Terminal 2
docker compose -f docker-compose.rcm.local.yml up --build
```

The two stacks use separate Docker networks so they don't conflict.

### Stop local dev

```bash
docker compose -f docker-compose.local.yml down
docker compose -f docker-compose.rcm.local.yml down
```

---

## 13. EagleSoft SSL Certs (RuleEngine only)

The RuleEngine backend connects to EagleSoft over TLS. This requires cert files in `certs/`.

### Quick option: disable cert verification (dev/testing only)

```env
ES_SSL_CLIENT_TRUSTALL=true
```

No cert files needed. The backend accepts any EagleSoft certificate. **Do not use this in production.**

### Production setup

1. Set `ES_SSL_CLIENT_TRUSTALL=false` in `.env`
2. Get the EagleSoft server's certificate (`es-server.cer`)
3. Create the cert files:

```bash
mkdir -p certs && cd certs

# Import ES cert into truststore
keytool -importcert -alias eaglesoft -file es-server.cer \
  -keystore cacerts.jks -storepass changeit -noprompt

# Create client keystore
keytool -genkeypair -alias client -keyalg RSA -keysize 2048 \
  -validity 365 -keystore keystore.jks -storepass changeit \
  -keypass changeit -dname "CN=RuleEngine"
```

4. Confirm `certs/cacerts.jks` and `certs/keystore.jks` exist
5. `ES_SSL_CLIENT_PASSWORD` in `.env` must match the password used above (default: `changeit`)

### Local dev certs (self-signed)

```bash
./scripts/generate-es-ssl-certs.sh
```

See `docs/eaglesoft-ssl-certs.md` for full details.

---

## 14. Ports & URLs Reference

| Container | Host Port | Internal Port | How to reach |
|-----------|-----------|---------------|--------------|
| nginx-proxy | 80, 443 | 80, 443 | Entry point for all browser traffic |
| ruleengine-backend | 8081 | 8080 | `http://server:8081` — direct API (Postman) |
| ruleengine-frontend | — | 80 | Via nginx-proxy only |
| rcm-backend | 8082 | 8081 | `http://server:8082` — direct API (Postman) |
| rcm-frontend | — | 80 | Via nginx-proxy only |

Frontend containers have no host port binding. The only way to reach them from outside is through the proxy.

---

## 15. Troubleshooting

### `exec format error` — containers crash immediately

**Cause:** Image was built on ARM64 (Apple Silicon) and the server is AMD64.

**Fix:** Rebuild using the `build-push.sh` script, which uses `docker buildx --platform linux/amd64,linux/arm64`. Make sure you set up the multibuilder first:

```bash
docker buildx create --name multibuilder --use
docker buildx inspect --bootstrap
./build-push.sh rcm
```

---

### `pull access denied` — can't pull from Docker Hub

**Cause:** Either the repo name is wrong, the image tag doesn't exist, or you're not logged in.

**Fix:**
```bash
docker login
# Verify DOCKER_HUB_REPO in .env matches your actual Docker Hub repo name exactly
```

---

### `502 Bad Gateway`

**Cause:** App container is not running or still starting up.

**Fix:**
```bash
./deploy.sh --status
docker compose -f docker-compose.rcm.prod.yml logs rcm-backend --tail 50
```

Spring Boot takes ~60–90 seconds to start. Wait and retry.

---

### `nginx-proxy` keeps restarting

**Cause:** The nginx config references an SSL cert that doesn't exist yet.

**Fix:** Either get the SSL cert first, or switch to HTTP-only mode:

```env
NGINX_CONF=nginx-nossl.conf
```

Then restart the proxy:
```bash
docker compose -f docker-compose.proxy.yml up -d --force-recreate
```

---

### `shared-proxy network not found`

```bash
docker network create shared-proxy
```

---

### `ES SSL certs missing` (RuleEngine)

**Cause:** `certs/cacerts.jks` not found at the path specified by `CERTS_PATH`.

**Quick fix for testing:**
```env
ES_SSL_CLIENT_TRUSTALL=true
```

**Proper fix:** Add cert files to `certs/` directory. See [Section 13](#13-eaglesoft-ssl-certs-ruleengine-only).

---

### Backend OOM killed

**Cause:** JVM heap exceeds available server RAM.

**Fix:** Reduce `JAVA_OPTS` in `.env`:
```env
# 4GB server, RE only
JAVA_OPTS=-Xms512m -Xmx2048m

# 4GB server, RE + RCM together
JAVA_OPTS=-Xms512m -Xmx1024m
```

Also reduce `deploy.resources.limits.memory` in `docker-compose.prod.yml` to match.

---

### Frontend shows old version

```bash
# Force pull fresh image and recreate container
docker compose -f docker-compose.rcm.prod.yml pull rcm-frontend
docker compose -f docker-compose.rcm.prod.yml up -d --force-recreate rcm-frontend
```

Also do a hard refresh in the browser (`Ctrl+Shift+R`).

---

### certbot fails

**Cause:** Port 80 is in use by the proxy.

**Fix:**
```bash
docker compose -f docker-compose.proxy.yml down
sudo certbot certonly --standalone -d your-domain.com
./deploy.sh rcm   # redeploy — this restarts the proxy
```

---

### Domain not resolving

Check that the domain's A-record points to the server's public IP:
```bash
ping your-domain.com
# should resolve to your server's IP
```

If DNS is not set, use the server IP directly with `nginx-nossl.conf` until it is.