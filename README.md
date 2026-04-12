# AI Projects

A collection of full-stack and API projects built with modern technologies.

## Projects

### 1. FastAPI Items Manager
**Path:** [`fastapi-items-manager/`](./fastapi-items-manager/)

A full-stack web application for managing items with JWT authentication and role-based access control.

| Layer | Technology |
|-------|-----------|
| Backend | FastAPI (Python) |
| Frontend | Next.js 16 + TypeScript + Tailwind CSS |
| Database | PostgreSQL 16 |
| Auth | JWT |

```bash
cd fastapi-items-manager
docker compose up --build
```

| Service | URL |
|---------|-----|
| Frontend | http://localhost:3001 |
| API | http://localhost:8000 |
| API Docs | http://localhost:8000/docs |

---

### 2. Cargo ONE Record API
**Path:** [`cargo-one-record/`](./cargo-one-record/)

Airline Cargo Management System built on the **IATA ONE Record standard (v2.0.0)** — fully self-contained, no external ONE Record server dependency.

| Layer | Technology |
|-------|-----------|
| Framework | Spring Boot 3.2 |
| Language | Java 17 |
| Database | PostgreSQL 16 |
| Standard | IATA ONE Record v2.0 |

```bash
cd cargo-one-record
docker compose up --build
```

| Service | URL |
|---------|-----|
| API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| PostgreSQL | localhost:5433 |