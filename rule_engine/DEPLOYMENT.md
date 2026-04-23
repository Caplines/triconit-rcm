# Production Deployment Guide – RuleEngine & RCM

This document describes how RuleEngine and RCM are deployed on a server using Docker, with a shared nginx reverse proxy for domain-based routing and SSL termination.

Three deployment modes are supported:
1. **RuleEngine only** – single app behind the proxy
2. **RCM only** – single app behind the proxy
3. **Both apps** – both apps behind the same proxy, routed by domain

---

## 1. Architecture Overview

```
                    Internet (HTTPS :443 / HTTP :80)
                                    │
                                    ▼
                  ┌──────────────────────────────────┐
                  │  nginx-proxy                     │
                  │  (docker-compose.proxy.yml)      │
                  │  - Listens 80, 443 on host       │
                  │  - Routes by server_name         │
                  │  - SSL termination               │
                  └────────────────┬─────────────────┘
                                   │
            ┌──────────────────────┼──────────────────────┐
            │                      │                      │
            ▼                      ▼                      │
   RE_SERVER_NAME           RCM_SERVER_NAME               │
            │                      │                      │
            ▼                      ▼                      │
   ┌──────────────────┐   ┌──────────────────┐            │
   │ ruleengine-      │   │ rcm-frontend     │            │
   │ frontend         │   │ (Angular+Nginx)  │            │
   │ (Angular+Nginx)  │   │ port 80 internal │            │
   │ port 80 internal │   └────────┬─────────┘            │
   └────────┬─────────┘            │                      │
            │                      │  /api/v1/* →         │
            │  /api/v1/* →         │  rcm-backend:8081    │
            │  backend:8080        │                      │
            │  + legacy routes     │                      │
            ▼                      ▼                      │
   ┌──────────────────┐   ┌──────────────────┐            │
   │ ruleengine-      │   │ rcm-backend      │            │
   │ backend          │   │ (Spring Boot)    │            │
   │ (Spring Boot)    │   │ host 8082:8081   │            │
   │ host 8081:8080   │   └──────────────────┘            │
   └──────────────────┘                                   │
                                                          │
   All containers join Docker network: shared-proxy       │
   (plus each app's own internal network)                 │
```

**Key points:**

- **Single entry point:** Only `nginx-proxy` binds host ports 80 and 443.
- **Domain-based routing:** `RE_SERVER_NAME` domains → RuleEngine; `RCM_SERVER_NAME` domains → RCM.
- **Multi-domain support:** `RE_SERVER_NAME` can contain multiple space-separated domains (e.g. `caplineruleengine.com www.caplineruleengine.com production.ruleengine.caplineservices.in`). `RE_DOMAIN` is a single domain used for SSL cert paths only.
- **App frontends** serve Angular over HTTP on port 80 inside Docker; they do not do SSL.
- **Backends** are only reached via their frontend's nginx (e.g. `/api/v1/*` → backend). Host ports 8081 (RuleEngine) and 8082 (RCM) are exposed for direct API access (Postman, healthchecks).
- **Lazy DNS resolution:** All proxy configs use Docker's internal DNS resolver (`127.0.0.11`) so nginx starts even if an app container isn't running yet — requests return 502 until the app comes up.

---

## 2. Directory Layout

All commands are run from the **`rule_engine/`** directory.

```
rule_engine/
├── .env.example                    # Template — copy to .env and fill in values
├── .env                            # Your actual environment config (never commit)
├── deploy-init-re.sh               # One-command deploy: RuleEngine only
├── deploy-init-rcm.sh              # One-command deploy: RCM only
├── deploy-init-all.sh              # One-command deploy: both apps
├── docker-compose.yml              # RuleEngine production (backend + frontend)
├── docker-compose.rcm.yml          # RCM production (backend + frontend)
├── docker-compose.proxy.yml        # Shared nginx reverse proxy
├── docker-compose.local.yml        # RuleEngine local development
├── docker-compose.rcm.local.yml    # RCM local development
├── certs/                          # EagleSoft SSL certs (cacerts.jks, keystore.jks)
├── nginx-proxy/
│   ├── nginx-ssl.conf              # Proxy: HTTPS, both apps (default)
│   ├── nginx-ssl-re-only.conf      # Proxy: HTTPS, RuleEngine only
│   ├── nginx-ssl-rcm-only.conf     # Proxy: HTTPS, RCM only
│   └── nginx-nossl.conf            # Proxy: HTTP only (before certbot)
├── ruleengine/
│   └── Dockerfile                  # RuleEngine backend (Spring Boot JAR)
├── ruleengine-client-app/
│   ├── Dockerfile                  # RuleEngine frontend (Angular + Nginx)
│   ├── nginx.prod.conf             # Frontend nginx: prod (domain-aware, /api/v1 + legacy routes)
│   ├── nginx.local.conf            # Frontend nginx: local dev
│   └── ...                         # Angular app source
├── rcm/
│   └── Dockerfile                  # RCM backend (Spring Boot WAR, bundles Angular)
└── rcm-client-app/
    ├── Dockerfile                  # RCM frontend (Angular + Nginx)
    ├── nginx.prod.conf             # Frontend nginx: prod (/api/v1 → rcm-backend:8081)
    ├── nginx.prod-nossl.conf       # Frontend nginx: prod without SSL (use ENV=prod-nossl)
    ├── nginx.local.conf            # Frontend nginx: local dev
    └── ...                         # Angular app source
```

---

## 3. Environment Variables (.env)

Copy `.env.example` to `.env` and fill in your values:

```bash
cp .env.example .env
nano .env    # or vim, etc.
```

### Key variable groups

| Group | Variables | Notes |
|-------|-----------|-------|
| **Database** | `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` | Shared by both RE and RCM |
| **Domains** | `RE_DOMAIN`, `RE_SERVER_NAME`, `RCM_DOMAIN`, `RCM_SERVER_NAME` | See below |
| **Proxy** | `NGINX_CONF` | Selects which nginx config the proxy uses |
| **JWT** | `JWT_SECRET`, `JWT_EXPIRATION` | Auth tokens |
| **EagleSoft SSL** | `ES_SSL_CLIENT_PASSWORD`, `ES_SSL_CLIENT_TRUSTALL`, etc. | For RE ↔ EagleSoft TLS |
| **JVM** | `JAVA_OPTS` | Heap size and GC flags for RE backend |
| **CORS** | `CORS_ALLOWED_ORIGINS` | Comma-separated allowed origins |
| **RCM Mail** | `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD` | Email sending |
| **reCAPTCHA** | `CAPTCHA_SECRET_KEY` | RCM login captcha |
| **Proxy Timeouts** | `PROXY_READ_TIMEOUT`, `PROXY_SEND_TIMEOUT` | Backend response timeouts |

### Domain variables explained

| Variable | Example | Purpose |
|----------|---------|---------|
| `RE_DOMAIN` | `production.ruleengine.caplineservices.in` | **Single domain** — used for SSL cert paths (`/etc/letsencrypt/live/${RE_DOMAIN}/`). Must match the domain used with certbot. |
| `RE_SERVER_NAME` | `caplineruleengine.com www.caplineruleengine.com production.ruleengine.caplineservices.in` | **Space-separated list** of all domains nginx should respond to. Used as nginx `server_name`. |
| `RCM_DOMAIN` | `testing.rcm.caplineservices.in` | Same as `RE_DOMAIN` but for RCM. |
| `RCM_SERVER_NAME` | `testing.rcm.caplineservices.in` | Same as `RE_SERVER_NAME` but for RCM. |

> **Note:** If you only have one domain, set both `RE_DOMAIN` and `RE_SERVER_NAME` to the same value.

### NGINX_CONF options

| Value | SSL certs needed | When to use |
|-------|------------------|-------------|
| `nginx-ssl.conf` (default) | Both RE + RCM domains | Both apps deployed, HTTPS |
| `nginx-ssl-re-only.conf` | RE domain only | RuleEngine only, HTTPS |
| `nginx-ssl-rcm-only.conf` | RCM domain only | RCM only, HTTPS |
| `nginx-nossl.conf` | None | HTTP only — before certbot or for testing |

> **Local laptop (no Let’s Encrypt on your Mac):** Any `nginx-ssl*.conf` tells the proxy to load `/etc/letsencrypt/live/${RE_DOMAIN}/fullchain.pem` (see `docker-compose.proxy.yml`, which mounts the host’s `/etc/letsencrypt`). On a dev machine that path is usually empty or missing, so nginx exits with `BIO_new_file() failed` / `cannot load certificate`. This is **not** a Spring Boot bug; you picked an SSL proxy profile without certs. Use **`NGINX_CONF=nginx-nossl.conf`** in `.env`, or run **`./deploy-init-re.sh --no-ssl`**, or skip the shared proxy and use **`docker-compose.local.yml`** (section 9).

---

## 4. Quick Deploy (Recommended)

Each deploy script handles everything end-to-end: installing prerequisites, obtaining SSL certs, building images, starting containers, and health checking.

### 4.1 Fresh server — full workflow (example: RuleEngine only)

```bash
# 1. Clone the repo
git clone <your-repo-url>
cd rule-engine/rule_engine
chmod +x deploy-init-*.sh

# 2. Install Docker, Docker Compose, certbot, and create .env
./deploy-init-re.sh --setup

# 3. Configure environment
nano .env    # Fill in: DB_URL, DB_USERNAME, DB_PASSWORD, RE_DOMAIN, RE_SERVER_NAME, etc.

# 4. Deploy HTTP first (to verify everything works)
./deploy-init-re.sh --no-ssl

# 5. Verify it's running
./deploy-init-re.sh --status

# 6. Stop to free port 80 for certbot
./deploy-init-re.sh --down

# 7. Get SSL certs (requires DNS pointing to this server)
./deploy-init-re.sh --init-ssl

# 8. Redeploy with SSL
./deploy-init-re.sh
```

### 4.2 Script flags reference

All three scripts (`deploy-init-re.sh`, `deploy-init-rcm.sh`, `deploy-init-all.sh`) support the same flags:

| Flag | Purpose |
|------|---------|
| `--setup` | **First-time:** Install Docker, Docker Compose, certbot. Create `.env` from `.env.example`. Create `certs/` directory. |
| `--init-ssl` | **First-time:** Obtain SSL certs via certbot. Stops proxy if running (needs port 80). Reads domain(s) from `.env`. |
| *(no flags)* | **Deploy with SSL** (default). Validates `.env`, checks SSL certs, creates Docker network, starts proxy + app. |
| `--no-ssl` | **Deploy HTTP only.** Same as above but skips SSL cert check and uses `nginx-nossl.conf`. |
| `--status` | Show container state and health for all services. |
| `--logs` | Tail container logs (Ctrl+C to stop). |
| `--down` | Stop all containers (app + proxy). |
| `--help` | Show full usage with the typical workflow steps. |

### 4.3 Deploy RuleEngine only

```bash
./deploy-init-re.sh --setup       # First time: install prerequisites
./deploy-init-re.sh --init-ssl    # First time: get SSL cert
./deploy-init-re.sh               # Deploy with SSL
./deploy-init-re.sh --no-ssl      # Deploy HTTP only
./deploy-init-re.sh --status      # Check health
./deploy-init-re.sh --logs        # Tail logs
./deploy-init-re.sh --down        # Stop everything
```

### 4.4 Deploy RCM only

```bash
./deploy-init-rcm.sh --setup
./deploy-init-rcm.sh --init-ssl
./deploy-init-rcm.sh               # Deploy with SSL
./deploy-init-rcm.sh --no-ssl      # Deploy HTTP only
./deploy-init-rcm.sh --down        # Stop everything
```

### 4.5 Deploy both apps

```bash
./deploy-init-all.sh --setup
./deploy-init-all.sh --init-ssl    # Gets certs for BOTH RE_DOMAIN and RCM_DOMAIN
./deploy-init-all.sh               # Deploy with SSL
./deploy-init-all.sh --no-ssl      # Deploy HTTP only
./deploy-init-all.sh --down        # Stop everything
```

### What the deploy scripts do (on deploy)

1. Check Docker and Docker Compose v2 are installed (point to `--setup` if not)
2. Auto-create `.env` from `.env.example` if missing (then exit so you can edit it)
3. Validate required `.env` variables (DB, domain, etc.)
4. Select the correct `NGINX_CONF` for the deployment mode
5. Verify SSL certs exist (unless `--no-ssl`; point to `--init-ssl` if missing)
6. Warn if EagleSoft certs are missing (RE scripts only)
7. Create the `shared-proxy` Docker network (if it doesn't exist)
8. Start/recreate the nginx-proxy container
9. Build backend and frontend images **separately** (reduces peak memory on small servers)
10. Start the app containers
11. Wait 10s and show container health status
12. Print access URLs and useful commands

---

## 5. Files Involved in Deployment

### 5.1 Docker Compose (orchestration)

| File | Purpose | Services |
|------|---------|----------|
| **docker-compose.yml** | RuleEngine stack | `backend` (host 8081→8080), `frontend` (no host ports, joins `shared-proxy`). Networks: `ruleengine-network` + `shared-proxy`. Container memory limit: 3072MB. |
| **docker-compose.rcm.yml** | RCM stack | `rcm-backend` (host 8082→8081), `rcm-frontend` (no host ports, joins `shared-proxy`). Networks: `rcm-network` + `shared-proxy`. |
| **docker-compose.proxy.yml** | Reverse proxy | `nginx-proxy`: binds 80+443, mounts the nginx config selected by `NGINX_CONF` env var, mounts `/etc/letsencrypt`. Joins `shared-proxy` only. |

### 5.2 Reverse proxy (nginx-proxy/)

Controlled by the `NGINX_CONF` variable in `.env` — no need to edit compose files.

All four configs use Docker's internal DNS resolver (`127.0.0.11`) with lazy upstream resolution via the `set $upstream` pattern. This means:
- Nginx starts even if app containers aren't running yet
- Requests to a missing app return 502 until it comes up
- You can start the proxy before the apps without errors

Config files are mounted as nginx templates (`/etc/nginx/templates/default.conf.template`), so environment variables like `${RE_SERVER_NAME}` and `${RE_DOMAIN}` are substituted at container startup.

### 5.3 RuleEngine

| File | Role |
|------|------|
| **ruleengine/Dockerfile** | Multi-stage build: Maven (JDK 8) → JAR. Copies XSLT templates from `ivf Form/` into image at `/opt/xslt`. |
| **ruleengine-client-app/Dockerfile** | Multi-stage build: Angular `--configuration=production` → nginx:1.21-alpine. Copies `nginx.${ENV}.conf` as template. |
| **ruleengine-client-app/nginx.prod.conf** | Listens 80. Two backend route patterns: (1) `/api/v1/*` → `backend:8080` with path prefix strip, (2) legacy regex matching ~70 direct backend paths without prefix (for backward compatibility with older integrations). Serves Angular SPA with `try_files`. |

Backend uses Spring profile `prod` (`application-prod.properties`). JVM heap is configurable via `JAVA_OPTS` in `.env` (default: `-Xms256m -Xmx1536m` for ~4GB hosts with RE+FE). Container memory limit is set to 2560MB in `docker-compose.yml`. To enable a heap dump on OOM, add the `-XX:+HeapDumpOnOutOfMemoryError` and `-XX:HeapDumpPath=...` flags to `JAVA_OPTS`.

### 5.4 RCM

| File | Role |
|------|------|
| **rcm/Dockerfile** | 3-stage build: (1) Angular `--configuration=production` via Node 18, (2) Maven WAR (bundles the Angular dist), (3) JRE runtime. |
| **rcm-client-app/Dockerfile** | Multi-stage build: Angular `--configuration=${BUILD_CONFIG}` → nginx:1.21-alpine. Copies `nginx.${ENV}.conf` as template. |
| **rcm-client-app/nginx.prod.conf** | Listens 80. Routes `/api/v1/*` → `rcm-backend:8081` with path prefix strip. Serves Angular SPA. |
| **rcm-client-app/nginx.prod-nossl.conf** | Same as `nginx.prod.conf` but for HTTP-only setups. Use with `ENV=prod-nossl` in docker-compose if not using the shared proxy. |

Backend uses Spring profile `prod`. DB credentials come from `.env` (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`).

---

## 6. EagleSoft SSL Setup (RuleEngine only)

The RuleEngine backend connects to EagleSoft over TLS. This requires truststore/keystore files in `certs/`.

### Quick start (development / testing)

Set in `.env`:
```
ES_SSL_CLIENT_TRUSTALL=true
```
No cert files needed — the backend accepts any EagleSoft server certificate. The `certs/` directory can be empty or absent.

### Production setup

1. Set `ES_SSL_CLIENT_TRUSTALL=false` in `.env`
2. Obtain the EagleSoft server's certificate (`es-server.cer`)
3. Create truststore and keystore:

```bash
mkdir -p certs && cd certs

# Import ES server cert into truststore (password must match ES_SSL_CLIENT_PASSWORD in .env)
keytool -importcert -alias eaglesoft -file es-server.cer \
  -keystore cacerts.jks -storepass changeit -noprompt

# Create client keystore
keytool -genkeypair -alias client -keyalg RSA -keysize 2048 \
  -validity 365 -keystore keystore.jks -storepass changeit \
  -keypass changeit -dname "CN=RuleEngine"

cd ..
```

4. Verify `cacerts.jks` and `keystore.jks` exist in `certs/`
5. Passwords must match `ES_SSL_CLIENT_PASSWORD` in `.env` (default: `changeit`)

For local dev: run `./scripts/generate-es-ssl-certs.sh` to generate self-signed certs.

See [docs/eaglesoft-ssl-certs.md](docs/eaglesoft-ssl-certs.md) for full details.

---

## 7. SSL Setup (Let's Encrypt)

### Get certificates

Stop anything on ports 80/443, then:

```bash
sudo certbot certonly --standalone -d your-ruleengine-domain.com
sudo certbot certonly --standalone -d your-rcm-domain.com
```

### DNS

Point your domains to the server's public IP:
- `RE_DOMAIN` → server IP
- `RCM_DOMAIN` → server IP
- Any additional domains in `RE_SERVER_NAME` / `RCM_SERVER_NAME` → server IP

### Auto-renewal

Certbot sets up auto-renewal by default. After renewal, restart the proxy to pick up new certs:

```bash
docker compose -f docker-compose.proxy.yml restart
```

---

## 8. Manual Deploy (Step-by-Step)

Use this section if you prefer manual control over the deploy scripts.

### 8.1 One-time setup

```bash
# Create shared Docker network
docker network create shared-proxy
```

### 8.2 Deploy both apps

```bash
# .env: NGINX_CONF=nginx-ssl.conf  (or omit — it's the default)

# 1. Shared proxy
docker compose -f docker-compose.proxy.yml up -d

# 2. RuleEngine
docker compose -f docker-compose.yml up --build -d

# 3. RCM
docker compose -f docker-compose.rcm.yml up --build -d
```

With `nginx-ssl.conf`, upstreams are resolved lazily via Docker DNS. If one app is temporarily down, nginx still runs — requests to that domain return 502 until the app comes back. SSL certs for **both** domains must exist.

### 8.3 Deploy RuleEngine only

```bash
# .env: NGINX_CONF=nginx-ssl-re-only.conf

docker compose -f docker-compose.proxy.yml up -d
docker compose -f docker-compose.yml up --build -d
```

### 8.4 Deploy RCM only

```bash
# .env: NGINX_CONF=nginx-ssl-rcm-only.conf

docker compose -f docker-compose.proxy.yml up -d
docker compose -f docker-compose.rcm.yml up --build -d
```

### 8.5 Switching between modes

Change `NGINX_CONF` in `.env`, then recreate the proxy:

```bash
docker compose -f docker-compose.proxy.yml up -d --force-recreate
```

### 8.6 Rebuilding individual services

```bash
# Rebuild and restart only the RuleEngine frontend
docker compose -f docker-compose.yml build frontend
docker compose -f docker-compose.yml up -d --force-recreate frontend

# Rebuild and restart only the RuleEngine backend
docker compose -f docker-compose.yml build backend
docker compose -f docker-compose.yml up -d --force-recreate backend

# Same pattern for RCM
docker compose -f docker-compose.rcm.yml build rcm-frontend
docker compose -f docker-compose.rcm.yml up -d --force-recreate rcm-frontend
```

### 8.7 Build FE and BE separately (reduces peak memory on small servers)

```bash
# RuleEngine
docker compose -f docker-compose.yml build backend
docker compose -f docker-compose.yml build frontend
docker compose -f docker-compose.yml up -d

# RCM
docker compose -f docker-compose.rcm.yml build rcm-backend
docker compose -f docker-compose.rcm.yml build rcm-frontend
docker compose -f docker-compose.rcm.yml up -d
```

### 8.8 Running from a parent directory

If you run docker compose from a parent directory (e.g. `~/triconit-rcm`) with `-f rule_engine/docker-compose.yml`, create a `.env` in that parent directory with:

```bash
CERTS_PATH=rule_engine/certs
```

Otherwise the backend container won't find EagleSoft SSL certs and will log "ES SSL certs missing or unreadable (exists=false)".

---

## 9. Local Development

Use the `*.local.yml` compose files to run either app locally. No proxy or SSL needed — services are accessible directly on localhost.

If you **do** run `docker-compose.proxy.yml` on your laptop, set **`NGINX_CONF=nginx-nossl.conf`** unless you have real certbot files under `/etc/letsencrypt/live/<your-RE_DOMAIN>/` on the host (you almost never do locally).

### 9.1 RuleEngine (local)

| URL | Purpose |
|-----|---------|
| `http://localhost:4200` | Angular frontend |
| `http://localhost:8081` | Backend API (Postman, etc.) |

```bash
# From rule_engine/
docker compose -f docker-compose.local.yml up --build
```

- Backend runs with `SPRING_PROFILES_ACTIVE=docker` (`application-docker.properties`).
- Frontend built with `ENV=local`; nginx proxies `/api/v1/*` to the local backend.
- Maven source is mounted from `./ruleengine` and `~/.m2` is reused so no re-download on restart.
- XSLT files are mounted from `../ivf Form/` into the container at `/opt/xslt`.

To stop:
```bash
docker compose -f docker-compose.local.yml down
```

### 9.2 RCM (local)

| URL | Purpose |
|-----|---------|
| `http://localhost:4201` | Angular frontend (port 4201 avoids conflict with RuleEngine) |
| `http://localhost:8082` | Backend API (Postman, etc.) |

```bash
# From rule_engine/
docker compose -f docker-compose.rcm.local.yml up --build
```

- Backend runs with `SPRING_PROFILES_ACTIVE=docker` (`application-docker.properties`).
- Frontend built with `ENV=local` / `BUILD_CONFIG=docker`; nginx proxies `/api/v1/*` to the local backend.

To stop:
```bash
docker compose -f docker-compose.rcm.local.yml down
```

### 9.3 Running both apps locally at the same time

```bash
# Terminal 1 – RuleEngine
docker compose -f docker-compose.local.yml up --build

# Terminal 2 – RCM
docker compose -f docker-compose.rcm.local.yml up --build
```

The two stacks use separate Docker networks (`ruleengine-network` and `rcm-network`) so they won't conflict.

---

## 10. Request Flow

### Frontend page load

```
User → https://RE_DOMAIN/login
  → nginx-proxy (SSL termination, domain match via RE_SERVER_NAME)
  → ruleengine-frontend:80 (nginx serves index.html + static assets)
```

### API call (new pattern)

```
User → https://RE_DOMAIN/api/v1/account/login
  → nginx-proxy
  → ruleengine-frontend:80
  → nginx.prod.conf: location ^~ /api/v1/ (strips prefix)
  → backend:8080 → /account/login
```

### Legacy API call (RuleEngine only)

```
User → https://RE_DOMAIN/savedatatore
  → nginx-proxy
  → ruleengine-frontend:80
  → nginx.prod.conf: legacy regex location (no rewrite)
  → backend:8080 → /savedatatore
```

Same pattern for RCM with `RCM_DOMAIN` → `rcm-frontend` → `rcm-backend:8081`.

---

## 11. Configuration Reference

### JVM / Memory (RuleEngine backend)

| Setting | Where | Default | Notes |
|---------|-------|---------|-------|
| `JAVA_OPTS` | `.env` | `-Xms256m -Xmx1536m` | JVM heap. Adjust based on server RAM. Heap dump on OOM is off by default. |
| Container memory limit | `docker-compose.yml` | `2560m` | `deploy.resources.limits.memory` |

**Guidelines:**
- 4GB server, RE+FE (compose defaults): `-Xmx1536m`, container limit `2560m`
- 4GB server, RE only or more headroom: can raise `-Xmx` in `.env` and container limit
- 4GB server, RE + RCM: reduce each app (e.g. `-Xmx1024m` per service), match container limits

### CORS

`CORS_ALLOWED_ORIGINS` in `.env` (comma-separated) controls which origins the backend accepts. Update this when adding new frontend domains.

### Proxy Timeouts

| Variable | Default | Notes |
|----------|---------|-------|
| `PROXY_READ_TIMEOUT` | `1800s` | Max time to wait for backend response |
| `PROXY_SEND_TIMEOUT` | `1800s` | Max time to send request to backend |

### Secrets and Overrides

- **Database:** Production credentials are set in `.env` as `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`. Both RE and RCM read from the same variables.
- **RuleEngine DB override:** Can also be overridden with `SPRING_JPA1_PROPERTIES_HIBERNATE_CONNECTION_*` in `docker-compose.yml` if needed.
- **reCAPTCHA:** Frontend uses `environment.prod.ts` → `recaptcha.siteKey`; backend uses `CAPTCHA_SECRET_KEY` in `.env`. Use v2 checkbox keys for RCM login.
- **Email:** Set `MAIL_USERNAME` and `MAIL_PASSWORD` in `.env` (Gmail app password for `smtp.gmail.com`).

---

## 12. Ports & URLs Quick Reference

| Service | Host Port | Container Port | Access |
|---------|-----------|----------------|--------|
| nginx-proxy | 80, 443 | 80, 443 | Entry for all domains |
| ruleengine-backend | 8081 | 8080 | Direct API (Postman, healthcheck) |
| ruleengine-frontend | — | 80 | Via nginx-proxy only |
| rcm-backend | 8082 | 8081 | Direct API (Postman, healthcheck) |
| rcm-frontend | — | 80 | Via nginx-proxy only |

---

## 13. Troubleshooting

| Problem | Cause | Fix |
|---------|-------|-----|
| nginx-proxy won't start | SSL cert missing | Run certbot or use `--no-ssl` / `NGINX_CONF=nginx-nossl.conf` |
| `Mounts denied` / path not shared (Docker Desktop Mac) | `docker-compose.yml` defaults bind `/opt/project/tricon/...`, which does not exist on macOS | In `.env` set `RE_BIND_FILES`, `RE_BIND_LOG`, `RE_BIND_CHROME` to repo-relative dirs (see `.env.example`), or use `docker-compose.local.yml` for dev |
| 502 Bad Gateway | App container not running or still starting | Check: `docker compose -f docker-compose.yml ps` — wait for healthy or restart |
| Backend PKIX / SSL error | EagleSoft certs missing or wrong password | Set `ES_SSL_CLIENT_TRUSTALL=true` in `.env` for testing, or add proper certs to `certs/` |
| "shared-proxy network not found" | Network not created | Run: `docker network create shared-proxy` |
| Frontend shows old version | Browser cache or old Docker image | Hard refresh (Ctrl+Shift+R) and rebuild: `docker compose ... up --build -d` |
| Backend OOM killed | Heap too large for available server RAM | Reduce `-Xmx` in `JAVA_OPTS` and `deploy.resources.limits.memory` in compose |
| "ES SSL certs missing (exists=false)" | Cert path wrong | Check `CERTS_PATH` in `.env` — must point to directory containing `cacerts.jks` |
| Container keeps restarting | App crash, check logs | `docker compose -f docker-compose.yml logs backend --tail 100` |
| Domain not resolving | DNS not configured | Point domain A-record to server's public IP |
| certbot fails | Port 80 in use | Stop proxy first: `docker compose -f docker-compose.proxy.yml down` |
