# Psycho Clinic Platform

Production-style clinic platform monorepo with a Spring Boot backend and React frontend.

## 1. Repository Layout

```text
.
|- backend/            # Spring Boot API (Java)
|- frontend/           # React + TypeScript UI
|- docs/               # API and Docker notes
`- docker-compose.yml  # PostgreSQL + backend stack
```

## 2. Backend Overview

Backend is a role-based API for clinic operations with unified domain model:
- authentication and JWT sessions
- clinic structure (clinics, departments, services)
- users (admin, doctor, psychologist, receptionist, patient, client)
- appointments and status lifecycle
- appointment event timeline
- audit history
- medical encyclopedia and clinic news

Base path: `http://localhost:8080/api/v1`

## 3. Tech Stack

### Backend
- Java 21 compatible (Java 17+ also works)
- Spring Boot 4.0.0
- Spring Security + JWT
- Spring Data JPA
- Flyway migrations
- H2 (default local) or PostgreSQL (docker/prod)

### Frontend
- React 19
- TypeScript
- Vite
- React Router

### Infra
- Docker Compose

## 4. Domain and Database

Main entities:
- `clinics`
- `departments`
- `clinic_services`
- `users`
- `appointments`
- `appointment_events`
- `audit_events`
- `knowledge_articles`

Migration file:
- `backend/src/main/resources/db/migration/V1__init_clinic_schema.sql`

### Knowledge Categories
Supported categories now include:
- `PREVENTION`
- `DISEASES`
- `DIAGNOSTICS`
- `TREATMENT`
- `REHABILITATION`
- `NUTRITION`
- `MENTAL_HEALTH`
- `FAQ`
- `NEWS`

## 5. Roles and Access Model

Roles:
- `ADMIN`
- `DOCTOR`
- `PSYCHOLOGIST`
- `RECEPTIONIST`
- `PATIENT`
- `CLIENT`

Examples:
- doctors/psychologists can manage appointments and clinical content
- admin has broad operational access (dashboard, audit, content, users)
- patient/client can browse public info and book/view own appointments

## 6. Seeded Special Users (Detailed)

`DataInitializer` now seeds full profiles (phone, specialization, license, experience, bio, clinic):

| Role | Email | Password | Clinic | Phone | Specialization | License |
|---|---|---|---|---|---|---|
| ADMIN | `admin@clinic.local` | `Admin123!` | NorthCare Central Clinic | `+1-212-555-1001` | Healthcare platform operations | - |
| RECEPTIONIST | `reception@clinic.local` | `Reception123!` | NorthCare Central Clinic | `+1-212-555-1002` | Patient coordination | - |
| DOCTOR | `doc.alex@clinic.local` | `Doctor123!` | NorthCare Central Clinic | `+1-212-555-1101` | Internal medicine | `NY-IM-44718` |
| DOCTOR | `doc.sara@clinic.local` | `Doctor123!` | NorthCare Central Clinic | `+1-212-555-1102` | Cardiology | `NY-CARD-33892` |
| DOCTOR | `doc.mike@clinic.local` | `Doctor123!` | NorthCare Riverside Clinic | `+1-347-555-1103` | Diagnostics | `NY-DIAG-55217` |
| DOCTOR | `doc.emma@clinic.local` | `Doctor123!` | NorthCare Riverside Clinic | `+1-347-555-1104` | Radiology | `NY-RAD-88124` |
| PSYCHOLOGIST | `psy.julia@clinic.local` | `Doctor123!` | NorthCare Central Clinic | `+1-212-555-1201` | Clinical psychology | `NY-PSY-22460` |
| PATIENT | `patient.demo@clinic.local` | `Patient123!` | NorthCare Central Clinic | `+1-917-555-2001` | - | - |
| CLIENT | `client.demo@clinic.local` | `Client123!` | NorthCare Central Clinic | `+1-917-555-2002` | - | - |
| PATIENT | `patient.olivia@clinic.local` | `Patient123!` | NorthCare Riverside Clinic | `+1-917-555-2003` | - | - |

Seed behavior:
- users are upserted by email
- profile fields are refreshed on startup
- existing password is preserved unless missing

## 7. Seeded Clinic Data

### Clinics
- NorthCare Central Clinic (New York)
- NorthCare Riverside Clinic (Brooklyn)

### Departments
- Therapy
- Diagnostics
- Mental Health
- Rehabilitation
- Cardiology
- Imaging

### Services (examples)
- Therapist consultation
- Endocrinology check-up
- Extended blood panel
- MRI diagnostics
- Stress ECG
- Psychological intake session

### Knowledge Base
Seed includes:
- medical encyclopedia articles
- FAQ entries
- clinic `NEWS` entries

## 8. API Surface (High-Level)

### Auth
- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`

### Public Content
- `GET /api/v1/public/knowledge/articles`
- `GET /api/v1/public/knowledge/articles/{slug}`

### Users
- `GET /api/v1/users/{id}`
- `GET /api/v1/users/doctors`
- `GET /api/v1/users/patients`
- `PATCH /api/v1/users/{id}`
- `PATCH /api/v1/users/{id}/change-password`

### Clinics
- `GET /api/v1/clinics`
- `GET /api/v1/clinics/{id}`
- `POST /api/v1/clinics`
- `PATCH /api/v1/clinics/{id}`
- `GET /api/v1/clinics/{clinicId}/departments`
- `POST /api/v1/clinics/{clinicId}/departments`
- `GET /api/v1/clinics/departments/{departmentId}/services`
- `POST /api/v1/clinics/departments/{departmentId}/services`

### Appointments
- `POST /api/v1/appointments`
- `GET /api/v1/appointments/{id}`
- `PATCH /api/v1/appointments/{id}`
- `PATCH /api/v1/appointments/{id}/status`
- `GET /api/v1/appointments?patientId=...`
- `GET /api/v1/appointments?doctorId=...`
- `GET /api/v1/appointments/calendar/doctors/{doctorId}`

### Events and Audit
- `POST /api/v1/appointments/{appointmentId}/events`
- `GET /api/v1/appointments/{appointmentId}/events`
- `GET /api/v1/appointments/{appointmentId}/events/timeline`
- `GET /api/v1/audit/events/entity/{entityName}/{entityId}`
- `GET /api/v1/audit/actors/{actorId}/events`

### Dashboard and Knowledge Management
- `GET /api/v1/dashboard/summary`
- `GET /api/v1/knowledge/articles`
- `POST /api/v1/knowledge/articles`
- `PATCH /api/v1/knowledge/articles/{id}`

## 9. Running the Project

### Option A: Docker (recommended)
From repository root:

```bash
docker compose up --build -d
docker compose ps
docker compose logs -f backend
```

Stops containers:

```bash
docker compose down
```

## 10. Running Backend Locally

```bash
cd backend
./mvnw spring-boot:run
```

PowerShell (with your Java path):

```powershell
$env:JAVA_HOME='C:\Users\user\.jdks\ms-21.0.8'
$env:PATH="$env:JAVA_HOME\bin;$env:PATH"
java -version
.\mvnw.cmd spring-boot:run
```

Compile only:

```powershell
.\mvnw.cmd -DskipTests compile
```

## 11. Running Frontend Locally

```bash
cd frontend
cp .env.example .env
npm install
npm run dev
```

PowerShell:

```powershell
npm.cmd install
npm.cmd run dev
```

Frontend URL: `http://localhost:5173`

## 12. Environment Variables

### Backend
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `SPRING_JPA_DDL_AUTO`
- `SPRING_FLYWAY_ENABLED`

### Frontend
- `VITE_API_BASE_URL` (default: `http://localhost:8080/api/v1`)

## 13. Swagger and Docs

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- API map: `docs/API_V1.md`
- Docker notes: `docs/DOCKER.md`

## 14. Quality and Validation

Recommended checks:

```bash
# backend
cd backend
./mvnw test

# frontend
cd frontend
npm run build
```

## 15. Notes for Integrating Your Own Frontend

Backend is API-first and already ready for separate React/Next/mobile clients:
- stable `/api/v1` route prefix
- DTO-based response contracts
- role-aware access policies
- seeded demo accounts for each major flow
- public endpoints for encyclopedia and news

If you connect another frontend, keep JWT in auth header:
`Authorization: Bearer <token>`

## 16. Production Deploy (Railway + Vercel)

Recommended production setup:
- Backend + PostgreSQL on Railway
- Frontend on Vercel

Full step-by-step guide:
- `docs/DEPLOY_RAILWAY_VERCEL.md`
