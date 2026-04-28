#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
PIDFILE="$ROOT/scripts/.pids"

if [[ ! -f "$PIDFILE" ]]; then
  echo "Nenhum PID em scripts/.pids (nada para encerrar ou já foi limpo)." >&2
  exit 0
fi

while read -r pid; do
  [[ -z "$pid" ]] && continue
  if kill "$pid" 2>/dev/null; then
    echo "Encerrado PID $pid"
  else
    echo "PID $pid já não existe" >&2
  fi
done <"$PIDFILE"

rm -f "$PIDFILE"
echo "Concluído."
