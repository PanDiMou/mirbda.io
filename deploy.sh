#!/bin/bash
set -euo pipefail

# ==============================================================
# CONFIGURATION
# ==============================================================
readonly ENV_FILE="/home/pandimou/.env.mirbda"
readonly CONTAINER="mirbda-api"
readonly IMAGE="mirbda-api"
readonly PORT=7777
readonly DOCKER_NETWORK="mirbda-net"
readonly SSH_KEY="${HOME}/.ssh/id_ed25519_github_pullandpush"
readonly GIT_BRANCH="main"
readonly HEALTH_URL="http://localhost:${PORT}/health"
readonly HEALTH_RETRIES=20
readonly HEALTH_INTERVAL=3
readonly LOCK_FILE="/tmp/deploy-${CONTAINER}.lock"
readonly LOG_FILE="${HOME}/deploy-mirbda-api.log"
readonly MIN_DISK_MB=1024

COMMIT=""
PREVIOUS_IMAGE=""
ROLLBACK_DONE=false

# ==============================================================
# JOURNALISATION
# ==============================================================
log() {
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] $*" | tee -a "$LOG_FILE"
}

# ==============================================================
# ROLLBACK
# ==============================================================
rollback() {
    [[ "$ROLLBACK_DONE" == "true" ]] && return
    ROLLBACK_DONE=true
    if [[ -n "$PREVIOUS_IMAGE" ]] && docker image inspect "$PREVIOUS_IMAGE" &>/dev/null; then
        log "↩️   Rollback : restauration de $PREVIOUS_IMAGE"
        docker stop "$CONTAINER" 2>/dev/null || true
        docker rm --force "$CONTAINER" 2>/dev/null || true
        docker run -d \
            --name "$CONTAINER" \
            --network "$DOCKER_NETWORK" \
            --restart unless-stopped \
            --security-opt no-new-privileges:true \
            --log-opt max-size=10m \
            --log-opt max-file=3 \
            -p "${PORT}:${PORT}" \
            -e RPC_BASE_URL="${RPC_BASE_URL}" \
            "$PREVIOUS_IMAGE"
        log "✅ Rollback terminé — ancienne version en service"
    else
        log "⚠️   Aucune image précédente disponible — rollback impossible"
    fi
}

# ==============================================================
# NETTOYAGE À LA SORTIE
# ==============================================================
cleanup() {
    local exit_code=$?
    if [[ $exit_code -ne 0 ]]; then
        log "❌ Déploiement échoué (code $exit_code) — rollback en cours"
        rollback
        [[ -n "$COMMIT" ]] && docker rmi "${IMAGE}:${COMMIT}" 2>/dev/null || true
    fi
    rm -f "$LOCK_FILE"
    log "🔓 Verrou libéré"
}
trap cleanup EXIT

# ==============================================================
# VÉRIFICATIONS PRÉALABLES
# ==============================================================
mkdir -p "$(dirname "$LOG_FILE")"
log "🔍 Vérifications préalables"

if [[ ! -f "pom.xml" ]]; then
    log "❌ pom.xml introuvable — le script doit être lancé depuis la racine du projet ($(pwd))"
    exit 1
fi

if ! git rev-parse --is-inside-work-tree &>/dev/null; then
    log "❌ Répertoire courant n'est pas un dépôt git : $(pwd)"
    exit 1
fi
log "📁 Répertoire projet : $(pwd)"

if [[ ! -f "$ENV_FILE" ]]; then
    log "❌ Fichier d'environnement introuvable : $ENV_FILE"
    exit 1
fi
# shellcheck source=/dev/null
source "$ENV_FILE"

if ! docker info &>/dev/null; then
    log "❌ Docker n'est pas accessible"
    exit 1
fi

if [[ -z "${RPC_BASE_URL:-}" ]]; then
    log "❌ Variable RPC_BASE_URL non définie dans $ENV_FILE"
    exit 1
fi

if ! docker network inspect "$DOCKER_NETWORK" &>/dev/null; then
    log "❌ Réseau Docker '$DOCKER_NETWORK' introuvable"
    exit 1
fi

if [[ ! -f "$SSH_KEY" ]]; then
    log "❌ Clé SSH introuvable : $SSH_KEY"
    exit 1
fi

DISK_FREE_MB=$(df -m "$(pwd)" | awk 'NR==2 {print $4}')
if (( DISK_FREE_MB < MIN_DISK_MB )); then
    log "❌ Espace disque insuffisant : ${DISK_FREE_MB} Mo disponibles (minimum ${MIN_DISK_MB} Mo requis)"
    exit 1
fi
log "💽 Espace disque disponible : ${DISK_FREE_MB} Mo"

# ==============================================================
# VERROU DE DÉPLOIEMENT
# ==============================================================
if [[ -e "$LOCK_FILE" ]]; then
    LOCK_PID=$(cat "$LOCK_FILE" 2>/dev/null || echo "")
    if [[ -n "$LOCK_PID" ]] && kill -0 "$LOCK_PID" 2>/dev/null; then
        log "❌ Un déploiement est déjà en cours (PID $LOCK_PID)"
        exit 1
    else
        log "⚠️   Verrou orphelin détecté (PID ${LOCK_PID:-?}) — nettoyage"
        rm -f "$LOCK_FILE"
    fi
fi
echo $$ > "$LOCK_FILE"
log "🔒 Verrou acquis (PID $$)"

# ==============================================================
# GIT
# ==============================================================
log "📥 Récupération du code (branche $GIT_BRANCH)"
git reset --hard
git clean -fd
GIT_SSH_COMMAND="ssh -i ${SSH_KEY} -o StrictHostKeyChecking=no" \
    git pull --rebase origin "$GIT_BRANCH"
COMMIT=$(git rev-parse --short HEAD)
log "📌 Commit : $COMMIT"

# ==============================================================
# SAUVEGARDE DE L'IMAGE COURANTE
# ==============================================================
PREVIOUS_IMAGE=$(docker inspect --format='{{.Config.Image}}' "$CONTAINER" 2>/dev/null || echo "")
if [[ -n "$PREVIOUS_IMAGE" ]]; then
    log "💾 Image en service sauvegardée : $PREVIOUS_IMAGE"
fi
if docker image inspect "${IMAGE}:latest" &>/dev/null; then
    docker tag "${IMAGE}:latest" "${IMAGE}:previous"
    log "🏷  Tag de secours : ${IMAGE}:previous"
fi

# ==============================================================
# BUILD
# ==============================================================
log "🏗  Build de l'image Docker — $COMMIT"
docker build --pull --build-arg GITHUB_TOKEN="${GITHUB_TOKEN}" -t "${IMAGE}:${COMMIT}" .

# ==============================================================
# DÉPLOIEMENT
# ==============================================================
log "🧹 Arrêt du conteneur existant"
docker stop "$CONTAINER" 2>/dev/null || true
docker rm --force "$CONTAINER" 2>/dev/null || true

log "🚀 Démarrage du nouveau conteneur"
docker run -d \
    --name "$CONTAINER" \
    --network "$DOCKER_NETWORK" \
    --restart unless-stopped \
    --security-opt no-new-privileges:true \
    --log-opt max-size=10m \
    --log-opt max-file=3 \
    -p "${PORT}:${PORT}" \
    -e RPC_BASE_URL="${RPC_BASE_URL}" \
    --label "deploy.commit=${COMMIT}" \
    --label "deploy.date=$(date --iso-8601=seconds)" \
    "${IMAGE}:${COMMIT}"

# ==============================================================
# HEALTH CHECK
# ==============================================================
log "🩺 Health check (max ${HEALTH_RETRIES} tentatives × ${HEALTH_INTERVAL}s)"
HEALTHY=false
for i in $(seq 1 "$HEALTH_RETRIES"); do
    HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" "${HEALTH_URL}" || echo "000")
    if [[ "$HTTP_CODE" == "200" ]]; then
        log "✅ Health check OK — HTTP ${HTTP_CODE} (tentative $i)"
        HEALTHY=true
        break
    fi
    log "⏳ Tentative $i/$HEALTH_RETRIES — HTTP ${HTTP_CODE} — attente ${HEALTH_INTERVAL}s"
    sleep "$HEALTH_INTERVAL"
done

if [[ "$HEALTHY" != "true" ]]; then
    log "❌ Health check échoué après $((HEALTH_RETRIES * HEALTH_INTERVAL))s"
    exit 1
fi

# ==============================================================
# PROMOTION DE L'IMAGE
# ==============================================================
docker tag "${IMAGE}:${COMMIT}" "${IMAGE}:latest"
log "🏷  Promotion : ${IMAGE}:${COMMIT} → ${IMAGE}:latest"

# ==============================================================
# NETTOYAGE
# ==============================================================
log "🗑  Suppression des images obsolètes"
docker image prune -f >/dev/null

log "🎉 Backend déployé — commit ${COMMIT} en service"
