import { FormEvent, useEffect, useMemo, useState } from "react";
import { Link, useLocation, useNavigate, useSearchParams } from "react-router-dom";
import { apiRequest, toErrorMessage } from "../api";
import type { PageResponse, PublicDoctorSummary } from "../types";

function normalizeTag(value: string): string {
  return value.trim().toLowerCase();
}

function splitAndNormalizeTags(tags: string[] | undefined): string[] {
  if (!tags) return [];
  return tags.map(normalizeTag).filter(Boolean);
}

export function DoctorsDirectoryPage() {
  const location = useLocation();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const returnTo = searchParams.get("returnTo") || "/patient-app";

  const [searchInput, setSearchInput] = useState(searchParams.get("query") || "");
  const [query, setQuery] = useState(searchParams.get("query") || "");
  const [page, setPage] = useState(0);
  const [data, setData] = useState<PageResponse<PublicDoctorSummary> | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [clinicFilter, setClinicFilter] = useState("all");
  const [tagFilter, setTagFilter] = useState("all");

  useEffect(() => {
    let cancelled = false;
    async function load() {
      setLoading(true);
      setError(null);
      try {
        const response = await apiRequest<PageResponse<PublicDoctorSummary>>(
          "/public/doctors",
          { method: "GET" },
          undefined,
          { query: query || undefined, page, size: 12 }
        );
        if (!cancelled) {
          setData(response);
        }
      } catch (e) {
        if (!cancelled) {
          setError(toErrorMessage(e));
        }
      } finally {
        if (!cancelled) {
          setLoading(false);
        }
      }
    }
    load();
    return () => {
      cancelled = true;
    };
  }, [query, page]);

  const clinics = useMemo(() => {
    const values = new Set<string>();
    data?.content.forEach((doctor) => {
      if (doctor.clinic) values.add(doctor.clinic);
    });
    return Array.from(values).sort((a, b) => a.localeCompare(b));
  }, [data?.content]);

  const tags = useMemo(() => {
    const values = new Set<string>();
    data?.content.forEach((doctor) => {
      splitAndNormalizeTags(doctor.tags).forEach((tag) => values.add(tag));
    });
    return Array.from(values).sort((a, b) => a.localeCompare(b));
  }, [data?.content]);

  const filteredDoctors = useMemo(() => {
    const base = data?.content ?? [];
    return base.filter((doctor) => {
      const clinicOk = clinicFilter === "all" || doctor.clinic === clinicFilter;
      if (!clinicOk) return false;
      if (tagFilter === "all") return true;
      const doctorTags = splitAndNormalizeTags(doctor.tags);
      return doctorTags.includes(tagFilter);
    });
  }, [data?.content, clinicFilter, tagFilter]);

  function onSearchSubmit(event: FormEvent) {
    event.preventDefault();
    setPage(0);
    setQuery(searchInput.trim());
  }

  function openDetails(doctorId: number) {
    const params = new URLSearchParams();
    if (returnTo) params.set("returnTo", returnTo);
    if (query) params.set("query", query);
    navigate(`/doctors/${doctorId}?${params.toString()}`, {
      state: {
        from: location.pathname
      }
    });
  }

  return (
    <section className="panel">
      <h2>Doctor Directory</h2>
      <p className="muted">Public endpoint: <code>/api/v1/public/doctors</code></p>

      <form onSubmit={onSearchSubmit} className="row-form">
        <input
          value={searchInput}
          onChange={(event) => setSearchInput(event.target.value)}
          placeholder="Search by doctor name, specialization or clinic"
        />
        <button type="submit">Search</button>
      </form>

      <div className="row-form">
        <label>
          Clinic
          <select value={clinicFilter} onChange={(event) => setClinicFilter(event.target.value)}>
            <option value="all">All clinics</option>
            {clinics.map((clinic) => (
              <option key={clinic} value={clinic}>
                {clinic}
              </option>
            ))}
          </select>
        </label>

        <label>
          Specialization tag
          <select value={tagFilter} onChange={(event) => setTagFilter(event.target.value)}>
            <option value="all">All tags</option>
            {tags.map((tag) => (
              <option key={tag} value={tag}>
                {tag}
              </option>
            ))}
          </select>
        </label>
      </div>

      {loading && <p className="muted">Loading doctors...</p>}
      {error && <p className="error">{error}</p>}

      {!loading && !error && (
        <>
          {!filteredDoctors.length && (
            <p className="muted">No doctors found for selected filters.</p>
          )}
          <ul className="doctor-directory-grid">
            {filteredDoctors.map((doctor) => (
              <li key={doctor.id}>
                <div>
                  <strong>{doctor.fullName}</strong>
                  <p className="muted">{doctor.specialization || "General practice"}</p>
                  {doctor.clinic && <p>{doctor.clinic}</p>}
                  {doctor.shortBio && <p>{doctor.shortBio}</p>}
                </div>
                <div className="library-tag-row">
                  {(doctor.tags || []).slice(0, 4).map((tag) => (
                    <span key={tag} className="library-tag-chip">
                      {tag}
                    </span>
                  ))}
                </div>
                <div className="inline-actions">
                  <button type="button" className="ghost-btn" onClick={() => openDetails(doctor.id)}>
                    View details
                  </button>
                  <Link to={`/doctors/${doctor.id}?returnTo=${encodeURIComponent(returnTo)}`}>Open profile</Link>
                </div>
              </li>
            ))}
          </ul>

          {data && (
            <div className="pagination-bar">
              <button type="button" disabled={data.first} onClick={() => setPage((value) => Math.max(0, value - 1))}>
                Prev
              </button>
              <span>
                {data.number + 1}/{Math.max(1, data.totalPages)}
              </span>
              <button
                type="button"
                disabled={data.last || data.totalPages === 0}
                onClick={() => setPage((value) => value + 1)}
              >
                Next
              </button>
            </div>
          )}
        </>
      )}
    </section>
  );
}
