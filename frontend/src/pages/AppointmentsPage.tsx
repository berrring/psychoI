import { FormEvent, useEffect, useMemo, useState } from "react";
import { Link, useLocation } from "react-router-dom";
import { apiRequest, toErrorMessage } from "../api";
import { useAuth } from "../auth";
import type {
  Appointment,
  AppointmentStatus,
  Clinic,
  Department,
  MedicalService,
  PageResponse,
  PublicDoctorSummary,
  UserInfo
} from "../types";

const STATUS_OPTIONS: AppointmentStatus[] = [
  "CREATED",
  "BOOKED",
  "CONFIRMED",
  "IN_PROGRESS",
  "COMPLETED",
  "CANCELLED",
  "NO_SHOW"
];

function withSeconds(value: string) {
  if (!value) return value;
  return value.length === 16 ? `${value}:00` : value;
}

export function AppointmentsPage() {
  const { token, session, hasRole } = useAuth();
  const location = useLocation();

  const [doctors, setDoctors] = useState<UserInfo[]>([]);
  const [patients, setPatients] = useState<UserInfo[]>([]);
  const [clinics, setClinics] = useState<Clinic[]>([]);
  const [departments, setDepartments] = useState<Department[]>([]);
  const [services, setServices] = useState<MedicalService[]>([]);

  const [appointments, setAppointments] = useState<PageResponse<Appointment> | null>(null);
  const [filterBy, setFilterBy] = useState<"patientId" | "doctorId">(
    hasRole("PATIENT", "CLIENT") ? "patientId" : "doctorId"
  );
  const [filterId, setFilterId] = useState<number>(session?.userId ?? 0);
  const [statusReason, setStatusReason] = useState("");
  const [pendingStatusId, setPendingStatusId] = useState<number | null>(null);

  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const [form, setForm] = useState({
    patientId: session?.userId ?? 0,
    doctorId: 0,
    clinicId: 0,
    departmentId: 0,
    medicalServiceId: 0,
    time: "",
    durationMinutes: 30,
    complaint: "",
    notes: ""
  });

  const canSelectAnyPatient = useMemo(
    () => !hasRole("PATIENT", "CLIENT"),
    [hasRole]
  );
  const canManageStatuses = useMemo(
    () => hasRole("ADMIN", "RECEPTIONIST", "DOCTOR", "PSYCHOLOGIST"),
    [hasRole]
  );
  const isPatientLike = useMemo(() => hasRole("PATIENT", "CLIENT"), [hasRole]);

  useEffect(() => {
    if (!session) return;
    if (hasRole("PATIENT", "CLIENT")) {
      setFilterBy("patientId");
      setFilterId(session.userId);
    }
  }, [session?.userId, hasRole]);

  async function loadReferenceData() {
    if (!token) return;
    if (isPatientLike) {
      const doctorResp = await apiRequest<PageResponse<PublicDoctorSummary>>(
        "/public/doctors",
        { method: "GET" },
        undefined,
        { page: 0, size: 100 }
      );
      setDoctors(
        doctorResp.content.map((doctor) => ({
          id: doctor.id,
          name: doctor.fullName,
          email: "",
          role: "DOCTOR",
          specialization: doctor.specialization,
          yearsOfExperience: doctor.experienceYears,
          clinicName: doctor.clinic,
          active: true
        }))
      );
    } else {
      const doctorResp = await apiRequest<PageResponse<UserInfo>>(
        "/users/doctors",
        { method: "GET" },
        token,
        { page: 0, size: 100 }
      );
      setDoctors(doctorResp.content);
    }

    const clinicResp = await apiRequest<Clinic[]>("/clinics", { method: "GET" }, token);
    setClinics(clinicResp);

    if (canSelectAnyPatient) {
      try {
        const patientResp = await apiRequest<PageResponse<UserInfo>>(
          "/users/patients",
          { method: "GET" },
          token,
          { page: 0, size: 200 }
        );
        setPatients(patientResp.content);
      } catch {
        setPatients([]);
      }
    }
  }

  useEffect(() => {
    const state = location.state as { selectedDoctorId?: number } | null;
    if (!state?.selectedDoctorId) return;
    setForm((current) => ({ ...current, doctorId: state.selectedDoctorId ?? current.doctorId }));
  }, [location.state]);

  useEffect(() => {
    let cancelled = false;
    async function init() {
      if (!token) return;
      setLoading(true);
      setError(null);
      try {
        await loadReferenceData();
      } catch (e) {
        if (!cancelled) setError(toErrorMessage(e));
      } finally {
        if (!cancelled) setLoading(false);
      }
    }
    init();
    return () => {
      cancelled = true;
    };
  }, [token, canSelectAnyPatient, isPatientLike]);

  useEffect(() => {
    let cancelled = false;
    async function loadDepartments() {
      if (!token || !form.clinicId) {
        setDepartments([]);
        setServices([]);
        return;
      }
      try {
        const data = await apiRequest<Department[]>(
          `/clinics/${form.clinicId}/departments`,
          { method: "GET" },
          token
        );
        if (!cancelled) {
          setDepartments(data);
          const nextDepartment = data[0]?.id ?? 0;
          setForm((current) => ({
            ...current,
            departmentId: current.departmentId || nextDepartment
          }));
        }
      } catch (e) {
        if (!cancelled) setError(toErrorMessage(e));
      }
    }
    loadDepartments();
    return () => {
      cancelled = true;
    };
  }, [token, form.clinicId]);

  useEffect(() => {
    let cancelled = false;
    async function loadServices() {
      if (!token || !form.departmentId) {
        setServices([]);
        return;
      }
      try {
        const data = await apiRequest<MedicalService[]>(
          `/clinics/departments/${form.departmentId}/services`,
          { method: "GET" },
          token
        );
        if (!cancelled) {
          setServices(data);
          const nextService = data[0]?.id ?? 0;
          setForm((current) => ({
            ...current,
            medicalServiceId: current.medicalServiceId || nextService
          }));
        }
      } catch (e) {
        if (!cancelled) setError(toErrorMessage(e));
      }
    }
    loadServices();
    return () => {
      cancelled = true;
    };
  }, [token, form.departmentId]);

  async function loadAppointments() {
    if (!token || !filterId) return;
    setError(null);
    setLoading(true);
    try {
      const data = await apiRequest<PageResponse<Appointment>>(
        "/appointments",
        { method: "GET" },
        token,
        { [filterBy]: filterId, page: 0, size: 50 }
      );
      setAppointments(data);
    } catch (e) {
      setError(toErrorMessage(e));
    } finally {
      setLoading(false);
    }
  }

  async function onCreate(event: FormEvent) {
    event.preventDefault();
    if (!token) return;
    setError(null);
    try {
      const payload = {
        patientId: canSelectAnyPatient ? form.patientId : session?.userId,
        doctorId: form.doctorId,
        clinicId: form.clinicId,
        departmentId: form.departmentId || null,
        medicalServiceId: form.medicalServiceId || null,
        time: withSeconds(form.time),
        durationMinutes: form.durationMinutes,
        complaint: form.complaint || null,
        notes: form.notes || null
      };
      await apiRequest<Appointment>(
        "/appointments",
        { method: "POST", body: JSON.stringify(payload) },
        token
      );
      await loadAppointments();
    } catch (e) {
      setError(toErrorMessage(e));
    }
  }

  async function onStatusChange(id: number, status: AppointmentStatus) {
    if (!token || !canManageStatuses) return;
    setPendingStatusId(id);
    setError(null);
    try {
      await apiRequest<Appointment>(
        `/appointments/${id}/status`,
        {
          method: "PATCH",
          body: JSON.stringify({
            status,
            reason: statusReason || null
          })
        },
        token
      );
      setStatusReason("");
      await loadAppointments();
    } catch (e) {
      setError(toErrorMessage(e));
    } finally {
      setPendingStatusId(null);
    }
  }

  return (
    <section className="panel">
      <h2>Appointments</h2>
      <p className="muted">Endpoints: <code>/api/v1/appointments</code> and <code>/api/v1/appointments/:id/status</code></p>
      {!canManageStatuses && (
        <p className="muted">Status updates are available for staff roles only.</p>
      )}

      {error && <p className="error">{error}</p>}
      {loading && <p className="muted">Loading...</p>}

      <div className="split-grid">
        <div className="panel-sub">
          <h3>Create Appointment</h3>
          <form onSubmit={onCreate} className="form-grid">
            {canSelectAnyPatient ? (
              <label>
                Patient
                <select
                  value={form.patientId || ""}
                  onChange={(e) => setForm((v) => ({ ...v, patientId: Number(e.target.value) }))}
                  required
                >
                  <option value="">Select patient</option>
                  {patients.map((p) => (
                    <option key={p.id} value={p.id}>
                      {p.name} ({p.email})
                    </option>
                  ))}
                </select>
              </label>
            ) : (
              <p className="muted">Patient ID is fixed to your account: {session?.userId}</p>
            )}

            <label>
              Doctor
              <select
                value={form.doctorId || ""}
                onChange={(e) => setForm((v) => ({ ...v, doctorId: Number(e.target.value) }))}
                required
              >
                <option value="">Select doctor</option>
                {doctors.map((d) => (
                  <option key={d.id} value={d.id}>
                    {d.name} ({d.specialization || "Doctor"})
                  </option>
                ))}
              </select>
            </label>
            {isPatientLike && (
              <Link to="/doctors?returnTo=/appointments">Open doctor details before selection</Link>
            )}

            <label>
              Clinic
              <select
                value={form.clinicId || ""}
                onChange={(e) => setForm((v) => ({ ...v, clinicId: Number(e.target.value) }))}
                required
              >
                <option value="">Select clinic</option>
                {clinics.map((clinic) => (
                  <option key={clinic.id} value={clinic.id}>
                    {clinic.name}
                  </option>
                ))}
              </select>
            </label>

            <label>
              Department
              <select
                value={form.departmentId || ""}
                onChange={(e) => setForm((v) => ({ ...v, departmentId: Number(e.target.value) }))}
              >
                <option value="">Not selected</option>
                {departments.map((dep) => (
                  <option key={dep.id} value={dep.id}>
                    {dep.name}
                  </option>
                ))}
              </select>
            </label>

            <label>
              Service
              <select
                value={form.medicalServiceId || ""}
                onChange={(e) => setForm((v) => ({ ...v, medicalServiceId: Number(e.target.value) }))}
              >
                <option value="">Not selected</option>
                {services.map((service) => (
                  <option key={service.id} value={service.id}>
                    {service.name}
                  </option>
                ))}
              </select>
            </label>

            <label>
              Date and Time
              <input
                type="datetime-local"
                value={form.time}
                onChange={(e) => setForm((v) => ({ ...v, time: e.target.value }))}
                required
              />
            </label>

            <label>
              Duration Minutes
              <input
                type="number"
                min={5}
                value={form.durationMinutes}
                onChange={(e) => setForm((v) => ({ ...v, durationMinutes: Number(e.target.value) }))}
                required
              />
            </label>

            <textarea
              placeholder="Complaint"
              value={form.complaint}
              onChange={(e) => setForm((v) => ({ ...v, complaint: e.target.value }))}
            />
            <textarea
              placeholder="Notes"
              value={form.notes}
              onChange={(e) => setForm((v) => ({ ...v, notes: e.target.value }))}
            />
            <button type="submit">Create Appointment</button>
          </form>
        </div>

        <div className="panel-sub">
          <h3>Find Appointments</h3>
          <div className="row-form">
            {canSelectAnyPatient ? (
              <>
                <select value={filterBy} onChange={(e) => setFilterBy(e.target.value as "patientId" | "doctorId")}>
                  <option value="patientId">By patientId</option>
                  <option value="doctorId">By doctorId</option>
                </select>
                <input
                  type="number"
                  value={filterId || ""}
                  onChange={(e) => setFilterId(Number(e.target.value))}
                  placeholder="User ID"
                />
              </>
            ) : (
              <p className="muted">Showing appointments for your patient account (ID: {session?.userId}).</p>
            )}
            <button type="button" onClick={loadAppointments}>
              Load
            </button>
          </div>

          {canManageStatuses && (
            <label>
              Status change reason
              <input
                value={statusReason}
                onChange={(e) => setStatusReason(e.target.value)}
                placeholder="Optional reason for cancellation/status update"
              />
            </label>
          )}

          <ul className="list">
            {appointments?.content.map((appointment) => (
              <li key={appointment.id}>
                <div>
                  <strong>
                    #{appointment.id} {appointment.patientName} to {appointment.doctorName}
                  </strong>
                  <p className="muted">
                    {appointment.time} | {appointment.clinicName} | {appointment.status}
                  </p>
                </div>
                <div className="inline-actions">
                  {canManageStatuses ? (
                    STATUS_OPTIONS.map((status) => (
                      <button
                        key={status}
                        type="button"
                        className={status === appointment.status ? "ghost-btn ghost-btn-active" : "ghost-btn"}
                        onClick={() => onStatusChange(appointment.id, status)}
                        disabled={pendingStatusId === appointment.id}
                      >
                        {status}
                      </button>
                    ))
                  ) : (
                    <span className={`status-chip status-chip-${appointment.status.toLowerCase()}`}>{appointment.status}</span>
                  )}
                </div>
              </li>
            ))}
          </ul>
        </div>
      </div>
    </section>
  );
}
