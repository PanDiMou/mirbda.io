# mirbda.io

API REST Bitcoin — gateway vers un nœud Bitcoin Core.
Spring Boot 4.0.1 · Java 25 · Spring WebFlux (Netty) · Reactive non-blocking

---

## Endpoints disponibles

> `POST` sur tous les endpoints · Base URL : `https://api.mirbda.io`

### ⛓️ Blockchain

| | Endpoint | Path | Description |
|--|----------|------|-------------|
| ℹ️ | `/blockchain` | `POST /v1/blockchain` | Vue d'ensemble complète de l'état de la blockchain (hauteur, difficulté, softforks, synchronisation…) |
| 🏆 | `/blockchain/best-block-hash` | `POST /v1/blockchain/best-block-hash` | Hash du bloc le plus récent au sommet de la chaîne |
| 📏 | `/blockchain/block-count` | `POST /v1/blockchain/block-count` | Hauteur actuelle de la blockchain (nombre de blocs validés) |
| 🌿 | `/blockchain/chain-tips` | `POST /v1/blockchain/chain-tips` | Tous les sommets connus de l'arbre de blocs, incluant les forks alternatifs |
| 📊 | `/blockchain/chain-tx-stats` | `POST /v1/blockchain/chain-tx-stats` | Statistiques sur les transactions dans une fenêtre récente de blocs |
| ⚙️ | `/blockchain/difficulty` | `POST /v1/blockchain/difficulty` | Difficulté actuelle de preuve de travail, en multiple de la difficulté minimale |
| 🔀 | `/blockchain/deployments` | `POST /v1/blockchain/deployments` | État d'activation de tous les softforks (Taproot, Segwit…) — type, status, hauteur d'activation, signalling BIP9 |
| 🗂️ | `/blockchain/index-info` | `POST /v1/blockchain/index-info` | État de synchronisation des index actifs du nœud (txindex, coinstatsindex, blockfilterindex…) — filtrable par nom d'index |

### 📦 Bloc

| | Endpoint | Path | Description |
|--|----------|------|-------------|
| 📦 | `/block` | `POST /v1/block` | Informations détaillées sur un bloc spécifique par son hash (verbosité 0, 1 ou 2) |
| 🔗 | `/block/hash` | `POST /v1/block/hash` | Hash d'un bloc à une hauteur donnée dans la meilleure blockchain |
| 📋 | `/block/header` | `POST /v1/block/header` | En-tête de bloc par hash — format JSON ou hexadécimal brut |
| 📈 | `/block/stats` | `POST /v1/block/stats` | Statistiques analytiques d'un bloc (fees, feerates, tailles, segwit…) — par hash ou hauteur, champs sélectionnables |

### 🏊 Mempool

| | Endpoint | Path | Description |
|--|----------|------|-------------|
| 🧊 | `/mempool` | `POST /v1/mempool` | État actuel du pool de transactions non confirmées (taille, frais, charge…) |
| 📜 | `/mempool/raw` | `POST /v1/mempool/raw` | Liste brute de tous les IDs de transactions en attente de confirmation |
| 🔍 | `/mempool/entry` | `POST /v1/mempool/entry` | Détails d'une transaction en attente par txid (fees, poids, ancestors, descendants…) |
| 🧬 | `/mempool/ancestors` | `POST /v1/mempool/ancestors` | Transactions parentes d'une tx en attente (txid requis, verbose optionnel) — liste de txids ou détail complet |
| 🔽 | `/mempool/descendants` | `POST /v1/mempool/descendants` | Transactions enfants d'une tx en attente (txid requis, verbose optionnel) — liste de txids ou détail complet |

### 🪙 UTXO

| | Endpoint | Path | Description |
|--|----------|------|-------------|
| 🪙 | `/utxo` | `POST /v1/utxo` | Détails d'une sortie de transaction non dépensée (UTXO) par txid et index |
| 🌳 | `/utxo/proof` | `POST /v1/utxo/proof` | Preuve d'inclusion Merkle (hex) qu'une transaction a bien été incluse dans un bloc |
| 🧾 | `/utxo/verify-proof` | `POST /v1/utxo/verify-proof` | Vérifie une preuve Merkle et retourne le ou les txid confirmés |
| 🗃️ | `/utxo/set-info` | `POST /v1/utxo/set-info` | Statistiques sur l'ensemble UTXO (offre totale, taille, hauteur…) |

---

### 💸 Transaction

| | Endpoint | Path | Description |
|--|----------|------|-------------|
| 🔎 | `/transaction/raw` | `POST /v1/transaction/raw` | Transaction brute ou décodée par txid — format JSON (verbose) ou hexadécimal |
| 🧩 | `/transaction/decode-raw` | `POST /v1/transaction/decode-raw` | Décode une transaction brute (hex) hors-ligne — structure complète (vin, vout, witness, types de scripts) |
| 🧪 | `/transaction/test-mempool-accept` | `POST /v1/transaction/test-mempool-accept` | Teste si une ou plusieurs transactions brutes (hex) seraient acceptées par le mempool — sans les diffuser. Retourne `allowed`, `vsize`, les fees effectives ou la raison du rejet. `maxFeeRate` optionnel (BTC/kvB, défaut 0.10) |

---

### 🌐 Réseau

| | Endpoint | Path | Description |
|--|----------|------|-------------|
| 🌐 | `/network` | `POST /v1/network` | Informations complètes sur le réseau P2P du nœud (version, connexions, frais de relay, interfaces réseau…) |
| 🔌 | `/network/connection-count` | `POST /v1/network/connection-count` | Nombre de connexions actives du nœud Bitcoin avec ses pairs P2P |
| 📡 | `/network/net-totals` | `POST /v1/network/net-totals` | Trafic réseau total du nœud (octets reçus, envoyés, horodatage) |
| 🚫 | `/network/list-banned` | `POST /v1/network/list-banned` | Liste des IPs et subnets bannis par le nœud avec leurs dates de ban |
| 👥 | `/network/peers` | `POST /v1/network/peers` | Détails de chaque pair P2P connecté (adresse, version, ping, bytes, hauteur sync, services, transport…) |
| 📍 | `/network/node-addresses` | `POST /v1/network/node-addresses` | Adresses de pairs connus — filtrables par `count` et `network` (ipv4, ipv6, onion, i2p, cjdns) |

---

### 🎛️ Contrôle

| | Endpoint | Path | Description |
|--|----------|------|-------------|
| ⏱️ | `/control/uptime` | `POST /v1/control/uptime` | Durée de fonctionnement du nœud Bitcoin en secondes depuis son dernier démarrage |
| 🧠 | `/control/memory-info` | `POST /v1/control/memory-info` | Statistiques mémoire du nœud — heap utilisé, libre, total, mémoire verrouillée et nombre de chunks |

---

### ⛏️ Minage

| | Endpoint | Path | Description |
|--|----------|------|-------------|
| ⛏️ | `/mining` | `POST /v1/mining` | Informations sur le minage (hauteur, difficulté, hashrate réseau, transactions en attente…) |
| 🔢 | `/mining/network-hash-ps` | `POST /v1/mining/network-hash-ps` | Hashrate réseau estimé (H/s) sur les N derniers blocs — `nblocks` (défaut 120, -1 = depuis dernier ajustement) et `height` optionnels |

---

### 🔧 Utilitaires

| | Endpoint | Path | Description |
|--|----------|------|-------------|
| 💸 | `/util/estimate-smart-fee` | `POST /v1/util/estimate-smart-fee` | Estimation du fee rate optimal (BTC/kB) pour une confirmation dans N blocs |
| ✅ | `/util/validate-address` | `POST /v1/util/validate-address` | Valide une adresse Bitcoin et retourne son type (P2PKH, P2SH, bech32, taproot…), le scriptPubKey associé et les éventuelles erreurs de décodage |
| 📜 | `/util/decode-script` | `POST /v1/util/decode-script` | Décode un script Bitcoin hexadécimal — ASM, type, adresse, descripteur, wrapping P2SH et segwit |

---

## Démarrage rapide

```bash
./mvnw spring-boot:run           # Dev — localhost:8080
./mvnw test                      # Tests
./deploy.sh                      # Build Docker + déploiement (port 7777)
```

## Stack

| Couche | Technologie |
|--------|-------------|
| Framework | Spring Boot 4.0.1 + WebFlux (Netty) |
| Langage | Java 25 |
| Build | Maven |
| Déploiement | Docker + Nginx (api.mirbda.io) |
| Tests | Spring Boot Test + WebTestClient |
