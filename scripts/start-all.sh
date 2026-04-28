#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"

VERSION="1.0.0-SNAPSHOT"
MODULES=(students-api courses-api teachers-api classes-api grades-api gateway)

export KEYCLOAK_ISSUER_URI="${KEYCLOAK_ISSUER_URI:-http://localhost:8180/realms/cesmac}"

if ! command -v mvn >/dev/null 2>&1; then
  echo "Maven não encontrado no PATH." >&2
  exit 1
fi
if ! command -v java >/dev/null 2>&1; then
  echo "Java não encontrado no PATH." >&2
  exit 1
fi

mkdir -p "$ROOT/scripts/logs"
PIDFILE="$ROOT/scripts/.pids"
rm -f "$PIDFILE"

if [[ "${SKIP_BUILD:-0}" != "1" ]]; then
  echo "Compilando módulos (SKIP_BUILD=1 pula esta etapa)..."
  mvn -q -DskipTests package
fi

for mod in "${MODULES[@]}"; do
  jar="$ROOT/$mod/target/${mod}-${VERSION}.jar"
  if [[ ! -f "$jar" ]]; then
    echo "JAR ausente: $jar — rode 'mvn package' ou remova SKIP_BUILD=1." >&2
    exit 1
  fi
  log="$ROOT/scripts/logs/${mod}.log"
  nohup java -jar "$jar" >>"$log" 2>&1 &
  echo $! >>"$PIDFILE"
  echo "Iniciado $mod (PID $!, log: scripts/logs/${mod}.log)"
  sleep "${START_STAGGER_SEC:-2}"
done

echo
echo "Pronto. Keycloak deve estar em ${KEYCLOAK_ISSUER_URI%/realms/cesmac}"
echo "Gateway: http://localhost:8080 — exemplo: GET /api/students/info com Bearer token"
echo "PIDs salvos em scripts/.pids — para encerrar: scripts/stop-all.sh"
