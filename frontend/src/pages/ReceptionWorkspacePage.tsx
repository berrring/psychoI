import { useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import { apiRequest, toErrorMessage } from "../api";
import { useAuth } from "../auth";
import type { Appointment, Clinic, DashboardSummary } from "../types";

function formatDateTime(value: string) {
  return new Date(value).toLocaleString();
}

export function ReceptionWorkspacePage() {
  const { token } = useAuth();
  const [summary, setSummary] = useState<DashboardSummary | null>(null);
  const [clinics, setClinics] = useState<Clinic[]>([]);
  const [selectedClinicId, setSelectedClinicId] = useState<number>(0);
  const [appointments, setAppointments] = useState<Appointment[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function loadInitial() {
    if (!token) return;
    const [summaryData, clinicData] = await Promise.all([
      apiRequest<DashboardSummary>("/dashboard/summary", { method: "GET" }, token),
      apiRequest<Clinic[]>("/clinics", { method: "GET" }, token)
    ]);
    setSummary(summaryData);
    setClinics(clinicData);
    setSelectedClinicId((current) => current || clinicData[0]?.id || 0);
  }

  async function loadClinicSchedule(clinicId: number) {
    if (!token || !clinicId) return;
    const data = await apiRequest<Appointment[]>(
      `/appointments/clinics/${clinicId}`,
      { method: "GET" },
      token
    );
    setAppointments(data);
  }

  useEffect(() => {
    let cancelled = false;
    async function init() {
      if (!token) return;
      setLoading(true);
      setError(null);
      try {
        await loadInitial();
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
  }, [token]);

  useEffect(() => {
    let cancelled = false;
    async function load() {
      if (!selectedClinicId) {
        setAppointments([]);
        return;
      }
      try {
        await loadClinicSchedule(selectedClinicId);
      } catch (e) {
        if (!cancelled) setError(toErrorMessage(e));
      }
    }
    load();
    return () => {
      cancelled = true;
    };
  }, [selectedClinicId, token]);

  const upcoming = useMemo(
    () =>
      appointments
        .filter((item) => new Date(item.time).getTime() >= Date.now())
        .slice(0, 8),
    [appointments]
  );

  return (
    <section className="workspace">
      <div className="workspace-hero workspace-hero-reception">
        <p className="eyebrow">Reception Workspace</p>
        <h2>Patient flow coordination</h2>
        <p>Manage schedules, bookings and confirmations for your clinic front desk.</p>
      </div>

      {loading && <p className="muted">Loading reception workspace...</p>}
      {error && <p className="error">{error}</p>}

      {summary && (
        <div className="kpi-grid">
          <article className="kpi-card">
            <span>Clinics</span>
            <strong>{summary.clinics}</strong>
          </article>
          <article className="kpi-card">
            <span>Doctors</span>
            <strong>{summary.doctors}</strong>
          </article>
          <article className="kpi-card">
            <span>Patients</span>
            <strong>{summary.patients}</strong>
          </article>
          <article className="kpi-card">
            <span>Appointments</span>
            <strong>{summary.appointments}</strong>
          </article>
        </div>
      )}

      <div className="workspace-grid">
        <article className="panel workspace-panel">
          <h3>Reception Tools</h3>
          <ul className="list">
            <li>
              <strong>Booking console</strong>
              <p className="muted">Create, reschedule and cancel patient appointments.</p>
              <Link to="/appointments">Open appointments</Link>
            </li>
            <li>
              <strong>People search</strong>
              <p className="muted">Find doctors and patients for front desk operations.</p>
              <Link to="/users">Open people</Link>
            </li>
            <li>
              <strong>Clinic setup</strong>
              <p className="muted">Manage departments and services for registration flow.</p>
              <Link to="/clinics">Open clinics</Link>
            </li>
          </ul>
        </article>

        <article className="panel workspace-panel workspace-panel-wide">
          <h3>Clinic Schedule Snapshot</h3>
          <p className="muted">Endpoint: <code>/api/v1/appointments/clinics/{'{'}clinicId{'}'}</code></p>
          <label>
            Clinic
            <select
              value={selectedClinicId || ""}
              onChange={(event) => setSelectedClinicId(Number(event.target.value))}
            >
              <option value="">Select clinic</option>
              {clinics.map((clinic) => (
                <option key={clinic.id} value={clinic.id}>
                  {clinic.name}
                </option>
              ))}
            </select>
          </label>

          {!upcoming.length && <p className="muted">No upcoming appointments for selected clinic.</p>}
          <ul className="timeline-list">
            {upcoming.map((appointment) => (
              <li key={appointment.id}>
                <div>
                  <strong>{formatDateTime(appointment.time)}</strong>
                  <p className="muted">
                    {appointment.patientName} to {appointment.doctorName}
                  </p>
                </div>
                <span className={`status-chip status-chip-${appointment.status.toLowerCase()}`}>
                  {appointment.status}
                </span>
              </li>
            ))}
          </ul>
        </article>
      </div>
    </section>
  );
}
