#!/usr/bin/env bash
#
# Generate self-signed SSL certs for local EagleSoft (ES) ↔ Rule Engine TLS.
# Run from repo root:  ./rule_engine/scripts/generate-es-ssl-certs.sh
#
# Output (in rule_engine/certs/):
#   - cacerts.jks   (client truststore – Rule Engine)
#   - keystore.jks  (client keystore – Rule Engine)
#   - server.jks    (server keystore – use on ES server if you run it locally)
#   - server.cer    (exported cert, for importing into truststores)
#
# Password used: p@ssw0rd (matches application-docker.properties; override with ES_SSL_PASS env)
#

set -e
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
CERTS_DIR="$(cd "$SCRIPT_DIR/../certs" && pwd)"
PASS="${ES_SSL_PASS:-p@ssw0rd}"
ALIAS="${ES_SSL_ALIAS:-es-local}"

echo "Using certs dir: $CERTS_DIR"
mkdir -p "$CERTS_DIR"
cd "$CERTS_DIR"

# 1. Server keystore (for ES server; Rule Engine client will trust this cert)
echo "Generating server keypair..."
keytool -genkeypair -alias "$ALIAS" -keyalg RSA -keysize 2048 -validity 365 \
  -keystore server.jks -storepass "$PASS" -keypass "$PASS" \
  -dname "CN=localhost, OU=Dev, O=Capline, L=City, ST=State, C=US"

# 2. Export server cert so client can trust it
echo "Exporting server cert..."
keytool -exportcert -alias "$ALIAS" -keystore server.jks -storepass "$PASS" -file server.cer

# 3. Client truststore (Rule Engine: trust the ES server)
echo "Creating client truststore (cacerts.jks)..."
keytool -importcert -alias "$ALIAS" -file server.cer -keystore cacerts.jks -storepass "$PASS" -noprompt

# 4. Client keystore (Rule Engine: needed for default SSLContext; use same cert for simplicity)
echo "Creating client keystore (keystore.jks)..."
cp server.jks keystore.jks

echo ""
echo "Done. Generated in $CERTS_DIR:"
ls -la cacerts.jks keystore.jks server.jks server.cer 2>/dev/null || true
echo ""
echo "Password: $PASS (set ES_SSL_PASS to override)"
echo ""
echo "Rule Engine (local / Docker):"
echo "  - Point trustStore to: $CERTS_DIR/cacerts.jks"
echo "  - Point keyStore to:   $CERTS_DIR/keystore.jks"
echo "  - Password:           $PASS"
echo ""
echo "Docker: mount certs and use docker profile, e.g.:"
echo "  volumes:"
echo "    - ./rule_engine/certs:/opt/project/tricon/client"
echo "  environment:"
echo "    - SPRING_PROFILES_ACTIVE=docker"
echo ""
echo "If your ES server runs elsewhere with its own cert, import that server's cert into cacerts.jks:"
echo "  keytool -importcert -alias es-remote -file /path/to/server.cer -keystore $CERTS_DIR/cacerts.jks -storepass $PASS -noprompt"
echo ""
