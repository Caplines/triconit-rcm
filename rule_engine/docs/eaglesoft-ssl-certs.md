# EagleSoft SSL certs: where to get them and when they’re required

## Why do these certs exist? (Client vs server)

TLS has two directions of verification:

| Who verifies whom | What it means | What we use |
|-------------------|---------------|-------------|
| **Client verifies server** | Rule Engine checks that the other end is really EagleSoft (not an impostor). | **cacerts.jks (truststore)** – list of server certs we trust. |
| **Server verifies client** | EagleSoft can optionally require the client to present a certificate (mutual TLS). | **keystore.jks** – our client identity, only needed if ES is configured to require client certs. |

- **The main point of the certs is client → server verification.**  
  With a proper **truststore (cacerts.jks)** containing EagleSoft’s server cert, the Rule Engine will only connect to the *real* EagleSoft server. If someone stands up a fake ES server, the client will reject it (cert doesn’t match).

- **trustAll=true turns off that verification.**  
  It means: “accept any server certificate.” So the Rule Engine will connect to *any* TLS server that claims to be EagleSoft – including an attacker’s. That’s why trustAll is only for local/dev: it’s convenient (no cert setup) but **insecure**. For test/prod you should use the real certs and keep trustAll=false.

- **Does the ES server require the client to have certs?**  
  That depends on how EagleSoft is configured. If it uses **client authentication (mTLS)**, then the Rule Engine must present a cert from **keystore.jks** and the ES server will only accept connections from clients it trusts. If ES does *not* require client auth (common), it only presents its own cert; the client still needs the truststore to verify that cert, but the server doesn’t care whether the client has a keystore. Our code currently requires both files when trustAll=false for implementation reasons (building the SSLContext).

So: **cacerts.jks** = “only connect to the real EagleSoft.” **trustAll=true** = “connect to anyone” – fine for dev, not for test/prod.

---

## Do I need these files to connect?

**Password:** The app uses `es.ssl.client.password` (default: `p@ssw0rd` for docker/dev, **`changeit`** for prod). The value must match the password used when creating both `cacerts.jks` and `keystore.jks`. If you see "Keystore was tampered with, or password was incorrect", set `ES_SSL_CLIENT_PASSWORD` to the JKS password or re-create the JKS files with the expected password.

**"PKIX path validation failed: signature check failed" / "Signature does not match":** The certificate in your `cacerts.jks` is not the one the EagleSoft server is presenting. You must export the **server’s** certificate from the keystore used by the ES server (on the ES host) and import it into the Rule Engine’s `cacerts.jks`. See “Where do I get cacerts.jks” below — use the cert from the ES server’s actual keystore (e.g. from the same machine that runs ES on `listenport`), not a different or old cert.

**To use the exact cert the server sends**, capture it with openssl (run from a host that can reach the ES server):
```bash
openssl s_client -connect <es-host>:<es-port> -showcerts </dev/null 2>/dev/null | openssl x509 -outform PEM -out es-live.cer
```
Then import `es-live.cer` into a new `cacerts.jks` (see steps in “Where do I get cacerts.jks”). Replace `<es-host>` and `<es-port>` with the ES server IP and port (e.g. 4444).

**Temporary test-only workaround:** To unblock test while fixing the cert, set on the **test** server only (e.g. in `.env` or backend env): `ES_SSL_CLIENT_TRUSTALL=true`. The Rule Engine will then accept any ES server certificate. Do **not** set this in production.

- **With `es.ssl.client.trustAll=true` (e.g. docker profile):** No. The app accepts any EagleSoft server certificate. You can connect without `cacerts.jks` or `keystore.jks`. Use only for local/dev.
- **With `es.ssl.client.trustAll=false` (prod profile):** Yes. The app requires both files to be present and readable. If either is missing, it will not establish the SSL connection to EagleSoft. So for test and prod, you must provide the certs.

---

## Where do I get cacerts.jks and keystore.jks?

### 1. Local / dev (EagleSoft on your machine or local Docker)

Run the script once; it creates both files in `rule_engine/certs/`:

```bash
# From repo root
./rule_engine/scripts/generate-es-ssl-certs.sh
```

This generates self-signed certs. Use them with the **docker** profile and mount `./certs` at `/opt/project/tricon/client`. If you run the ES server locally, you can use the same script’s `server.jks` on the ES side so the client truststore matches.

### 2. Test / prod (EagleSoft on a real server)

The Rule Engine backend must **trust the EagleSoft server’s TLS certificate**. You need to get that certificate from the machine that runs EagleSoft, then build the two JKS files.

**Step 1 – Get the EagleSoft server certificate**

- **Option A:** Ask the team that runs EagleSoft to export the server’s TLS cert (e.g. as `es-server.cer` or `es-server.pem`). They can export it from the server’s keystore or from the same place they configured ES SSL.
- **Option B:** If you have access to the EagleSoft server, export from its keystore, for example:
  ```bash
  keytool -exportcert -alias <alias-used-by-es> -keystore /path/to/es/keystore.jks -file es-server.cer -storepass <password>
  ```
- **Option C:** From any machine that can reach EagleSoft over TLS, capture the server cert, e.g.:
  ```bash
  openssl s_client -connect <es-host>:<es-port> -showcerts </dev/null 2>/dev/null | openssl x509 -outform PEM -out es-server.cer
  ```

**Step 2 – Create cacerts.jks (truststore)**

Import the server cert into a new truststore (password must match `es.ssl.client.password` in `application-prod.properties`, default **`changeit`**):

```bash
cd rule_engine/certs
keytool -importcert -alias eaglesoft -file es-server.cer -keystore cacerts.jks -storepass changeit -noprompt
```

**Step 3 – Create keystore.jks (client keystore)**

The app expects a keystore at the path in config. You can create a minimal one:

```bash
keytool -genkeypair -alias client -keyalg RSA -keysize 2048 -validity 365 \
  -keystore keystore.jks -storepass changeit -keypass changeit \
  -dname "CN=RuleEngine, OU=App, O=Capline, L=City, ST=State, C=US"
```

**Step 4 – Deploy**

Put `cacerts.jks` and `keystore.jks` in `rule_engine/certs/` on the test or prod server (the same directory you use for `docker compose`). The compose file mounts `./certs` into the backend at `/opt/project/tricon/client`. Use **one set of certs per environment**: test ES cert for test, prod ES cert for prod.

---

## Summary

| Environment | How to get certs | trustAll |
|-------------|------------------|----------|
| Local / dev | Run `./rule_engine/scripts/generate-es-ssl-certs.sh` | Can use `true` (docker profile); certs optional then |
| Test        | Export test EagleSoft server cert → create cacerts.jks + keystore.jks (steps above) | `false` – certs required |
| Prod        | Export prod EagleSoft server cert → create cacerts.jks + keystore.jks (steps above) | `false` – certs required |

So: **cacerts.jks** = “which servers I trust” (must include the EagleSoft server’s cert). **keystore.jks** = “my client identity” (needed because the code path for prod requires both files; the ES server may or may not require client auth depending on its config).
