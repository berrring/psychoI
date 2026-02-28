import { FormEvent, useEffect, useMemo, useState } from "react";
import { apiRequest, toErrorMessage } from "../api";
import { useAuth } from "../auth";
import type { Appointment, Clinic, PageResponse, UserInfo } from "../types";

function toApiDateTime(value: string): string {
  if (!value) return value;
  return value.length === 16 ? `${value}:00` : value;
}

function formatDateTime(value: string): string {
  return new Date(value).toLocaleString();
}

export function ClientWorkspacePage() {
  const { token, session } = useAuth();
  const [doctors, setDoctors] = useState<UserInfo[]>([]);
  const [clinics, setClinics] = useState<Clinic[]>([]);
  const [appointments, setAppointments] = useState<Appointment[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [form, setForm] = useState({
    doctorId: 0,
    clinicId: 0,
    time: "",
    complaint: ""
  });

  const upcomingAppointments = useMemo(
    () =>
      appointments
        .slice()
        .sort((a, b) => new Date(a.time).getTime() - new Date(b.time).getTime())
        .filter((item) => new Date(item.time).getTime() >= Date.now()),
    [appointments]
  );

  async function loadWorkspace() {
    if (!token || !session) return;
    const [doctorData, clinicData, appointmentData] = await Promise.all([
      apiRequest<PageResponse<UserInfo>>("/users/doctors", { method: "GET" }, token, { page: 0, size: 12 }),
      apiRequest<Clinic[]>("/clinics", { method: "GET" }, token),
      apiRequest<PageResponse<Appointment>>(
        "/appointments",
        { method: "GET" },
        token,
        { patientId: session.userId, page: 0, size: 30 }
      )
    ]);

    setDoctors(doctorData.content);
    setClinics(clinicData);
    setAppointments(appointmentData.content);
    setForm((current) => ({
      ...current,
      clinicId: current.clinicId || clinicData[0]?.id || 0
    }));
  }

  useEffect(() => {
    let cancelled = false;
    async function init() {
      if (!token || !session) return;
      setLoading(true);
      setError(null);
      try {
        await loadWorkspace();
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
  }, [token, session?.userId]);

  async function createBooking(event: FormEvent) {
    event.preventDefault();
    if (!token || !session) return;
    setSaving(true);
    setError(null);
    try {
      await apiRequest<Appointment>(
        "/appointments",
        {
          method: "POST",
          body: JSON.stringify({
            patientId: session.userId,
            doctorId: form.doctorId,
            clinicId: form.clinicId,
            time: toApiDateTime(form.time),
            durationMinutes: 30,
            complaint: form.complaint || null
          })
        },
        token
      );
      setForm((current) => ({
        ...current,
        time: "",
        complaint: ""
      }));
      await loadWorkspace();
    } catch (e) {
      setError(toErrorMessage(e));
    } finally {
      setSaving(false);
    }
  }

  return (
    <section className="workspace">
      <div className="workspace-hero workspace-hero-client">
        <p className="eyebrow">Client App</p>
        <h2>Your Care Journey</h2>
        <p>Book appointments, choose specialists and track upcoming visits from one workspace.</p>
      </div>

      {loading && <p className="muted">Loading workspace...</p>}
      {error && <p className="error">{error}</p>}

      <div className="workspace-grid">
        <article className="panel workspace-panel">
          <h3>Book Appointment</h3>
          <p className="muted">Endpoint: <code>/api/v1/appointments</code></p>
          <form className="form-grid" onSubmit={createBooking}>
            <label>
              Doctor
              <select
                value={form.doctorId || ""}
                onChange={(e) => setForm((value) => ({ ...value, doctorId: Number(e.target.value) }))}
                required
              >
                <option value="">Select doctor</option>
                {doctors.map((doctor) => (
                  <option key={doctor.id} value={doctor.id}>
                    {doctor.name} ({doctor.specialization || doctor.role})
                  </option>
                ))}
              </select>
            </label>

            <label>
              Clinic
              <select
                value={form.clinicId || ""}
                onChange={(e) => setForm((value) => ({ ...value, clinicId: Number(e.target.value) }))}
                required
              >
                <option value="">Select clinic</option>
                {clinics.map((clinic) => (
                  <option key={clinic.id} value={clinic.id}>
                    {clinic.name} - {clinic.city}
                  </option>
                ))}
              </select>
            </label>

            <label>
              Preferred Date and Time
              <input
                type="datetime-local"
                value={form.time}
                onChange={(e) => setForm((value) => ({ ...value, time: e.target.value }))}
                required
              />
            </label>

            <label>
              Primary Concern
              <textarea
                value={form.complaint}
                onChange={(e) => setForm((value) => ({ ...value, complaint: e.target.value }))}
                placeholder="Describe your symptoms or reason for visit"
              />
            </label>

            <button type="submit" disabled={saving}>
              {saving ? "Booking..." : "Book Appointment"}
            </button>
          </form>
        </article>

        <article className="panel workspace-panel">
          <h3>Available Doctors</h3>
          <ul className="doctor-grid">
            {doctors.map((doctor) => (
              <li key={doctor.id}>
                <strong>{doctor.name}</strong>
                <span>{doctor.specialization || "General practice"}</span>
                <small>{doctor.yearsOfExperience ?? 0} years experience</small>
              </li>
            ))}
          </ul>
        </article>

        <article className="panel workspace-panel workspace-panel-wide">
          <h3>Upcoming Visits</h3>
          {!upcomingAppointments.length && <p className="muted">No upcoming appointments yet.</p>}
          <ul className="timeline-list">
            {upcomingAppointments.map((appointment) => (
              <li key={appointment.id}>
                <div>
                  <strong>{formatDateTime(appointment.time)}</strong>
                  <p className="muted">
                    {appointment.doctorName} at {appointment.clinicName}
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
