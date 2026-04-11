# AI Project — FastAPI Items Manager

A full-stack web application for managing items with user authentication and role-based access control.

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend API | FastAPI (Python) |
| Frontend | Next.js 16 + TypeScript + Tailwind CSS |
| Database | PostgreSQL 16 |
| Auth | JWT (JSON Web Tokens) |
| Containerization | Docker + Docker Compose |
| API Testing | Postman |

## Project Structure

```
AI-Project/
├── docker-compose.yml          # Orchestrates all 3 services
├── fastapi-app/                # Backend API
│   ├── main.py                 # API routes & business logic
│   ├── models.py               # SQLAlchemy database models
│   ├── database.py             # PostgreSQL connection setup
│   ├── requirements.txt        # Python dependencies
│   └── Dockerfile
├── nextjs-app/                 # Frontend web app
│   ├── app/                    # Next.js App Router pages
│   │   ├── page.tsx            # Home — items list with search & pagination
│   │   ├── login/page.tsx      # Login page
│   │   ├── register/page.tsx   # Register page
│   │   └── items/
│   │       ├── new/page.tsx    # Create item (admin only)
│   │       └── [id]/edit/      # Edit item (admin only)
│   ├── components/             # Reusable UI components
│   │   ├── Navbar.tsx          # Top navigation with role badge
│   │   ├── DeleteButton.tsx    # Delete with confirmation
│   │   ├── ItemForm.tsx        # Shared create/edit form
│   │   └── ProtectedRoute.tsx  # Auth guard wrapper
│   ├── context/AuthContext.tsx # Global auth state (token, role)
│   ├── lib/api.ts              # API client functions
│   └── Dockerfile
└── postman/
    └── Items_API.postman_collection.json
```

## Features

- **JWT Authentication** — Register, login, and secure all API endpoints with bearer tokens
- **Role-Based Access Control** — Admin users can create, update, and delete items; regular users can only view
- **Search & Filter** — Search items by name/description; filter by price range
- **Pagination** — Browse large item lists with page controls and item count display
- **PostgreSQL** — Persistent relational database with SQLAlchemy ORM
- **Dockerized** — One command to run the full stack locally

## Getting Started

### Option 1 — Docker (Recommended)

Requires [Docker Desktop](https://www.docker.com/products/docker-desktop/) to be running.

```bash
docker compose up --build
```

| Service | URL |
|---------|-----|
| Frontend | http://localhost:3001 |
| Backend API | http://localhost:8000 |
| API Docs (Swagger) | http://localhost:8000/docs |
| PostgreSQL | localhost:5432 |

### Option 2 — Run Locally

**Backend**

```bash
cd fastapi-app
pip install -r requirements.txt
uvicorn main:app --reload --port 8002
```

> Requires PostgreSQL running locally. Set the `DATABASE_URL` environment variable if needed:
> ```bash
> export DATABASE_URL=postgresql://postgres:postgres@localhost:5432/itemsdb
> ```

**Frontend**

```bash
cd nextjs-app
npm install
npm run dev
```

Open http://localhost:3000

## API Endpoints

### Auth

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/auth/register` | Register a new user |
| POST | `/auth/login` | Login and receive JWT token |
| GET | `/auth/me` | Get current user info |

### Items

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| GET | `/items` | All users | List items with search, filter & pagination |
| GET | `/items/{id}` | All users | Get a single item |
| POST | `/items` | Admin only | Create a new item |
| PUT | `/items/{id}` | Admin only | Update an item |
| DELETE | `/items/{id}` | Admin only | Delete an item |

**Query Parameters for GET `/items`:**

| Parameter | Type | Description |
|-----------|------|-------------|
| `search` | string | Search by name or description |
| `min_price` | float | Minimum price filter |
| `max_price` | float | Maximum price filter |
| `page` | int | Page number (default: 1) |
| `page_size` | int | Items per page (default: 5) |

## User Roles

| Role | Can View Items | Can Create/Edit/Delete |
|------|---------------|----------------------|
| `user` | Yes | No |
| `admin` | Yes | Yes |

Set the role during registration. The role is stored in the JWT and enforced on every protected endpoint.

## Postman Collection

Import `postman/Items_API.postman_collection.json` into Postman.

The collection includes 16 requests across 3 folders:
- **Auth** — Register, Login (auto-saves token), Get Me, error cases
- **Items** — Full CRUD, search, filter, pagination, 404 cases
- **Health** — Root endpoint

The Login request automatically captures the JWT token into the `{{token}}` collection variable — all other requests use it automatically.

## Environment Variables

### Backend (`fastapi-app`)

| Variable | Default | Description |
|----------|---------|-------------|
| `DATABASE_URL` | `postgresql://postgres:postgres@localhost:5432/itemsdb` | PostgreSQL connection string |

### Frontend (`nextjs-app`)

| Variable | Default | Description |
|----------|---------|-------------|
| `NEXT_PUBLIC_API_URL` | `http://localhost:8000` | Backend API base URL |

## Database Schema

**users**
| Column | Type | Description |
|--------|------|-------------|
| id | Integer | Primary key |
| username | String | Unique username |
| hashed_password | String | bcrypt hashed password |
| role | String | `admin` or `user` |

**items**
| Column | Type | Description |
|--------|------|-------------|
| id | Integer | Primary key |
| name | String | Item name |
| description | String | Item description |
| price | Float | Item price |
