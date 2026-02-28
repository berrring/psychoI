# Psycho Monorepo

Monorepo with separate backend and frontend workspaces.

## Structure
- `backend/` - Spring Boot API and database migrations
- `frontend/` - frontend workspace (React app can be added here)
- `docker-compose.yml` - local environment (backend + postgres)

## Backend Commands
```bash
cd backend
./mvnw test
./mvnw spring-boot:run
```

## Docker
```bash
docker compose up --build -d
docker compose logs -f backend
docker compose down
```
