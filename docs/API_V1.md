# Clinic API v1

Base URL: `/api/v1`

## Auth
- `POST /auth/register`
- `POST /auth/login`

## Users
- `GET /users/{id}`
- `GET /users/doctors?query=&page=&size=`
- `GET /users/patients?page=&size=`
- `PATCH /users/{id}`
- `PATCH /users/{id}/change-password`

## Public Doctors
- `GET /public/doctors?query=&page=&size=`
- `GET /public/doctors/{id}`

## Clinics
- `GET /clinics`
- `GET /clinics/{id}`
- `POST /clinics`
- `PATCH /clinics/{id}`
- `GET /clinics/{clinicId}/departments`
- `POST /clinics/{clinicId}/departments`
- `GET /clinics/departments/{departmentId}/services`
- `POST /clinics/departments/{departmentId}/services`

## Appointments
- `POST /appointments`
- `GET /appointments/{id}`
- `PATCH /appointments/{id}`
- `PATCH /appointments/{id}/status`
- `GET /appointments?patientId=&page=&size=`
- `GET /appointments?doctorId=&page=&size=`
- `GET /appointments/calendar/doctors/{doctorId}?from=YYYY-MM-DD&to=YYYY-MM-DD`
- `GET /appointments/clinics/{clinicId}` (ADMIN/RECEPTIONIST)

## Appointment Events (Timeline)
- `POST /appointments/{appointmentId}/events`
- `GET /appointments/{appointmentId}/events?page=&size=`
- `GET /appointments/{appointmentId}/events/timeline`
- `GET /events/{id}`

## Audit
- `GET /audit/events/entity/{entityName}/{entityId}?page=&size=`
- `GET /audit/actors/{actorId}/events?page=&size=`

## Dashboard
- `GET /dashboard/summary`

## Knowledge / Encyclopedia
Public:
- `GET /public/knowledge/articles?query=&category=&page=&size=`
- `GET /public/knowledge/articles/{slug}`

Staff:
- `GET /knowledge/articles?page=&size=`
- `POST /knowledge/articles`
- `PATCH /knowledge/articles/{id}`
