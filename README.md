# Psycho Clinic Platform

Full-stack monorepo for a modern clinic platform:
- `backend/`: Spring Boot REST API (`/api/v1`) with JWT auth, role-based access, Flyway, PostgreSQL/H2
- `frontend/`: React + TypeScript app with role-specific workspaces

## Main Capabilities
- Clinic, department and medical service management
- Appointment lifecycle and timeline events
- Audit history by entity and actor
- Public medical encyclopedia
- Admin/doctor knowledge management
- Public clinic news (`NEWS` category)
- Role-based frontend applications:
  - Client App (`PATIENT`, `CLIENT`)
  - Doctor App (`DOCTOR`, `PSYCHOLOGIST`)
  - Admin App (`ADMIN`)

## Project Structure
```text
.
|- backend/
|- frontend/
|- docs/
`- docker-compose.yml
```

## Prerequisites
- Java 21 (or Java 17+)
- Node.js 20+
- Docker Desktop (optional)

## Quick Start
### 1) Backend + DB with Docker
```bash
docker compose up --build -d
docker compose ps
docker compose logs -f backend
```

### 2) Frontend local run
```bash
cd frontend
cp .env.example .env
npm install
npm run dev
```

PowerShell alternative:
```powershell
npm.cmd install
npm.cmd run dev
```

Frontend: `http://localhost:5173`
Backend: `http://localhost:8080`

## Backend Local Run (without Docker)
```bash
cd backend
./mvnw spring-boot:run
```

PowerShell with your Java path:
```powershell
$env:JAVA_HOME='C:\Users\user\.jdks\ms-21.0.8'
$env:PATH="$env:JAVA_HOME\bin;$env:PATH"
java -version
.\mvnw.cmd spring-boot:run
```

## Demo Accounts
| Role | Email | Password |
|---|---|---|
| ADMIN | `admin@clinic.local` | `Admin123!` |
| RECEPTIONIST | `reception@clinic.local` | `Reception123!` |
| DOCTOR | `doc.alex@clinic.local` | `Doctor123!` |
| PATIENT | `patient.demo@clinic.local` | `Patient123!` |
| CLIENT | `client.demo@clinic.local` | `Client123!` |

## API and Docs
- API map: [`docs/API_V1.md`](./docs/API_V1.md)
- Docker notes: [`docs/DOCKER.md`](./docs/DOCKER.md)
- Swagger UI: `http://localhost:8080/swagger-ui.html`

## Frontend Role Routes
- `/client-app`
- `/doctor-app`
- `/admin-app`
- `/workspace` (auto-redirect by role)

## Useful Commands
```bash
# root
docker compose up --build -d
docker compose down

# frontend
cd frontend
npm run dev
npm run build

# backend
cd backend
./mvnw test
./mvnw spring-boot:run
```
