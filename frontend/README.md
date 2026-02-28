# Frontend

React + TypeScript client for the clinic backend (`/api/v1`).

## Features
- Corporate healthcare landing page
- Public encyclopedia + public news feed
- Role-specific workspaces:
  - Client App (`/client-app`)
  - Doctor App (`/doctor-app`)
  - Admin App (`/admin-app`)
- Auto role redirect: `/workspace`

## Setup
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

## Build
```bash
npm run build
```

## Environment
```bash
VITE_API_BASE_URL=http://localhost:8080/api/v1
```
