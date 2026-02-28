import { useState } from "react";
import { apiRequest, toErrorMessage } from "../api";
import { useAuth } from "../auth";
import type { AuditEvent, PageResponse } from "../types";

export function AuditPage() {
  const { token } = useAuth();
  const [entityName, setEntityName] = useState("appointments");
  const [entityId, setEntityId] = useState<number>(0);
  const [actorId, setActorId] = useState<number>(0);
  const [events, setEvents] = useState<AuditEvent[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  async function loadByEntity() {
    if (!token || !entityName || !entityId) return;
    setLoading(true);
    setError(null);
    try {
      const data = await apiRequest<PageResponse<AuditEvent>>(
        `/audit/events/entity/${entityName}/${entityId}`,
        { method: "GET" },
        token,
        { page: 0, size: 50 }
      );
      setEvents(data.content);
    } catch (e) {
      setError(toErrorMessage(e));
    } finally {
      setLoading(false);
    }
  }

  async function loadByActor() {
    if (!token || !actorId) return;
    setLoading(true);
    setError(null);
    try {
      const data = await apiRequest<PageResponse<AuditEvent>>(
        `/audit/actors/${actorId}/events`,
        { method: "GET" },
        token,
        { page: 0, size: 50 }
      );
      setEvents(data.content);
    } catch (e) {
      setError(toErrorMessage(e));
    } finally {
      setLoading(false);
    }
  }

  return (
    <section className="panel">
      <h2>Audit Events</h2>
      <p className="muted">Staff endpoint with role restrictions.</p>

      <div className="split-grid">
        <div className="panel-sub">
          <h3>By Entity</h3>
          <div className="form-grid">
            <input
              value={entityName}
              onChange={(e) => setEntityName(e.target.value)}
              placeholder="entity name, e.g. appointments"
            />
            <input
              type="number"
              value={entityId || ""}
              onChange={(e) => setEntityId(Number(e.target.value))}
              placeholder="entity id"
            />
            <button type="button" onClick={loadByEntity}>
              Load by Entity
            </button>
          </div>
        </div>

        <div className="panel-sub">
          <h3>By Actor</h3>
          <div className="form-grid">
            <input
              type="number"
              value={actorId || ""}
              onChange={(e) => setActorId(Number(e.target.value))}
              placeholder="actor id"
            />
            <button type="button" onClick={loadByActor}>
              Load by Actor
            </button>
          </div>
        </div>
      </div>

      {loading && <p className="muted">Loading audit data...</p>}
      {error && <p className="error">{error}</p>}

      <ul className="list">
        {events.map((event) => (
          <li key={event.id}>
            <strong>{event.action}</strong> [{event.entityName}:{event.entityId}]<br />
            <span>{event.actorEmail}</span> at {event.createdAt}
            <p>{event.details}</p>
          </li>
        ))}
      </ul>
    </section>
  );
}
