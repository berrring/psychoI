import { useEffect, useState } from "react";
import { apiRequest, toErrorMessage } from "../api";
import { useAuth } from "../auth";
import type { DashboardSummary } from "../types";

export function DashboardPage() {
  const { token } = useAuth();
  const [summary, setSummary] = useState<DashboardSummary | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!token) return;
    let cancelled = false;
    async function load() {
      setLoading(true);
      setError(null);
      try {
        const data = await apiRequest<DashboardSummary>("/dashboard/summary", { method: "GET" }, token);
        if (!cancelled) setSummary(data);
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
  }, [token]);

  return (
    <section className="panel">
      <h2>Dashboard</h2>
      <p className="muted">Secured endpoint: <code>/api/v1/dashboard/summary</code></p>

      {loading && <p className="muted">Loading summary...</p>}
      {error && <p className="error">{error}</p>}

      {summary && (
        <div className="kpi-grid">
          <div className="kpi-card">
            <span>Clinics</span>
            <strong>{summary.clinics}</strong>
          </div>
          <div className="kpi-card">
            <span>Doctors</span>
            <strong>{summary.doctors}</strong>
          </div>
          <div className="kpi-card">
            <span>Patients</span>
            <strong>{summary.patients}</strong>
          </div>
          <div className="kpi-card">
            <span>Appointments</span>
            <strong>{summary.appointments}</strong>
          </div>
          <div className="kpi-card">
            <span>Articles</span>
            <strong>{summary.articles}</strong>
          </div>
        </div>
      )}
    </section>
  );
}
