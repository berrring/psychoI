import { FormEvent, useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { apiRequest, toErrorMessage } from "../api";
import type { Article, KnowledgeCategory, PageResponse } from "../types";

const CATEGORIES: KnowledgeCategory[] = [
  "PREVENTION",
  "DISEASES",
  "DIAGNOSTICS",
  "TREATMENT",
  "REHABILITATION",
  "NUTRITION",
  "MENTAL_HEALTH",
  "FAQ",
  "NEWS"
];

const CARE_PATHWAYS = [
  {
    title: "Diagnostics",
    text: "MRI, ultrasound and lab pathways with rapid consultant reporting."
  },
  {
    title: "Surgery & Treatment",
    text: "Planned procedures and targeted therapy programmes across specialties."
  },
  {
    title: "Rehabilitation",
    text: "Recovery plans with physiotherapy, nutrition and follow-up monitoring."
  },
  {
    title: "Mental Health",
    text: "Psychologists and psychiatrists integrated with the wider clinical team."
  }
];

const TRUST_POINTS = [
  "Board-certified consultants",
  "Evidence-based protocols",
  "Secure digital patient history",
  "24/7 patient contact centre"
];

function formatCategory(category: string): string {
  return category.replace(/_/g, " ");
}

export function PublicKnowledgePage() {
  const [queryInput, setQueryInput] = useState("");
  const [query, setQuery] = useState("");
  const [category, setCategory] = useState<KnowledgeCategory | "">("");
  const [page, setPage] = useState(0);
  const [result, setResult] = useState<PageResponse<Article> | null>(null);
  const [news, setNews] = useState<Article[]>([]);
  const [loading, setLoading] = useState(false);
  const [newsLoading, setNewsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let cancelled = false;
    async function load() {
      setLoading(true);
      setError(null);
      try {
        const data = await apiRequest<PageResponse<Article>>(
          "/public/knowledge/articles",
          { method: "GET" },
          undefined,
          {
            query: query || undefined,
            category: category || undefined,
            page,
            size: 8
          }
        );
        if (!cancelled) setResult(data);
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
  }, [query, category, page]);

  useEffect(() => {
    let cancelled = false;
    async function loadNews() {
      setNewsLoading(true);
      try {
        const data = await apiRequest<PageResponse<Article>>(
          "/public/knowledge/articles",
          { method: "GET" },
          undefined,
          {
            category: "NEWS",
            page: 0,
            size: 3
          }
        );
        if (!cancelled) setNews(data.content);
      } catch (e) {
        if (!cancelled) setError(toErrorMessage(e));
      } finally {
        if (!cancelled) setNewsLoading(false);
      }
    }
    loadNews();
    return () => {
      cancelled = true;
    };
  }, []);

  function onSearch(event: FormEvent) {
    event.preventDefault();
    setPage(0);
    setQuery(queryInput.trim());
  }

  return (
    <section className="landing-page">
      <div className="landing-hero">
        <img
          className="landing-hero-image"
          src="/images/hero-doctor-patient.jpg"
          alt="Doctor consultation with patient"
        />
        <div className="landing-hero-overlay">
          <p className="eyebrow">NorthCare Private Hospital Group</p>
          <h2>Clinical excellence with one connected care journey</h2>
          <p className="hero-copy">
            Find trusted information, discover specialists, and move from diagnostics to treatment with one digital
            platform.
          </p>

          <form className="landing-search" onSubmit={onSearch}>
            <input
              value={queryInput}
              onChange={(e) => setQueryInput(e.target.value)}
              placeholder="Search conditions, diagnostics, treatment plans"
            />
            <select
              value={category}
              onChange={(e) => {
                setCategory(e.target.value as KnowledgeCategory | "");
                setPage(0);
              }}
            >
              <option value="">All categories</option>
              {CATEGORIES.map((value) => (
                <option key={value} value={value}>
                  {formatCategory(value)}
                </option>
              ))}
            </select>
            <button type="submit">Search Library</button>
          </form>

          <div className="hero-stats">
            <article className="hero-stat">
              <strong>{result?.totalElements ?? 0}</strong>
              <span>Published Articles</span>
            </article>
            <article className="hero-stat">
              <strong>15+</strong>
              <span>Clinical Disciplines</span>
            </article>
            <article className="hero-stat">
              <strong>24/7</strong>
              <span>Patient Support</span>
            </article>
          </div>

          <p className="hero-endpoint">
            Public API endpoint: <code>/api/v1/public/knowledge/articles</code>
          </p>
        </div>
      </div>

      <div className="trust-strip">
        {TRUST_POINTS.map((item) => (
          <p key={item}>{item}</p>
        ))}
      </div>

      <section className="service-section">
        <div className="section-heading">
          <h3>Comprehensive care pathways</h3>
          <p className="muted">
            Structured treatment tracks from first symptoms to rehabilitation, powered by your backend domain modules.
          </p>
        </div>
        <div className="service-grid">
          {CARE_PATHWAYS.map((item) => (
            <article key={item.title} className="service-card">
              <h4>{item.title}</h4>
              <p>{item.text}</p>
            </article>
          ))}
        </div>
      </section>

      <section className="story-grid">
        <article className="story-card story-card-large">
          <img src="/images/team-corridor.jpg" alt="Medical team in hospital corridor" />
          <div>
            <h3>Consultant-led collaboration</h3>
            <p>
              Every patient plan is reviewed across departments, connecting doctors, diagnostics and long-term follow-up.
            </p>
          </div>
        </article>

        <article className="story-card">
          <img src="/images/mri-scan.jpg" alt="MRI scan process" />
          <div>
            <h3>High-end diagnostics</h3>
            <p>Faster imaging and reporting to shorten time from concern to treatment decision.</p>
          </div>
        </article>

        <article className="story-card">
          <img src="/images/reception-consultation.jpg" alt="Reception and consultation support" />
          <div>
            <h3>Seamless patient access</h3>
            <p>Single front door for appointments, records and care coordination across clinics.</p>
          </div>
        </article>
      </section>

      <section className="news-section">
        <div className="section-heading">
          <h3>Clinic News and Updates</h3>
          <p className="muted">Latest announcements from our medical network.</p>
        </div>
        {newsLoading && <p className="muted">Loading news...</p>}
        <div className="card-grid">
          {news.map((article) => (
            <article key={article.id} className="article-card article-card-rich">
              <small>{formatCategory(article.category)}</small>
              <h4>{article.title}</h4>
              <p>{article.summary || "No summary provided."}</p>
              <div className="meta-line">
                <span>{article.authorName ?? "NorthCare communications team"}</span>
                <Link to={`/knowledge/${article.slug}`}>Open</Link>
              </div>
            </article>
          ))}
          {!newsLoading && !news.length && <p className="muted">No news available yet.</p>}
        </div>
      </section>

      <section className="care-details">
        <article className="care-detail-card">
          <img src="/images/hospital-corridor.jpg" alt="Hospital interior corridor" />
          <div>
            <h3>Modern Clinical Environment</h3>
            <p>
              Purpose-built spaces for diagnostics, consultation and day-case procedures, designed around patient
              comfort and safety.
            </p>
          </div>
        </article>
        <article className="care-detail-card">
          <img src="/images/patient-care.jpg" alt="Doctor supporting patient care process" />
          <div>
            <h3>Continuity of Care</h3>
            <p>
              One team follows your journey from first consultation to follow-up treatment, with transparent history and
              outcome tracking.
            </p>
          </div>
        </article>
      </section>

      <section className="panel knowledge-panel">
        <div className="panel-head">
          <div>
            <h3>Medical encyclopedia</h3>
            <p className="muted">Evidence-based guidance from the NorthCare clinical knowledge team.</p>
          </div>
          <span className="badge">{result?.totalElements ?? 0} total</span>
        </div>

        <form className="row-form row-form-compact" onSubmit={onSearch}>
          <input
            value={queryInput}
            onChange={(e) => setQueryInput(e.target.value)}
            placeholder="Search by title, summary or tags"
          />
          <select
            value={category}
            onChange={(e) => {
              setCategory(e.target.value as KnowledgeCategory | "");
              setPage(0);
            }}
          >
            <option value="">All categories</option>
            {CATEGORIES.map((value) => (
              <option key={value} value={value}>
                {formatCategory(value)}
              </option>
            ))}
          </select>
          <button type="submit">Apply Filters</button>
        </form>

        {loading && <p className="muted">Loading articles...</p>}
        {error && <p className="error">{error}</p>}
        {!loading && !error && result?.content.length === 0 && (
          <p className="muted">No articles found for selected filters.</p>
        )}

        <div className="card-grid article-grid">
          {result?.content.map((article) => (
            <article key={article.id} className="article-card article-card-rich">
              <small>{formatCategory(article.category)}</small>
              <h4>{article.title}</h4>
              <p>{article.summary || "No summary provided."}</p>
              <div className="meta-line">
                <span>{article.authorName ?? "NorthCare editorial team"}</span>
                <Link to={`/knowledge/${article.slug}`}>Read Article</Link>
              </div>
            </article>
          ))}
        </div>

        {result && (
          <div className="pagination-bar">
            <button type="button" onClick={() => setPage((p) => Math.max(0, p - 1))} disabled={result.first}>
              Previous
            </button>
            <span>
              Page {result.number + 1} / {Math.max(1, result.totalPages)}
            </span>
            <button
              type="button"
              onClick={() => setPage((p) => p + 1)}
              disabled={result.last || result.totalPages === 0}
            >
              Next
            </button>
          </div>
        )}
      </section>
    </section>
  );
}
