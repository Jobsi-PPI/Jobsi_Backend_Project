# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

Jobsy is a Spring Boot 3.5 / Java 21 REST backend for a job-matching platform, built as an academic exercise in Clean / Hexagonal Architecture. Code, comments, and domain names are in Spanish; keep new code consistent with that.

## Commands

The project uses the Maven Wrapper (`mvnw` / `mvnw.cmd`) — no global Maven needed. On Windows use `mvnw.cmd`.

```bash
# Run (dev): PostgreSQL + Mailpit en contenedores vía Testcontainers — requiere Docker Desktop
mvnw.cmd spring-boot:test-run                               # usa src/test/.../TestJobsyApplication
# Run (prod): PostgreSQL externo configurado por BD_* en el entorno / .env
mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=prod

# Build
mvnw.cmd package                 # build jar (corre los tests: requiere Docker)
mvnw.cmd package -DskipTests     # skip tests

# Tests (Testcontainers + GreenMail; requieren Docker Desktop)
mvnw.cmd test                                  # all tests
mvnw.cmd test -Dtest=AdminControllerTest       # single test class

# Docker (app + Postgres + Mailpit, prod profile)
docker compose up --build
```

App runs on `http://localhost:8080`. Swagger UI at `/swagger-ui.html`, health check at `/health` (public `{"status":"UP"}`; details only for ADMIN), plus `/health/liveness` and `/health/readiness`. In dev the Mailpit inbox URL is logged at startup (`Bandeja de Mailpit: ...`); with docker compose it is `http://localhost:8025`.

## Configuration

`application.properties` references env vars supplied by the `.env` file (see `.env.example`) or Docker Compose: `SPRING_PROFILE` (default `dev`), `SERVER_PORT`, `BD_*`, `BD_SCHEMA` (default `jobsi`), `CORS_*`, `JWT_SECRET` (required in prod, ≥32 chars), `ADMIN_*`, `MAIL_*`, `APP_PUBLIC_URL`, `FRONTEND_URL`. Profiles:
- **dev** — datasource and SMTP provided by Testcontainers (`TestcontainersConfiguration`, `DevServicesConfiguration`); data is lost on stop. Default JWT secret and admin (`admin@jobsi.local` / `Admin12345`).
- **prod** — PostgreSQL from `BD_URL`.
- **test** — `src/test/resources/application-test.properties`.

**Schema is owned by Flyway** (`src/main/resources/db/migration`, `V1` schema, `V2` reference catalogs, `V3` password-reset tokens) and Hibernate runs with `ddl-auto=validate`. Any entity change needs a new `V<n>__*.sql` migration; never edit an applied migration. Column names follow Spring's default naming (snake_case lowercase of the `@Column` name).

`AdminInitializer` creates the initial ADMIN user from `app.admin.*` (`ADMIN_EMAIL`, `ADMIN_PASSWORD`, …) if no user with that email exists. Catalogs (roles, géneros, estados, categorías, tipos de pago, ubicaciones, categorías de valoración) come from `V2__datos_referencia.sql`.

## Architecture

Hexagonal layering under `com.escaes.jobsy`. A request flows: **controller → use case → domain repository (port) → JPA adapter → Spring Data repository**.

- **`domain/`** — framework-agnostic core. `domain/model/` holds immutable Java `record`s (e.g. `Categoria`, accessed as `categoria.id()`/`categoria.nombre()`). `domain/repository/` holds repository **interfaces (ports)** that the domain depends on.
- **`application/`** — `usecase/` business logic organized per aggregate (categoria, estado, genero, institucion, pago, rol, solicitud, trabajo, ubicacion, usuario). Each aggregate typically has a `Gestion*UseCase` (writes/lookups) and a `Listar*UseCase` (reads). `dto/` holds `Request`/`Response` records. Validation lives in use cases, throwing `IllegalArgumentException`.
- **`infraestructure/`** (note the spelling) — outward adapters:
  - `rest/controller/` — thin `@RestController`s under base path `/v1`; delegate directly to use cases. `rest/exception/` has global handlers.
  - `adapter/` — `Jpa*RepositoryAdapter` classes implement the domain repository ports.
  - `jpa/` — `SpringData*Repository` Spring Data interfaces.
  - `persistence/entity/` — JPA `@Entity` classes (separate from domain records).
  - `mapper/` — static mappers converting between domain records, JPA entities, and DTOs.
  - `web/controller/` — Thymeleaf MVC controllers (not REST). `RecuperacionClaveController` serves the password-reset pages under `/auth/password/**`; templates live in `src/main/resources/templates/` (`auth/`, `mail/`, `fragments/`).
  - `adapter/SmtpCorreoAdapter` implements the `CorreoService` port (async SMTP + Thymeleaf email template).
- **`config/`** — Spring config: `SecurityConfig` (Spring Security + JWT), `jwt/` (`JwtAuthFilter`, `JwtProvider`), `CorsConfig`, `SwaggerConfig`, `AdminInitializer`, `properties/AdminProperties`.

Key rule: there is no separate "adapter" indirection between controllers and use cases — controllers call use cases directly. Domain code must not import Spring or JPA types; that separation is enforced by keeping ports in `domain/repository/` and implementations in `infraestructure/adapter/`.

When adding a new aggregate, follow the existing per-aggregate pattern across all layers: domain record + repository port, use cases, DTOs, JPA entity + Spring Data repo + adapter, mapper, and controller.

## Security

Stateless JWT auth via `JwtAuthFilter` (invalid/expired tokens fall through to the 401 entry point). `/auth/**`, Swagger, `/v1/public/**` and `/health/**` are public; `/v1/admin/**` requires `ADMIN`; other `/v1/**` paths require `USER`/`ADMIN` as configured in `SecurityConfig`. Blocked users (`bloqueado=true`) cannot log in (403). Public registration always assigns `USER`.

Password recovery: `GET /auth/password/forgot` → email with a single-use link (only its SHA-256 hash is stored in `tokens_recuperacion`, expires after `PASSWORD_RESET_EXPIRATION` minutes) → `GET/POST /auth/password/reset` → redirect button to `${FRONTEND_URL}/login`. The response never reveals whether an email is registered.

## Admin module

All under `/v1/admin/**`: users (`GET /users` paginated, `PATCH /users/{doc}/block|unblock|role`, `DELETE /users/{doc}` only without history), catalog CRUD (category, state — system states PENDIENTE/ASIGNADO/FINALIZADO/CANCELADO are protected —, gender, payment, roles, institution, rating-category), moderation (`GET /jobs`, `PATCH /jobs/{id}/cancel`, `DELETE /jobs/{id}`, `GET /requests`) and `GET /metrics` for the dashboard. Deleting an in-use catalog returns 409 (`DataIntegrityViolationException`). Requests are in `src/test/http/admin.http`.

## Conventions

- Branches: `main` (stable), `dev` (integration), `feature/*`, `fix/*`.
- Commits follow Conventional Commits (`feat:`, `fix:`, `refactor:`, `test:`, `docs:`).
- PRs target `main` and need at least one approval.
