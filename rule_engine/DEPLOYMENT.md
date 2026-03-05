# Production Deployment Guide – RuleEngine & RCM (Same Server)

This document describes how RuleEngine and RCM are deployed together on one server using Docker and a shared reverse proxy. It lists every file involved and the order to run things.

---

## 1. Architecture Overview

```
                    Internet (HTTPS :443 / HTTP :80)
                                    │
                                    ▼
                    ┌───────────────────────────────┐
                    │  nginx-proxy (docker-compose  │
                    │  .proxy.yml)                  │
                    │  - Listens 80, 443 on host    │
                    │  - Routes by domain           │
                    │  - SSL termination            │
                    └───────────────┬───────────────┘
                                    │
              ┌─────────────────────┼─────────────────────┐
              │                     │                     │
              ▼                     ▼                     │
   testing.ruleengine.*     testing.rcm.*                  │
              │                     │                     │
              ▼                     ▼                     │
   ┌──────────────────┐   ┌──────────────────┐            │
   │ ruleengine-      │   │ rcm-frontend     │            │
   │ frontend         │   │ (Angular+Nginx)  │            │
   │ (Angular+Nginx)  │   │ port 80 internal │            │
   │ port 80 internal │   └────────┬─────────┘            │
   └────────┬────────┘            │                      │
            │                      │  /api/v1/* → rcm-     │
            │  /api/v1/* →         │  backend:8081         │
            │  backend:8080        │                      │
            ▼                      ▼                      │
   ┌──────────────────┐   ┌──────────────────┐            │
   │ ruleengine-       │   │ rcm-backend      │            │
   │ backend           │   │ (Spring Boot)    │            │
   │ (Spring Boot)     │   │ host 8082:8081   │            │
   │ host 8081:8080    │   └──────────────────┘            │
   └──────────────────┘                                    │
                                                           │
   All above containers join Docker network: shared-proxy   │
   (plus each app’s own network for backend↔frontend)      │
```

- **Single entry point:** Only `nginx-proxy` binds host ports 80 and 443.
- **Domain-based routing:** `testing.ruleengine.caplineservices.in` → RuleEngine; `testing.rcm.caplineservices.in` → RCM.
- **App frontends** serve Angular over HTTP on port 80 inside Docker; they do not do SSL.
- **Backends** are only reached via their frontend’s nginx (e.g. `/api/v1/*` → backend). Host ports 8081 (RuleEngine) and 8082 (RCM) are for direct access (e.g. Postman) and healthchecks.

---

## 2. Directory Layout (rule_engine/)

All commands below are from the **rule_engine/** directory (e.g. `~/triconit-rcm/rule_engine` on the server).

```
rule_engine/
├── docker-compose.yml           # RuleEngine (backend + frontend)
├── docker-compose.rcm.yml        # RCM (backend + frontend)
├── docker-compose.proxy.yml      # Shared nginx reverse proxy
├── certs/                        # EagleSoft SSL: cacerts.jks, keystore.jks (create per env – see §4)
├── nginx-proxy/
│   ├── nginx-nossl.conf         # Proxy config: HTTP only (before SSL)
│   └── nginx-ssl.conf           # Proxy config: HTTPS + redirect
├── ruleengine/
│   └── Dockerfile               # RuleEngine backend image
├── ruleengine-client-app/
│   ├── Dockerfile               # RuleEngine frontend image
│   ├── nginx.prod.conf          # Nginx inside frontend (HTTP, /api/v1 → backend)
│   └── ... (Angular app)
├── rcm/
│   ├── Dockerfile               # RCM backend image (builds Angular + WAR)
│   └── src/main/resources/
│       ├── application-prod.properties   # Prod config (DB placeholders overridden by env)
│       ├── application-dev.properties
│       └── application-docker.properties
└── rcm-client-app/
    ├── Dockerfile               # RCM frontend image
    ├── nginx.prod.conf          # Nginx inside frontend (HTTP, /api/v1 → backend)
    ├── src/environments/
    │   ├── environment.prod.ts  # API_URL, recaptcha siteKey for prod build
    │   ├── environment.ts       # Default/dev
    │   └── environment.docker.ts
    └── ... (Angular app)
```

---

## 3. Files Involved in Deployment

### 3.1 Docker Compose (orchestration)

| File | Purpose | What it runs |
|------|--------|----------------|
| **docker-compose.yml** | RuleEngine stack | `ruleengine-backend` (host 8081→8080), `ruleengine-frontend` (no host ports; joins `shared-proxy`). Uses `ruleengine-network` + `shared-proxy`. |
| **docker-compose.rcm.yml** | RCM stack | `rcm-backend` (host 8082→8081), `rcm-frontend` (no host ports; joins `shared-proxy`). Uses `rcm-network` + `shared-proxy`. Sets `SPRING_DATASOURCE_*` env for prod DB. |
| **docker-compose.proxy.yml** | Reverse proxy | `nginx-proxy`: binds 80 and 443, mounts `nginx-proxy/nginx-ssl.conf` (or `nginx-nossl.conf`) and `/etc/letsencrypt`. Joins `shared-proxy` only. |

### 3.2 Reverse proxy (nginx-proxy)

| File | When to use | Role |
|------|-------------|------|
| **nginx-proxy/nginx-nossl.conf** | Before SSL certs | Listens on 80 only; routes by `server_name` to `ruleengine-frontend:80` and `rcm-frontend:80`. |
| **nginx-proxy/nginx-ssl.conf** | After certbot | Listens 80 (redirect to HTTPS) and 443; TLS for both domains; proxies to same frontend containers. |

Proxy resolves `ruleengine-frontend` and `rcm-frontend` by container name on the **shared-proxy** network.

### 3.3 RuleEngine

| File | Role |
|------|------|
| **ruleengine/Dockerfile** | Builds RuleEngine JAR; copies XSLT from `ivf Form` into image. No Angular (frontend is separate). |
| **ruleengine-client-app/Dockerfile** | Builds Angular with `--configuration=production`, copies `nginx.prod.conf` (via `ENV=prod`), serves app on 80. |
| **ruleengine-client-app/nginx.prod.conf** | Listens 80; serves static from `/usr/share/nginx/html`; `location ^~ /api/v1/` proxies to `backend:8080` with path rewrite. |

RuleEngine backend uses **Spring profile** `prod` (`application-prod.properties`); DB etc. can be overridden by env in compose if needed.

### 3.4 RCM

| File | Role |
|------|------|
| **rcm/Dockerfile** | Multi-stage: (1) build Angular `production`, (2) build WAR (Maven) including that Angular, (3) run WAR. Frontend in Docker is still used for serving; this WAR build is for consistency. |
| **rcm-client-app/Dockerfile** | Builds Angular with `BUILD_CONFIG=production` (uses `environment.prod.ts`), copies `nginx.prod.conf` (ENV=prod), serves on 80. |
| **rcm-client-app/nginx.prod.conf** | Listens 80; serves static; `location ^~ /api/v1/` proxies to `rcm-backend:8081` with rewrite. |
| **rcm-client-app/src/environments/environment.prod.ts** | `API_URL: "/api/v1"`, `recaptcha.siteKey` for production build. |
| **rcm/src/main/resources/application-prod.properties** | Prod Spring config; `spring.datasource.*` are placeholders (`***`); **overridden by** `SPRING_DATASOURCE_JDBC_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD` in `docker-compose.rcm.yml`. |

---

## 4. One-Time Setup on the Server

1. **Create shared network**
   ```bash
   docker network create shared-proxy
   ```

2. **EagleSoft SSL (required for Rule Engine ↔ EagleSoft)**  
   The Rule Engine backend connects to EagleSoft over TLS. Each environment (test, prod) must have a **certs** directory with the **EagleSoft server’s certificate** in the truststore. **See [docs/eaglesoft-ssl-certs.md](docs/eaglesoft-ssl-certs.md) for where to get cacerts.jks and keystore.jks and when they’re required.**

   - Create `rule_engine/certs/` on the server (same directory you run `docker compose` from).
   - **Option A – You have the EagleSoft server’s certificate (e.g. `es-server.cer`):**
     ```bash
     cd rule_engine/certs
     # Create truststore with ES server cert (password e.g. p@ssw0rd – must match application-prod.properties)
     keytool -importcert -alias eaglesoft -file es-server.cer -keystore cacerts.jks -storepass p@ssw0rd -noprompt
     # Create keystore (can copy from local or create minimal one)
     keytool -genkeypair -alias client -keyalg RSA -keysize 2048 -validity 365 -keystore keystore.jks -storepass p@ssw0rd -keypass p@ssw0rd -dname "CN=RuleEngine"
     ```
   - **Option B – Export cert from the EagleSoft server:** On the machine where EagleSoft runs, export its TLS certificate (e.g. from its keystore or from a browser connecting to ES and saving the cert). Then use Option A.
   - **Test (local) only:** From repo root, run `./rule_engine/scripts/generate-es-ssl-certs.sh` and use the generated `rule_engine/certs/` (self-signed; for local ES only).
   - Ensure `cacerts.jks` and `keystore.jks` exist in `rule_engine/certs/` before starting the stack. Passwords must match `es.ssl.client.password` in `application-prod.properties` (default `p@ssw0rd`).

3. **SSL (when ready)**  
   - Stop proxy: `docker compose -f docker-compose.proxy.yml down`  
   - Get certs:  
     `sudo certbot certonly --standalone -d testing.ruleengine.caplineservices.in`  
     `sudo certbot certonly --standalone -d testing.rcm.caplineservices.in`  
   - In **docker-compose.proxy.yml**, set the volume to mount `nginx-proxy/nginx-ssl.conf` (it may already be set).  
   - Start proxy: `docker compose -f docker-compose.proxy.yml up -d`

4. **DNS**  
   Point `testing.ruleengine.caplineservices.in` and `testing.rcm.caplineservices.in` to the server’s public IP.

---

## 5. Local Development

Use the `*.local.yml` compose files to run either app locally. No proxy or SSL needed — services are accessible directly on localhost.

### 5.1 RuleEngine (local)

| URL | Purpose |
|-----|---------|
| `http://localhost:4200` | Angular frontend |
| `http://localhost:8081` | Backend API (Postman, etc.) |

```bash
# From rule_engine/
docker compose -f docker-compose.local.yml up --build
```

- Backend runs with `SPRING_PROFILES_ACTIVE=dev` (`application-dev.properties`).
- Frontend built with `ENV=local`; nginx proxies `/api/v1/*` to the local backend.
- Maven source is mounted from `./ruleengine` and `~/.m2` is reused so no re-download on restart.
- XSLT files are mounted from `../ivf Form/` into the container at `/opt/xslt`.

To rebuild after code changes:
```bash
docker compose -f docker-compose.local.yml up --build
```

To stop:
```bash
docker compose -f docker-compose.local.yml down
```

---

### 5.2 RCM (local)

| URL | Purpose |
|-----|---------|
| `http://localhost:4201` | Angular frontend (port 4201 avoids conflict with RuleEngine) |
| `http://localhost:8082` | Backend API (Postman, etc.) |

```bash
# From rule_engine/
docker compose -f docker-compose.rcm.local.yml up --build
```

- Backend runs with `SPRING_PROFILES_ACTIVE=docker` (`application-docker.properties`), connecting to the same RDS as RuleEngine.
- Frontend built with `ENV=local` / `BUILD_CONFIG=docker`; nginx proxies `/api/v1/*` to the local backend.

To stop:
```bash
docker compose -f docker-compose.rcm.local.yml down
```

---

### 5.3 Running both apps locally at the same time

```bash
# Terminal 1 – RuleEngine
docker compose -f docker-compose.local.yml up --build

# Terminal 2 – RCM
docker compose -f docker-compose.rcm.local.yml up --build
```

The two stacks use separate Docker networks (`ruleengine-network` and `rcm-network`) so they won't conflict.

---

## 6. Deploy / Run Order

From **rule_engine/** (e.g. `~/triconit-rcm/rule_engine`):

```bash
# 1. Shared proxy (must be up so frontends can be reached by domain)
docker compose -f docker-compose.proxy.yml up -d

# 2. RuleEngine
docker compose -f docker-compose.yml up --build -d

# 3. RCM
docker compose -f docker-compose.rcm.yml up --build -d
```

To **rebuild** after code/config changes:

```bash
# Rebuild and restart RuleEngine
docker compose -f docker-compose.yml up --build -d

# Rebuild and restart RCM
docker compose -f docker-compose.rcm.yml up --build -d
```

**Building FE and BE separately (reduces server load)**

Build one service at a time, then start the stack without `--build`:

**RCM:**
```bash
# Build backend first (Maven + bundled Angular), then frontend
docker compose -f docker-compose.rcm.yml build rcm-backend
docker compose -f docker-compose.rcm.yml build rcm-frontend
docker compose -f docker-compose.rcm.yml up -d
```

**RuleEngine:**
```bash
docker compose -f docker-compose.yml build backend
docker compose -f docker-compose.yml build frontend
docker compose -f docker-compose.yml up -d
```

To rebuild only one service (e.g. after changing only the frontend):

```bash
docker compose -f docker-compose.rcm.yml build rcm-frontend
docker compose -f docker-compose.rcm.yml up -d --force-recreate rcm-frontend
```

Proxy only needs restart when you change its config or certs:

```bash
docker compose -f docker-compose.proxy.yml up -d --force-recreate
```

---

## 7. Request Flow (Summary)

- **User** → `https://testing.rcm.caplineservices.in/login`  
  → **nginx-proxy** (SSL, domain match)  
  → **rcm-frontend:80** (Angular + nginx)  
  → Nginx serves `index.html` and static assets.

- **User** → `https://testing.rcm.caplineservices.in/api/v1/account/login` (from Angular)  
  → **nginx-proxy**  
  → **rcm-frontend:80**  
  → **rcm-client-app/nginx.prod.conf** `location ^~ /api/v1/`  
  → **rcm-backend:8081** (path rewritten to `/account/login`).

Same pattern for RuleEngine with `testing.ruleengine.caplineservices.in` and `ruleengine-frontend` → `backend:8080`.

---

## 8. Optional: Secrets and Overrides

- **RCM DB:** Production credentials are set in `docker-compose.rcm.yml` as env vars. To avoid storing them in the file, use a `.env` in `rule_engine/` (add `.env` to `.gitignore`) and reference variables in the compose file, or use Docker secrets.
- **RuleEngine:** DB can be overridden with `SPRING_JPA1_PROPERTIES_HIBERNATE_CONNECTION_*` in `docker-compose.yml` if needed.
- **reCAPTCHA:** Frontend uses `environment.prod.ts` `recaptcha.siteKey`; backend uses `application-prod.properties` `captcha.secret.key`. Use v2 checkbox keys for the current RCM login.

---

## 9. Quick Reference – Ports & URLs

| Service            | Host port | Container port | URL (HTTPS)                          |
|--------------------|-----------|----------------|--------------------------------------|
| nginx-proxy        | 80, 443   | 80, 443        | Entry for both domains               |
| ruleengine-backend | 8081      | 8080           | Direct API access                    |
| ruleengine-frontend| —         | 80             | testing.ruleengine.caplineservices.in|
| rcm-backend        | 8082      | 8081           | Direct API access                    |
| rcm-frontend       | —         | 80             | testing.rcm.caplineservices.in       |
