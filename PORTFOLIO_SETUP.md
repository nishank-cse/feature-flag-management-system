# Feature Flag Platform — Full-Stack Portfolio Project

A full-stack feature flag service for gradual releases and per-environment configuration. This repository includes a Java/Spring Boot control-plane API, a Go evaluation API, a Next.js/React dashboard, PostgreSQL, Redis, and Java/Node SDKs.

> Portfolio note: this is a strong starting codebase, not a guarantee of FAANG interviews. Understand the architecture, test it, document your own changes, and never claim benchmark numbers you have not reproduced yourself.

## Stack

- **Control plane:** Java 21, Spring Boot, Spring Security, JWT, JPA
- **Evaluation data plane:** Go, Chi, in-memory cache, Redis
- **Dashboard:** Next.js, React, TypeScript, Tailwind CSS
- **Storage/cache:** PostgreSQL 16, Redis 7
- **SDKs:** Java and Node.js

## Fastest start (Docker Desktop)

Prerequisites: Docker Desktop with the Linux engine running. Docker Compose v2 is included with current Docker Desktop installations.

From the extracted `feature-flag-main` folder:

```powershell
docker info
docker compose up --build -d
docker compose ps
```

Wait for the containers to finish building and start. Open:

- Dashboard: http://localhost:3000
- Admin API health: http://localhost:8080/actuator/health
- Evaluation API health: http://localhost:8081/health
- Evaluation API readiness: http://localhost:8081/ready
- PostgreSQL from Windows: `localhost:5433` (container port remains `5432`)
- Redis: `localhost:6379`

The PostgreSQL host port is intentionally `5433` to avoid conflicts with a local Windows PostgreSQL service already using `5432`. Containers connect to PostgreSQL using `db:5432` on the Compose network.

Follow the dashboard flow to register the first user. The project README documents that the first registered user is promoted to Admin. Once signed in, create an environment, then a flag and an environment-specific flag value. Copy the environment API key to call the Evaluation API.

Example evaluation request (replace the key and flag key with values created in your dashboard):

```powershell
curl.exe -H "X-API-Key: YOUR_ENVIRONMENT_API_KEY" "http://localhost:8081/evaluate/YOUR_FLAG_KEY?user=user-123"
```

### Stop services

```powershell
docker compose down
```

This keeps local database files. To remove persistent local data, manually back up first and then remove the `data/db` and `data/redis` folders. Do not delete them if you need to preserve your data.

## Local development without building all services in Docker

Start only infrastructure:

```powershell
docker compose up -d db redis
```

Run the Admin API in one terminal:

```powershell
cd services/admin-api
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5433/featureflags"
$env:SPRING_DATASOURCE_USERNAME="postgres"
$env:SPRING_DATASOURCE_PASSWORD="postgres"
$env:SPRING_DATA_REDIS_HOST="localhost"
$env:SPRING_DATA_REDIS_PORT="6379"
.\mvnw.cmd spring-boot:run
```

Run the Evaluation API in a second terminal:

```powershell
cd services/evaluation-api
$env:DB_HOST="localhost"
$env:DB_PORT="5433"
$env:DB_USER="postgres"
$env:DB_PASSWORD="postgres"
$env:DB_NAME="featureflags"
$env:REDIS_HOST="localhost"
$env:REDIS_PORT="6379"
go run ./cmd/server
```

Run the dashboard in a third terminal:

```powershell
cd dashboard
Copy-Item .env.local.example .env.local
npm ci
npm run dev
```

The Java service requires Java 21. The dashboard requires Node.js 22 recommended for this packaged setup. The evaluation API requires a Go version compatible with its `go.mod`.

## Suggested portfolio upgrades

1. Add integration tests for percentage rollouts and cache invalidation.
2. Add rate limiting and request validation for public evaluation endpoints.
3. Add audit history for every flag/environment mutation.
4. Add OpenTelemetry tracing and metrics dashboards.
5. Run repeatable load tests and publish the exact machine, Docker settings, test duration, request mix, and raw results.
6. Add CI that runs Java tests, Go tests, and frontend lint/build on every pull request.
7. Document failure modes: Redis unavailable, stale cache, invalid API key, database restart, and concurrent flag updates.

## Architecture overview

- **Dashboard → Admin API:** manage users, environments, flags, and rollout values.
- **Admin API → PostgreSQL:** durable source of truth.
- **Admin API → Redis Pub/Sub:** publish cache invalidation events after mutations.
- **Evaluation API:** read-only flag evaluation using the database and two-tier caching.
- **SDKs:** consume evaluation endpoints from Java or Node applications.

Read the original `README.md` and module-specific documentation for API details, SDK usage, and benchmarking methodology.

## Security before deployment

The repository has development defaults for local use. Before deploying publicly, replace all local credentials and JWT secrets, configure real CORS origins, review OAuth settings, enable TLS, and use managed secrets. Do not commit `.env` files, real API keys, or production credentials.
