# Docker Run Guide

## 1) Optional env file
`.env` is optional. If it is absent, defaults from `docker-compose.yml` are used.
If you need custom DB credentials, copy `backend/.env.example` to `.env` and edit values.

## 2) Build and start
```bash
docker compose up --build -d
```

## 3) Quick checks
```bash
docker compose ps
docker compose logs -f backend
docker compose logs -f postgres
curl http://localhost:8080/api/v1/public/knowledge/articles
```

## 4) Stop
```bash
docker compose down
```

## 5) Full reset with DB cleanup
```bash
docker compose down -v
```
