import { FormEvent, useEffect, useMemo, useState } from "react";
import { apiRequest, toErrorMessage } from "../api";
import { useAuth } from "../auth";
import type { Clinic, Department, MedicalService } from "../types";

export function ClinicsPage() {
  const { token, hasRole } = useAuth();
  const canManageClinicData = hasRole("ADMIN", "RECEPTIONIST");
  const [clinics, setClinics] = useState<Clinic[]>([]);
  const [departments, setDepartments] = useState<Department[]>([]);
  const [services, setServices] = useState<MedicalService[]>([]);
  const [selectedClinicId, setSelectedClinicId] = useState<number | null>(null);
  const [selectedDepartmentId, setSelectedDepartmentId] = useState<number | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const [clinicForm, setClinicForm] = useState({
    name: "",
    city: "",
    address: "",
    phone: "",
    email: "",
    description: ""
  });
  const [departmentForm, setDepartmentForm] = useState({ name: "", description: "" });
  const [serviceForm, setServiceForm] = useState({
    code: "",
    name: "",
    description: "",
    durationMinutes: 30,
    basePrice: 10
  });

  const selectedClinic = useMemo(
    () => clinics.find((clinic) => clinic.id === selectedClinicId) ?? null,
    [clinics, selectedClinicId]
  );

  async function loadClinics() {
    if (!token) return;
    const data = await apiRequest<Clinic[]>("/clinics", { method: "GET" }, token);
    setClinics(data);
    if (data.length > 0 && !selectedClinicId) {
      setSelectedClinicId(data[0].id);
    }
  }

  useEffect(() => {
    let cancelled = false;
    async function init() {
      if (!token) return;
      setLoading(true);
      setError(null);
      try {
        await loadClinics();
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
    async function loadDepartments() {
      if (!token || !selectedClinicId) {
        setDepartments([]);
        return;
      }
      setError(null);
      try {
        const data = await apiRequest<Department[]>(
          `/clinics/${selectedClinicId}/departments`,
          { method: "GET" },
          token
        );
        if (!cancelled) {
          setDepartments(data);
          const firstDepartmentId = data[0]?.id ?? null;
          setSelectedDepartmentId(firstDepartmentId);
        }
      } catch (e) {
        if (!cancelled) setError(toErrorMessage(e));
      }
    }
    loadDepartments();
    return () => {
      cancelled = true;
    };
  }, [token, selectedClinicId]);

  useEffect(() => {
    let cancelled = false;
    async function loadServices() {
      if (!token || !selectedDepartmentId) {
        setServices([]);
        return;
      }
      setError(null);
      try {
        const data = await apiRequest<MedicalService[]>(
          `/clinics/departments/${selectedDepartmentId}/services`,
          { method: "GET" },
          token
        );
        if (!cancelled) setServices(data);
      } catch (e) {
        if (!cancelled) setError(toErrorMessage(e));
      }
    }
    loadServices();
    return () => {
      cancelled = true;
    };
  }, [token, selectedDepartmentId]);

  async function createClinic(event: FormEvent) {
    event.preventDefault();
    if (!token) return;
    setError(null);
    try {
      await apiRequest<Clinic>(
        "/clinics",
        { method: "POST", body: JSON.stringify(clinicForm) },
        token
      );
      setClinicForm({ name: "", city: "", address: "", phone: "", email: "", description: "" });
      await loadClinics();
    } catch (e) {
      setError(toErrorMessage(e));
    }
  }

  async function createDepartment(event: FormEvent) {
    event.preventDefault();
    if (!token || !selectedClinicId) return;
    setError(null);
    try {
      await apiRequest<Department>(
        `/clinics/${selectedClinicId}/departments`,
        { method: "POST", body: JSON.stringify(departmentForm) },
        token
      );
      setDepartmentForm({ name: "", description: "" });
      const data = await apiRequest<Department[]>(
        `/clinics/${selectedClinicId}/departments`,
        { method: "GET" },
        token
      );
      setDepartments(data);
    } catch (e) {
      setError(toErrorMessage(e));
    }
  }

  async function createService(event: FormEvent) {
    event.preventDefault();
    if (!token || !selectedDepartmentId) return;
    setError(null);
    try {
      await apiRequest<MedicalService>(
        `/clinics/departments/${selectedDepartmentId}/services`,
        { method: "POST", body: JSON.stringify(serviceForm) },
        token
      );
      setServiceForm({ code: "", name: "", description: "", durationMinutes: 30, basePrice: 10 });
      const data = await apiRequest<MedicalService[]>(
        `/clinics/departments/${selectedDepartmentId}/services`,
        { method: "GET" },
        token
      );
      setServices(data);
    } catch (e) {
      setError(toErrorMessage(e));
    }
  }

  return (
    <section className="panel">
      <h2>Clinics</h2>
      <p className="muted">Endpoints: <code>/api/v1/clinics/*</code></p>
      {!canManageClinicData && (
        <p className="muted">Read-only mode. Create/update actions are available for ADMIN and RECEPTIONIST roles.</p>
      )}

      {loading && <p className="muted">Loading clinics...</p>}
      {error && <p className="error">{error}</p>}

      <div className="split-grid">
        <div className="panel-sub">
          <h3>Clinics</h3>
          <select
            value={selectedClinicId ?? ""}
            onChange={(e) => setSelectedClinicId(Number(e.target.value))}
          >
            {clinics.map((clinic) => (
              <option key={clinic.id} value={clinic.id}>
                {clinic.name} ({clinic.city})
              </option>
            ))}
          </select>
          {selectedClinic && (
            <div className="hint-box">
              <p><strong>Address:</strong> {selectedClinic.address}</p>
              <p><strong>Phone:</strong> {selectedClinic.phone || "-"}</p>
              <p><strong>Email:</strong> {selectedClinic.email || "-"}</p>
            </div>
          )}

          {canManageClinicData && (
            <>
              <h4>Create Clinic</h4>
              <form onSubmit={createClinic} className="form-grid">
                <input
                  placeholder="Name"
                  value={clinicForm.name}
                  onChange={(e) => setClinicForm((v) => ({ ...v, name: e.target.value }))}
                  required
                />
                <input
                  placeholder="City"
                  value={clinicForm.city}
                  onChange={(e) => setClinicForm((v) => ({ ...v, city: e.target.value }))}
                  required
                />
                <input
                  placeholder="Address"
                  value={clinicForm.address}
                  onChange={(e) => setClinicForm((v) => ({ ...v, address: e.target.value }))}
                  required
                />
                <input
                  placeholder="Phone"
                  value={clinicForm.phone}
                  onChange={(e) => setClinicForm((v) => ({ ...v, phone: e.target.value }))}
                />
                <input
                  placeholder="Email"
                  type="email"
                  value={clinicForm.email}
                  onChange={(e) => setClinicForm((v) => ({ ...v, email: e.target.value }))}
                />
                <textarea
                  placeholder="Description"
                  value={clinicForm.description}
                  onChange={(e) => setClinicForm((v) => ({ ...v, description: e.target.value }))}
                />
                <button type="submit">Create Clinic</button>
              </form>
            </>
          )}
        </div>

        <div className="panel-sub">
          <h3>Departments</h3>
          <ul className="list">
            {departments.map((department) => (
              <li key={department.id}>
                <button
                  type="button"
                  className={`list-btn${selectedDepartmentId === department.id ? " list-btn-active" : ""}`}
                  onClick={() => setSelectedDepartmentId(department.id)}
                >
                  {department.name}
                </button>
              </li>
            ))}
          </ul>

          {canManageClinicData && (
            <>
              <h4>Create Department</h4>
              <form onSubmit={createDepartment} className="form-grid">
                <input
                  placeholder="Name"
                  value={departmentForm.name}
                  onChange={(e) => setDepartmentForm((v) => ({ ...v, name: e.target.value }))}
                  required
                />
                <textarea
                  placeholder="Description"
                  value={departmentForm.description}
                  onChange={(e) => setDepartmentForm((v) => ({ ...v, description: e.target.value }))}
                />
                <button type="submit" disabled={!selectedClinicId}>
                  Create Department
                </button>
              </form>
            </>
          )}

          <h3>Services</h3>
          <ul className="list">
            {services.map((service) => (
              <li key={service.id}>
                {service.name} ({service.durationMinutes} min, ${service.basePrice})
              </li>
            ))}
          </ul>

          {canManageClinicData && (
            <>
              <h4>Create Service</h4>
              <form onSubmit={createService} className="form-grid">
                <input
                  placeholder="Code"
                  value={serviceForm.code}
                  onChange={(e) => setServiceForm((v) => ({ ...v, code: e.target.value }))}
                  required
                />
                <input
                  placeholder="Name"
                  value={serviceForm.name}
                  onChange={(e) => setServiceForm((v) => ({ ...v, name: e.target.value }))}
                  required
                />
                <textarea
                  placeholder="Description"
                  value={serviceForm.description}
                  onChange={(e) => setServiceForm((v) => ({ ...v, description: e.target.value }))}
                />
                <label>
                  Duration minutes
                  <input
                    type="number"
                    min={5}
                    value={serviceForm.durationMinutes}
                    onChange={(e) =>
                      setServiceForm((v) => ({ ...v, durationMinutes: Number(e.target.value) }))
                    }
                  />
                </label>
                <label>
                  Base price
                  <input
                    type="number"
                    min={0}
                    step="0.01"
                    value={serviceForm.basePrice}
                    onChange={(e) =>
                      setServiceForm((v) => ({ ...v, basePrice: Number(e.target.value) }))
                    }
                  />
                </label>
                <button type="submit" disabled={!selectedDepartmentId}>
                  Create Service
                </button>
              </form>
            </>
          )}
        </div>
      </div>
    </section>
  );
}
