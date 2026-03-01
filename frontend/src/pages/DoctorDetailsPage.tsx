import { useEffect, useMemo, useState } from "react";
import { Link, useNavigate, useParams, useSearchParams } from "react-router-dom";
import { apiRequest, toErrorMessage } from "../api";
import type { PublicDoctorDetails } from "../types";

export function DoctorDetailsPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const returnTo = searchParams.get("returnTo") || "/patient-app";

  const [doctor, setDoctor] = useState<PublicDoctorDetails | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const doctorId = useMemo(() => Number(id), [id]);

  useEffect(() => {
    if (!doctorId || Number.isNaN(doctorId)) {
      setError("Doctor ID is invalid");
      return;
    }

    let cancelled = false;
    async function load() {
      setLoading(true);
      setError(null);
      try {
        const data = await apiRequest<PublicDoctorDetails>(`/public/doctors/${doctorId}`, { method: "GET" });
        if (!cancelled) setDoctor(data);
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
  }, [doctorId]);

  function onSelectDoctor() {
    if (!doctor) return;
    navigate(returnTo, {
      state: {
        selectedDoctorId: doctor.id,
        selectedDoctorName: doctor.fullName
      }
    });
  }

  if (error) {
    return (
      <section className="panel panel-narrow">
        <h2>Doctor profile unavailable</h2>
        <p className="error">{error}</p>
        <Link to="/doctors">Back to directory</Link>
      </section>
    );
  }

  return (
    <section className="panel doctor-details-page">
      {loading && <p className="muted">Loading doctor profile...</p>}

      {doctor && (
        <>
          <div className="doctor-details-head">
            <div>
              <p className="eyebrow">Doctor Details</p>
              <h2>{doctor.fullName}</h2>
              <p className="muted">{doctor.specialization || "General practice"}</p>
            </div>
            <div className="doctor-details-actions">
              <button type="button" onClick={onSelectDoctor}>
                Select doctor
              </button>
              <Link to="/doctors">Back to directory</Link>
            </div>
          </div>

          <div className="doctor-details-grid">
            <article className="panel-sub">
              <h3>Profile</h3>
              <p>{doctor.fullBio || doctor.shortBio || "Doctor profile is being updated."}</p>
            </article>

            <article className="panel-sub">
              <h3>Professional Information</h3>
              <ul className="list">
                <li>
                  <strong>Clinic:</strong> {doctor.clinic || "Not specified"}
                </li>
                <li>
                  <strong>Location:</strong> {doctor.clinicCity || "City N/A"}, {doctor.clinicAddress || "Address N/A"}
                </li>
                <li>
                  <strong>Experience:</strong> {doctor.experienceYears ?? 0} years
                </li>
              </ul>
            </article>
          </div>

          {!!doctor.tags?.length && (
            <div className="library-tag-row">
              {doctor.tags.map((tag) => (
                <span key={tag} className="library-tag-chip">
                  {tag}
                </span>
              ))}
            </div>
          )}
        </>
      )}
    </section>
  );
}
