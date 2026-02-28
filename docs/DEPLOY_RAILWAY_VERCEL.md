# Deploy Guide: Railway (Backend) + Vercel (Frontend)

This guide deploys:
- backend (`backend/`) to Railway
- frontend (`frontend/`) to Vercel

## 1. Backend Deployment on Railway

### 1.1 Create services
1. Open Railway and create a new project from this GitHub repository.
2. Create a backend service from source code.
3. Set backend service Root Directory to `backend`.
4. Add a PostgreSQL service in the same Railway project.

## 1.2 Backend environment variables
In backend Railway service, set:

- `SPRING_DATASOURCE_URL`
  - Build it from PostgreSQL reference variables:
  - `jdbc:postgresql://${{Postgres.PGHOST}}:${{Postgres.PGPORT}}/${{Postgres.PGDATABASE}}`
- `SPRING_DATASOURCE_USERNAME`
  - PostgreSQL reference: `PGUSER`
- `SPRING_DATASOURCE_PASSWORD`
  - PostgreSQL reference: `PGPASSWORD`
- `SPRING_JPA_DDL_AUTO=validate`
- `SPRING_FLYWAY_ENABLED=true`
- `APP_CORS_ALLOWED_ORIGIN_PATTERNS=https://<your-vercel-domain>,https://*.vercel.app`

Notes:
- Application already reads Railway `PORT` automatically (`server.port=${PORT:8080}`).
- CORS is configurable via `APP_CORS_ALLOWED_ORIGIN_PATTERNS`.

### 1.3 Expose backend domain
1. In Railway backend service, create a public domain.
2. Save URL, for example:
   - `https://psycho-backend-production.up.railway.app`
3. Health/smoke check:
   - `https://<railway-domain>/swagger-ui.html`

## 2. Frontend Deployment on Vercel

### 2.1 Import project
1. Import the same GitHub repository into Vercel.
2. Set Root Directory to `frontend`.
3. Framework preset: `Vite`.

### 2.2 Build settings
Use:
- Build command: `npm run build`
- Output directory: `dist`

`frontend/vercel.json` is already added to support SPA routing rewrites.

### 2.3 Frontend environment variable
In Vercel project settings, add:
- `VITE_API_BASE_URL=https://<railway-domain>/api/v1`

Then redeploy frontend.

## 3. Connect Frontend Domain in Backend CORS

After Vercel gives the final domain:
1. Update Railway backend variable:
   - `APP_CORS_ALLOWED_ORIGIN_PATTERNS=https://<exact-vercel-domain>,https://*.vercel.app`
2. Redeploy backend service.

## 4. Verification Checklist

1. Open frontend Vercel URL.
2. Register/login with seeded user:
   - `admin@clinic.local / Admin123!`
3. Verify API calls in browser network tab use Railway URL.
4. Verify public encyclopedia loads (`/api/v1/public/knowledge/articles`).
5. Verify role pages:
   - `/admin-app`
   - `/doctor-app`
   - `/client-app`

## 5. Common Issues

### CORS blocked
- Fix `APP_CORS_ALLOWED_ORIGIN_PATTERNS` in Railway.
- Include exact Vercel domain and redeploy backend.

### Frontend still calls localhost
- Check `VITE_API_BASE_URL` in Vercel project env vars.
- Trigger redeploy after env update.

### Backend fails DB connection
- Recheck `SPRING_DATASOURCE_URL`, username, password reference variables from PostgreSQL service.

## 6. Useful Links

- Railway service variables: https://docs.railway.com/guides/variables
- Railway service domains: https://docs.railway.com/guides/public-networking
- Railway service source/root settings: https://docs.railway.com/reference/services
- Railway configuration as code: https://docs.railway.com/guides/config-as-code
- Vercel Vite deployment: https://vercel.com/docs/frameworks/frontend/vite
- Vercel project root directory: https://vercel.com/docs/deployments/configure-a-build
- Vercel rewrites: https://vercel.com/docs/rewrites
