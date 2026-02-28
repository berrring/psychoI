import { FormEvent, useEffect, useState } from "react";
import { apiRequest, toErrorMessage } from "../api";
import { useAuth } from "../auth";
import type { PageResponse, UserInfo } from "../types";

export function UsersPage() {
  const { token } = useAuth();
  const [query, setQuery] = useState("");
  const [search, setSearch] = useState("");
  const [doctorPage, setDoctorPage] = useState(0);
  const [doctors, setDoctors] = useState<PageResponse<UserInfo> | null>(null);
  const [patients, setPatients] = useState<PageResponse<UserInfo> | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    let cancelled = false;
    async function load() {
      if (!token) return;
      setLoading(true);
      setError(null);
      try {
        const [doctorData, patientData] = await Promise.all([
          apiRequest<PageResponse<UserInfo>>(
            "/users/doctors",
            { method: "GET" },
            token,
            { query: search || undefined, page: doctorPage, size: 10 }
          ),
          apiRequest<PageResponse<UserInfo>>(
            "/users/patients",
            { method: "GET" },
            token,
            { page: 0, size: 10 }
          ).catch(() => null)
        ]);
        if (!cancelled) {
          setDoctors(doctorData);
          setPatients(patientData);
        }
      } catch (e) {
        if (!cancelled) setError(toErrorMessage(e));
      } finally {
        if (!cancelled) setLoading(false);
      }
    }
    load();
    return () => {
      cancelled = true;
    };
  }, [token, search, doctorPage]);

  function onSubmit(event: FormEvent) {
    event.preventDefault();
    setDoctorPage(0);
    setSearch(query.trim());
  }

  return (
    <section className="panel">
      <h2>Users</h2>
      <p className="muted">Endpoints: <code>/api/v1/users/doctors</code>, <code>/api/v1/users/patients</code></p>

      <form onSubmit={onSubmit} className="row-form">
        <input
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          placeholder="Search doctors by name/email/specialization"
        />
        <button type="submit">Search</button>
      </form>

      {loading && <p className="muted">Loading users...</p>}
      {error && <p className="error">{error}</p>}

      <div className="split-grid">
        <div className="panel-sub">
          <h3>Doctors</h3>
          <ul className="list">
            {doctors?.content.map((doctor) => (
              <li key={doctor.id}>
                <strong>{doctor.name}</strong> ({doctor.role})<br />
                <span>{doctor.email}</span>
                {doctor.specialization && <em> | {doctor.specialization}</em>}
              </li>
            ))}
          </ul>
          {doctors && (
            <div className="pagination-bar">
              <button
                type="button"
                disabled={doctors.first}
                onClick={() => setDoctorPage((p) => Math.max(0, p - 1))}
              >
                Prev
              </button>
              <span>
                {doctors.number + 1}/{Math.max(1, doctors.totalPages)}
              </span>
              <button
                type="button"
                disabled={doctors.last || doctors.totalPages === 0}
                onClick={() => setDoctorPage((p) => p + 1)}
              >
                Next
              </button>
            </div>
          )}
        </div>

        <div className="panel-sub">
          <h3>Patients</h3>
          <p className="muted">Visible for staff roles. Patients may receive 403.</p>
          <ul className="list">
            {patients?.content.map((patient) => (
              <li key={patient.id}>
                <strong>{patient.name}</strong> ({patient.role})<br />
                <span>{patient.email}</span>
              </li>
            ))}
          </ul>
          {!patients && <p className="muted">No access or no data.</p>}
        </div>
      </div>
    </section>
  );
}
