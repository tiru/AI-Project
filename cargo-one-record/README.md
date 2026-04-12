# Cargo ONE Record API

Airline Cargo Management System built on the **IATA ONE Record standard (v2.0.0)** using Java + Spring Boot 3.

> No dependency on NE:ONE server or any external ONE Record server. This is a fully self-contained implementation.

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Framework | Spring Boot 3.2 |
| Language | Java 17 |
| Database | PostgreSQL 16 |
| ORM | Spring Data JPA + Hibernate |
| Security | Spring Security + JWT |
| API Docs | Swagger UI (SpringDoc OpenAPI 3) |
| Build | Maven |
| Container | Docker + Docker Compose |

## ONE Record Data Model Implemented

| Entity | ONE Record Type | Description |
|--------|----------------|-------------|
| Shipment | `cargo:Shipment` | Central cargo consignment |
| Piece | `cargo:Piece` | Individual package/unit |
| Waybill | `cargo:Waybill` | Air Waybill (MAWB/HAWB) |
| Company | `cargo:Company` | Airline, forwarder, shipper, consignee |
| Person | `cargo:Person` | Contact person within a company |
| TransportMeans | `cargo:TransportMeans` | Aircraft or vehicle |
| TransportSegment | `cargo:TransportSegment` | Individual flight leg |
| Booking | `cargo:Booking` | Confirmed cargo booking |
| BookingRequest | `cargo:BookingRequest` | Booking request to carrier |
| LogisticsEvent | `cargo:LogisticsEvent` | Status event (RCS, DEP, ARR, DLV...) |
| ChangeRequest | `api:ChangeRequest` | ONE Record update workflow |
| AuditTrail | `api:AuditTrail` | Immutable version history |

## Quick Start

### Docker (Recommended)

```bash
docker compose up --build
```

| Service | URL |
|---------|-----|
| API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| API Docs | http://localhost:8080/api-docs |
| PostgreSQL | localhost:5433 |

### Run Locally

```bash
# Start PostgreSQL first, then:
mvn spring-boot:run
```

## API Endpoints

### Auth
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/auth/register` | Register user (ADMIN/OPERATOR/VIEWER) |
| POST | `/auth/login` | Login → JWT token |

### ONE Record Core
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Server information |
| POST/GET/PUT/DELETE | `/logistics-objects/shipments` | Shipments CRUD |
| POST/GET/PUT/DELETE | `/logistics-objects/pieces` | Pieces CRUD |
| GET | `/logistics-objects/pieces/shipment/{id}` | Pieces by shipment |
| POST/GET/PUT/DELETE | `/logistics-objects/waybills` | Waybills CRUD |
| GET | `/logistics-objects/waybills/number/{awb}` | Lookup by AWB number |

### Parties
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST/GET/PUT/DELETE | `/logistics-objects/companies` | Companies CRUD |
| GET | `/logistics-objects/companies/iata/{code}` | Lookup airline by IATA code |
| POST/GET | `/logistics-objects/companies/{id}/persons` | Contact persons |

### Transport & Booking
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST/GET/PUT | `/logistics-objects/transport-means` | Aircraft/vehicles |
| POST/GET | `/logistics-objects/transport-segments` | Flight legs |
| PATCH | `/logistics-objects/transport-segments/{id}/status` | Update segment status |
| POST/GET | `/logistics-objects/booking-requests` | Booking requests |
| POST/GET | `/logistics-objects/bookings` | Confirmed bookings |
| PATCH | `/logistics-objects/bookings/{id}/cancel` | Cancel booking |

### ONE Record Standard Endpoints
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/logistics-objects/{id}/logistics-events` | Add status event |
| GET | `/logistics-objects/{id}/logistics-events` | Get all events |
| GET | `/logistics-objects/{id}/latest-event` | Current status |
| GET | `/logistics-objects/{id}/audit-trail` | Version history |
| POST | `/action-requests/change-requests` | Submit change request |
| PATCH | `/action-requests/change-requests/{id}/review` | Approve/Reject |

## IATA Event Codes

| Code | Description |
|------|-------------|
| RCS | Received from Shipper |
| MAN | Manifested |
| PRE | Prepared for Loading |
| DEP | Departed |
| ARR | Arrived |
| RCF | Received from Flight |
| AWR | Arrived at Warehouse |
| NFD | Notified |
| DLV | Delivered to Consignee |
| TFD | Transferred |
| FOH | Freight on Hand |
| CCD | Customs Cleared |

## User Roles

| Role | Permissions |
|------|------------|
| ADMIN | Full access — create, read, update, delete, approve change requests |
| OPERATOR | Create and update logistics objects and events |
| VIEWER | Read-only access |

## Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `DATABASE_URL` | `jdbc:postgresql://localhost:5432/cargoonerecord` | PostgreSQL URL |
| `DB_USERNAME` | `postgres` | Database username |
| `DB_PASSWORD` | `postgres` | Database password |
| `JWT_SECRET` | (built-in) | JWT signing secret — change in production |
| `COMPANY_IDENTIFIER` | `https://cargo.example.com` | Your ONE Record server URI |