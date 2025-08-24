# Teste Back-end Java (FIPE)

Este repositório contém **duas APIs** (Quarkus/Java 17) e um **docker-compose** para levantar
todo o ambiente (RabbitMQ, Redis, Postgres) e permitir testes locais.

- **API-1**: expõe endpoints REST para acionar a **carga inicial** (1.1), buscar **marcas** na FIPE (1.2),
  **enfileirar** marcas para a API-2 (1.3), consultar **marcas** (1.6), consultar **códigos, modelos e observações** por marca (1.7)
  e **atualizar modelo/observações** (1.8). Implementa **cache com Redis** (1.9) e **autenticação Basic** (1.10).
- **API-2**: consome as marcas da fila (RabbitMQ), busca **modelos** na FIPE (1.4) e persiste
  **código, marca e modelo** no banco SQL (1.5).

## Tecnologias
- Java 17, Quarkus 3.13
- RESTEasy Reactive + Jackson (REST/JSON)
- Hibernate ORM Panache + Flyway (SQL)
- RabbitMQ (SmallRye Reactive Messaging)
- Redis (quarkus-redis-client) — cache
- Postgres (docker) e H2 (local)
- OpenAPI/Swagger UI
- Autenticação Basic (arquivo)

## Subir tudo com Docker
Pré-requisitos: Docker Desktop.

```bash
docker compose up --build
```

Serviços:
- API-1: http://localhost:8080/q/swagger-ui
- API-2: http://localhost:8081/q/swagger-ui
- RabbitMQ: http://localhost:15672 (guest/guest)
- Redis: localhost:6379
- Postgres: localhost:5432 (magnum/magnum, DB: magnum)

**Usuários (Basic Auth):**
- `admin:admin` (roles: admin,user)
- `user:user` (roles: user)

> Dica: Use o Postman e configure Basic Auth.

## Rodar sem Docker (H2 local)
Em cada API:

```bash
cd api-1
mvn quarkus:dev
# Em outro terminal
cd api-2
mvn quarkus:dev
```

## Autenticação
- Endpoints `/admin/**` exigem **role `admin`**.
- Demais endpoints exigem `user` ou `admin`.

## Documentação dos Endpoints (API-1)
Base URL: `http://localhost:8080`

### 1. Carga Inicial (1.1)
`POST /admin/fipe/brands/enqueue?type=cars`  
Busca marcas na FIPE e envia cada uma para a fila `brands-queue`.

**Auth:** `admin`  
**200**: `{"status":"ok","type":"cars","reference":null,"sent":120}`

### 2. Buscar marcas direto na FIPE (1.2)
`GET /api/fipe/brands?type=cars&reference=308`

**Auth:** `user` ou `admin`  
**200**: `[{"code":"23","name":"VW - VolksWagen"}, ...]`

### 3. Buscar marcas no banco (1.6)
`GET /api/brands`  
**Auth:** `user` ou `admin`  
**200**: `[{"id":1,"vehicleType":"cars","code":"23","name":"VW - VolksWagen"}, ...]`

### 4. Buscar modelos por marca (1.7)
`GET /api/vehicles?brandCode=23`  
**Auth:** `user` ou `admin`  
**200**: `[{"id":10,"code":"1234","name":"Gol 1.6","observations":null, ...}]`

### 5. Atualizar modelo/observações (1.8)
`PUT /api/vehicles/{id}`  
Body (qualquer campo é opcional):
```json
{ "name": "Novo Nome", "observations": "text..." }
```
**Auth:** `admin`  
**200**: objeto atualizado.

## Fluxo de Fila (1.3, 1.4, 1.5)
1. `POST /admin/fipe/brands/enqueue` (API-1) → publica cada marca como JSON em `brands-exchange` com `routingKey=brands.created`.
2. API-2 consome da `brands-queue`, para cada marca:
   - chama FIPE `/cars/brands/{code}/models` (1.4);
   - persiste em `brand` e `vehicle_model` (1.5).

## Banco de Dados
- Em Docker: **Postgres** (tabela `brand` e `vehicle_model` via Flyway V1).
- Local (sem Docker): **H2** arquivo sob `./data` de cada API.

## Cache (1.9)
- Implementado em API-1 usando Redis Client de forma simples (key-value JSON).
- Ex.: cachear respostas de `/api/brands` e `/api/vehicles?brandCode=` por 300s.

## Exemplos de Teste com `curl`

```bash
# Login admin
curl -u admin:admin -X POST "http://localhost:8080/admin/fipe/brands/enqueue?type=cars"

# Marcas da FIPE (direto)
curl -u user:user "http://localhost:8080/api/fipe/brands?type=cars"

# Após processamento da API-2, consulte marcas no banco
curl -u user:user "http://localhost:8080/api/brands"

# Modelos por marca
curl -u user:user "http://localhost:8080/api/vehicles?brandCode=23"

# Atualizar modelo/observações
curl -u admin:admin -X PUT "http://localhost:8080/api/vehicles/10"   -H "Content-Type: application/json"   -d '{"name":"Novo Nome","observations":"Obs..."}'
```

## Observações
- Este projeto é de demonstração, so um teste mesmo;
- A API da FIPE pública possui limites de requisição diários.
