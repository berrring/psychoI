import { FormEvent, useState } from "react";
import { apiRequest, toErrorMessage } from "../api";
import { useAuth } from "../auth";
import type { MessageEvent, MessageType } from "../types";

const MESSAGE_TYPES: MessageType[] = ["CHAT", "NOTE", "SYSTEM"];

export function EventsPage() {
  const { token, session } = useAuth();

  const [appointmentId, setAppointmentId] = useState<number>(0);
  const [events, setEvents] = useState<MessageEvent[]>([]);
  const [type, setType] = useState<MessageType>("NOTE");
  const [text, setText] = useState("");
  const [metadata, setMetadata] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  async function loadTimeline() {
    if (!token || !appointmentId) return;
    setLoading(true);
    setError(null);
    try {
      const data = await apiRequest<MessageEvent[]>(
        `/appointments/${appointmentId}/events/timeline`,
        { method: "GET" },
        token
      );
      setEvents(data);
    } catch (e) {
      setError(toErrorMessage(e));
    } finally {
      setLoading(false);
    }
  }

  async function sendEvent(event: FormEvent) {
    event.preventDefault();
    if (!token || !appointmentId || !session?.userId) return;
    setError(null);
    try {
      await apiRequest<MessageEvent>(
        `/appointments/${appointmentId}/events`,
        {
          method: "POST",
          body: JSON.stringify({
            senderId: session.userId,
            appointmentId,
            type,
            text,
            metadata: metadata || null
          })
        },
        token
      );
      setText("");
      setMetadata("");
      await loadTimeline();
    } catch (e) {
      setError(toErrorMessage(e));
    }
  }

  return (
    <section className="panel">
      <h2>Appointment Events Timeline</h2>
      <p className="muted">Endpoints: <code>/api/v1/appointments/:id/events*</code></p>

      <div className="row-form">
        <input
          type="number"
          value={appointmentId || ""}
          onChange={(e) => setAppointmentId(Number(e.target.value))}
          placeholder="Appointment ID"
        />
        <button type="button" onClick={loadTimeline}>
          Load Timeline
        </button>
      </div>

      <form className="form-grid" onSubmit={sendEvent}>
        <label>
          Event Type
          <select value={type} onChange={(e) => setType(e.target.value as MessageType)}>
            {MESSAGE_TYPES.map((value) => (
              <option key={value} value={value}>
                {value}
              </option>
            ))}
          </select>
        </label>
        <textarea
          placeholder="Message text"
          value={text}
          onChange={(e) => setText(e.target.value)}
          required
        />
        <input
          placeholder="Metadata (optional)"
          value={metadata}
          onChange={(e) => setMetadata(e.target.value)}
        />
        <button type="submit" disabled={!appointmentId}>
          Send Event
        </button>
      </form>

      {loading && <p className="muted">Loading events...</p>}
      {error && <p className="error">{error}</p>}

      <ul className="list">
        {events.map((item) => (
          <li key={item.id}>
            <strong>{item.type}</strong> by {item.senderName} at {item.time}
            <p>{item.text}</p>
            {item.metadata && <p className="muted">Metadata: {item.metadata}</p>}
          </li>
        ))}
      </ul>
    </section>
  );
}
