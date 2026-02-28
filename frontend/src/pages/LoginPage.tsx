import { FormEvent, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../auth";
import { toErrorMessage } from "../api";

export function LoginPage() {
  const navigate = useNavigate();
  const { login } = useAuth();

  const [email, setEmail] = useState("admin@clinic.local");
  const [password, setPassword] = useState("Admin123!");
  const [pending, setPending] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function onSubmit(event: FormEvent) {
    event.preventDefault();
    setPending(true);
    setError(null);
    try {
      await login(email, password);
      navigate("/workspace");
    } catch (e) {
      setError(toErrorMessage(e));
    } finally {
      setPending(false);
    }
  }

  return (
    <section className="panel panel-narrow">
      <h2>Sign In</h2>
      <p className="muted">
        Authenticate via <code>/api/v1/auth/login</code> and continue with secured endpoints.
      </p>

      <form onSubmit={onSubmit} className="form-grid">
        <label>
          Email
          <input value={email} onChange={(e) => setEmail(e.target.value)} type="email" required />
        </label>
        <label>
          Password
          <input
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            type="password"
            required
          />
        </label>
        {error && <p className="error">{error}</p>}
        <button type="submit" disabled={pending}>
          {pending ? "Signing in..." : "Sign In"}
        </button>
      </form>

      <div className="hint-box">
        <h3>Demo Credentials</h3>
        <p>
          <strong>Admin:</strong> admin@clinic.local / Admin123!
        </p>
        <p>
          <strong>Reception:</strong> reception@clinic.local / Reception123!
        </p>
        <p>
          <strong>Doctor:</strong> doc.alex@clinic.local / Doctor123!
        </p>
        <p>
          <strong>Patient:</strong> patient.demo@clinic.local / Patient123!
        </p>
        <p>
          <strong>Client:</strong> client.demo@clinic.local / Client123!
        </p>
      </div>
    </section>
  );
}

