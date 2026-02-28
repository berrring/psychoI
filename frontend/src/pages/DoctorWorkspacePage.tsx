import { FormEvent, useEffect, useMemo, useState } from "react";
import { apiRequest, toErrorMessage } from "../api";
import { useAuth } from "../auth";
import type { Appointment, AppointmentStatus, PageResponse, UserInfo } from "../types";

const DOCTOR_STATUS_OPTIONS: AppointmentStatus[] = [
  "CONFIRMED",
  "IN_PROGRESS",
  "COMPLETED",
  "CANCELLED",
  "NO_SHOW"
];

function formatDateTime(value: string): string {
  return new Date(value).toLocaleString();
}

function toDateParam(value: Date): string {
  const year = value.getFullYear();
  const month = String(value.getMonth() + 1).padStart(2, "0");
  const day = String(value.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
}

function isToday(value: string): boolean {
  const current = new Date();
  const date = new Date(value);
  return (
    current.getFullYear() === date.getFullYear() &&
    current.getMonth() === date.getMonth() &&
    current.getDate() === date.getDate()
  );
}

export function DoctorWorkspacePage() {
  const { token, session } = useAuth();
  const [profile, setProfile] = useState<UserInfo | null>(null);
  const [appointments, setAppointments] = useState<Appointment[]>([]);
  const [calendar, setCalendar] = useState<Appointment[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [pendingStatusId, setPendingStatusId] = useState<number | null>(null);
  const [selectedAppointmentId, setSelectedAppointmentId] = useState<number | null>(null);
  const [savingNotes, setSavingNotes] = useState(false);
  const [notesForm, setNotesForm] = useState({
    diagnosis: "",
    treatmentPlan: "",
    notes: ""
  });

  const todayAppointments = useMemo(
    () =>
      calendar
        .filter((appointment) => isToday(appointment.time))
        .sort((a, b) => new Date(a.time).getTime() - new Date(b.time).getTime()),
    [calendar]
  );

  const upcomingAppointments = useMemo(
    () =>
      appointments
        .slice()
        .sort((a, b) => new Date(a.time).getTime() - new Date(b.time).getTime())
        .filter((appointment) => new Date(appointment.time).getTime() >= Date.now()),
    [appointments]
  );

  async function loadDoctorWorkspace() {
    if (!token || !session) return;
    const from = new Date();
    const to = new Date();
    to.setDate(to.getDate() + 14);

    const [doctorProfile, appointmentData, calendarData] = await Promise.all([
      apiRequest<UserInfo>(`/users/${session.userId}`, { method: "GET" }, token),
      apiRequest<PageResponse<Appointment>>(
        "/appointments",
        { method: "GET" },
        token,
        { doctorId: session.userId, page: 0, size: 40 }
      ),
      apiRequest<Appointment[]>(
        `/appointments/calendar/doctors/${session.userId}`,
        { method: "GET" },
        token,
        {
          from: toDateParam(from),
          to: toDateParam(to)
        }
      )
    ]);

    setProfile(doctorProfile);
    setAppointments(appointmentData.content);
    setCalendar(calendarData);
  }

  useEffect(() => {
    let cancelled = false;
    async function init() {
      if (!token || !session) return;
      setLoading(true);
      setError(null);
      try {
        await loadDoctorWorkspace();
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

  function onSelectAppointment(appointment: Appointment) {
    setSelectedAppointmentId(appointment.id);
    setNotesForm({
      diagnosis: appointment.diagnosis || "",
      treatmentPlan: appointment.treatmentPlan || "",
      notes: appointment.notes || ""
    });
  }

  async function onStatusChange(appointmentId: number, status: AppointmentStatus) {
    if (!token) return;
    setPendingStatusId(appointmentId);
    setError(null);
    try {
      await apiRequest<Appointment>(
        `/appointments/${appointmentId}/status`,
        {
          method: "PATCH",
          body: JSON.stringify({ status })
        },
        token
      );
      await loadDoctorWorkspace();
    } catch (e) {
      setError(toErrorMessage(e));
    } finally {
      setPendingStatusId(null);
    }
  }

  async function onSaveClinicalNotes(event: FormEvent) {
    event.preventDefault();
    if (!token || !selectedAppointmentId) return;
    setSavingNotes(true);
    setError(null);
    try {
      await apiRequest<Appointment>(
        `/appointments/${selectedAppointmentId}`,
        {
          method: "PATCH",
          body: JSON.stringify({
            diagnosis: notesForm.diagnosis || null,
            treatmentPlan: notesForm.treatmentPlan || null,
            notes: notesForm.notes || null
          })
        },
        token
      );
      await loadDoctorWorkspace();
    } catch (e) {
      setError(toErrorMessage(e));
    } finally {
      setSavingNotes(false);
    }
  }

  return (
    <section className="workspace">
      <div className="workspace-hero workspace-hero-doctor">
        <p className="eyebrow">Doctor App</p>
        <h2>Clinical Schedule and Visit Management</h2>
        <p>Track daily appointments, update statuses and keep structured clinical notes.</p>
      </div>

      {loading && <p className="muted">Loading doctor workspace...</p>}
      {error && <p className="error">{error}</p>}

      <div className="workspace-grid">
        <article className="panel workspace-panel">
          <h3>Doctor Profile</h3>
          {profile ? (
            <div className="profile-card">
              <strong>{profile.name}</strong>
              <p className="muted">{profile.specialization || profile.role}</p>
              <p>{profile.about || "No profile details provided."}</p>
              <small>{profile.yearsOfExperience ?? 0} years experience</small>
            </div>
          ) : (
            <p className="muted">Profile unavailable.</p>
          )}
        </article>

        <article className="panel workspace-panel">
          <h3>Today</h3>
          <ul className="timeline-list">
            {todayAppointments.map((appointment) => (
              <li key={appointment.id}>
                <div>
                  <strong>{formatDateTime(appointment.time)}</strong>
                  <p className="muted">{appointment.patientName}</p>
                </div>
                <span className={`status-chip status-chip-${appointment.status.toLowerCase()}`}>
                  {appointment.status}
                </span>
              </li>
            ))}
            {!todayAppointments.length && <li className="muted">No visits for today.</li>}
          </ul>
        </article>

        <article className="panel workspace-panel workspace-panel-wide">
          <h3>Upcoming Appointments</h3>
          <ul className="timeline-list">
            {upcomingAppointments.map((appointment) => (
              <li key={appointment.id}>
                <div>
                  <strong>
                    #{appointment.id} {appointment.patientName}
                  </strong>
                  <p className="muted">
                    {formatDateTime(appointment.time)} | {appointment.clinicName}
                  </p>
                </div>
                <div className="inline-actions">
                  {DOCTOR_STATUS_OPTIONS.map((status) => (
                    <button
                      key={status}
                      type="button"
                      className={status === appointment.status ? "ghost-btn ghost-btn-active" : "ghost-btn"}
                      onClick={() => onStatusChange(appointment.id, status)}
                      disabled={pendingStatusId === appointment.id}
                    >
                      {status}
                    </button>
                  ))}
                  <button type="button" className="ghost-btn" onClick={() => onSelectAppointment(appointment)}>
                    Edit Notes
                  </button>
                </div>
              </li>
            ))}
          </ul>
        </article>

        <article className="panel workspace-panel workspace-panel-wide">
          <h3>Clinical Notes</h3>
          <p className="muted">Endpoint: <code>/api/v1/appointments/:id</code></p>
          {!selectedAppointmentId && <p className="muted">Select appointment from list above to edit notes.</p>}
          {selectedAppointmentId && (
            <form className="form-grid" onSubmit={onSaveClinicalNotes}>
              <label>
                Diagnosis
                <textarea
                  value={notesForm.diagnosis}
                  onChange={(e) => setNotesForm((value) => ({ ...value, diagnosis: e.target.value }))}
                />
              </label>
              <label>
                Treatment Plan
                <textarea
                  value={notesForm.treatmentPlan}
                  onChange={(e) => setNotesForm((value) => ({ ...value, treatmentPlan: e.target.value }))}
                />
              </label>
              <label>
                Notes
                <textarea
                  value={notesForm.notes}
                  onChange={(e) => setNotesForm((value) => ({ ...value, notes: e.target.value }))}
                />
              </label>
              <button type="submit" disabled={savingNotes}>
                {savingNotes ? "Saving..." : "Save Clinical Notes"}
              </button>
            </form>
          )}
        </article>
      </div>
    </section>
  );
}
