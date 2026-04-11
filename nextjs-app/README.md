# Next.js Frontend — Items Manager

Frontend web application for the Items Manager, built with Next.js 16, TypeScript, and Tailwind CSS.

## Pages

| Route | Description |
|-------|-------------|
| `/login` | Login with username & password |
| `/register` | Register a new account (choose role) |
| `/` | Items list with search, filter & pagination |
| `/items/new` | Create item (admin only) |
| `/items/[id]/edit` | Edit item (admin only) |

## Running Locally

```bash
npm install
npm run dev
```

Open http://localhost:3000

The app expects the FastAPI backend at `http://localhost:8000` by default. Set `NEXT_PUBLIC_API_URL` to override.

## Running with Docker

From the project root:

```bash
docker compose up --build
```

Frontend will be available at http://localhost:3001

## Key Components

- **AuthContext** — Stores JWT token and user role in localStorage; exposes `isAdmin` flag
- **ProtectedRoute** — Redirects unauthenticated users to `/login`
- **Navbar** — Shows username, role badge (purple for admin, grey for user), and logout button
- **DeleteButton** — Delete with inline confirmation
- **ItemForm** — Shared form used for both create and edit flows
