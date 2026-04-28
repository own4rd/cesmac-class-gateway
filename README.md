# CESMAC — microsserviços + Spring Cloud Gateway + Keycloak

Projeto didático: cinco APIs REST em Spring Boot (dados em memória, sem banco), um **Spring Cloud Gateway** na porta 8080 e **Keycloak** para emitir JWTs.

## Módulos e portas

| Módulo        | Porta | Rota no gateway        |
|---------------|-------|-------------------------|
| `gateway`     | 8080  | —                       |
| `students-api`| 8081  | `/api/students/**`      |
| `courses-api` | 8082  | `/api/courses/**`       |
| `teachers-api`| 8083  | `/api/teachers/**`      |
| `classes-api` | 8084  | `/api/classes/**`       |
| `grades-api`  | 8085  | `/api/grades/**`        |

Cada API expõe `GET /info` (via gateway: `GET /api/{nome}/info`). Gateway e APIs validam o JWT com o mesmo `issuer-uri` do Keycloak.

## Pré-requisitos

- Java 21  
- Maven 3.9+  
- Docker (para o Keycloak)

## 1. Subir o Keycloak

Na raiz do repositório:

```bash
docker compose up -d
```

- Console admin: `http://localhost:8180/admin` (usuário `admin`, senha `admin`)  
- Realm importado: **cesmac**  
- Cliente confidencial: **cesmac-api**, secret: **cesmac-secret**  
- Usuário de teste: **aluno** / **aluno123**

Se o import do realm falhar na primeira subida, pare o container, apague o volume se houver, e suba de novo; ou crie o realm manualmente espelhando `infra/keycloak/cesmac-realm.json`.

## 2. Obter um token

**Resource owner password** (apenas para laboratório):

```bash
TOKEN=$(curl -s -X POST 'http://localhost:8180/realms/cesmac/protocol/openid-connect/token' \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'grant_type=password' \
  -d 'client_id=cesmac-api' \
  -d 'client_secret=cesmac-secret' \
  -d 'username=aluno' \
  -d 'password=aluno123' | jq -r .access_token)
```

**Client credentials** (conta de serviço do cliente):

```bash
TOKEN=$(curl -s -X POST 'http://localhost:8180/realms/cesmac/protocol/openid-connect/token' \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'grant_type=client_credentials' \
  -d 'client_id=cesmac-api' \
  -d 'client_secret=cesmac-secret' | jq -r .access_token)
```

## 3. Compilar tudo

```bash
mvn -q clean package
```

## 4. Executar

### Um comando (recomendado para aula)

Com o Keycloak já no ar:

```bash
chmod +x scripts/start-all.sh scripts/stop-all.sh   # só na primeira vez
./scripts/start-all.sh
```

Isso compila (`mvn package`), sobe os seis JARs em segundo plano e grava logs em `scripts/logs/`. Para encerrar: `./scripts/stop-all.sh`.

Variáveis opcionais: `SKIP_BUILD=1` (reusa JARs já gerados), `START_STAGGER_SEC=1` (intervalo entre subidas), `KEYCLOAK_ISSUER_URI=...`.

### Terminais separados (alternativa)

```bash
mvn -pl students-api spring-boot:run
mvn -pl courses-api spring-boot:run
mvn -pl teachers-api spring-boot:run
mvn -pl classes-api spring-boot:run
mvn -pl grades-api spring-boot:run
mvn -pl gateway spring-boot:run
```

Variável opcional (se o issuer não for o padrão):

```bash
export KEYCLOAK_ISSUER_URI=http://localhost:8180/realms/cesmac
```

## 5. Testar pelo gateway

```bash
curl -s -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/students/info | jq .
```

Repita trocando `students` por `courses`, `teachers`, `classes` ou `grades`.

## Arquitetura resumida

O gateway autentica cada requisição com OAuth2 Resource Server (JWT) e encaminha para o microsserviço correspondente (filtro `StripPrefix=2` para remover `/api/{serviço}`). Cada microsserviço valida de novo o mesmo JWT — cenário típico de aula para mostrar confiança zero entre processos; em produção costuma-se discutir passagem de identidade, audiences e rede interna.
