import { FormEvent, useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import { apiRequest, toErrorMessage } from "../api";
import { useAuth } from "../auth";
import type { Article, DashboardSummary, PageResponse } from "../types";

function slugify(input: string): string {
  return input
    .toLowerCase()
    .trim()
    .replace(/[^a-z0-9]+/g, "-")
    .replace(/^-+|-+$/g, "");
}

export function AdminWorkspacePage() {
  const { token } = useAuth();
  const [summary, setSummary] = useState<DashboardSummary | null>(null);
  const [articles, setArticles] = useState<Article[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);
  const [selectedNewsId, setSelectedNewsId] = useState<number | null>(null);

  const [form, setForm] = useState({
    slug: "",
    title: "",
    summary: "",
    content: "",
    tags: "news,bering,update",
    published: true
  });

  const [editForm, setEditForm] = useState({
    title: "",
    summary: "",
    content: "",
    tags: "",
    published: true
  });

  const newsArticles = useMemo(() => articles.filter((item) => item.category === "NEWS"), [articles]);

  async function loadAdminWorkspace() {
    if (!token) return;
    const [dashboard, articlePage] = await Promise.all([
      apiRequest<DashboardSummary>("/dashboard/summary", { method: "GET" }, token),
      apiRequest<PageResponse<Article>>("/knowledge/articles", { method: "GET" }, token, { page: 0, size: 120 })
    ]);
    setSummary(dashboard);
    setArticles(articlePage.content);
  }

  useEffect(() => {
    let cancelled = false;
    async function init() {
      if (!token) return;
      setLoading(true);
      setError(null);
      try {
        await loadAdminWorkspace();
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

  async function onCreateNews(event: FormEvent) {
    event.preventDefault();
    if (!token) return;
    setSaving(true);
    setError(null);
    try {
      await apiRequest<Article>(
        "/knowledge/articles",
        {
          method: "POST",
          body: JSON.stringify({
            slug: form.slug || slugify(form.title),
            title: form.title,
            summary: form.summary || null,
            content: form.content,
            category: "NEWS",
            tags: form.tags || null,
            published: form.published
          })
        },
        token
      );
      setForm({
        slug: "",
        title: "",
        summary: "",
        content: "",
        tags: "news,bering,update",
        published: true
      });
      await loadAdminWorkspace();
    } catch (e) {
      setError(toErrorMessage(e));
    } finally {
      setSaving(false);
    }
  }

  function onSelectNews(article: Article) {
    setSelectedNewsId(article.id);
    setEditForm({
      title: article.title,
      summary: article.summary || "",
      content: article.content,
      tags: article.tags || "",
      published: article.published
    });
  }

  async function onUpdateNews(event: FormEvent) {
    event.preventDefault();
    if (!token || !selectedNewsId) return;
    setSaving(true);
    setError(null);
    try {
      await apiRequest<Article>(
        `/knowledge/articles/${selectedNewsId}`,
        {
          method: "PATCH",
          body: JSON.stringify({
            title: editForm.title,
            summary: editForm.summary || null,
            content: editForm.content,
            tags: editForm.tags || null,
            published: editForm.published
          })
        },
        token
      );
      await loadAdminWorkspace();
    } catch (e) {
      setError(toErrorMessage(e));
    } finally {
      setSaving(false);
    }
  }

  async function onTogglePublish(article: Article) {
    if (!token) return;
    setSaving(true);
    setError(null);
    try {
      await apiRequest<Article>(
        `/knowledge/articles/${article.id}`,
        {
          method: "PATCH",
          body: JSON.stringify({ published: !article.published })
        },
        token
      );
      await loadAdminWorkspace();
    } catch (e) {
      setError(toErrorMessage(e));
    } finally {
      setSaving(false);
    }
  }

  return (
    <section className="workspace">
      <div className="workspace-hero workspace-hero-admin">
        <p className="eyebrow">Admin App</p>
        <h2>Operations and News Control Center</h2>
        <p>Monitor platform KPIs and manage public clinic news from one admin interface.</p>
      </div>

      {loading && <p className="muted">Loading admin workspace...</p>}
      {error && <p className="error">{error}</p>}

      {summary && (
        <div className="kpi-grid">
          <article className="kpi-card">
            <span>Clinics</span>
            <strong>{summary.clinics}</strong>
          </article>
          <article className="kpi-card">
            <span>Doctors</span>
            <strong>{summary.doctors}</strong>
          </article>
          <article className="kpi-card">
            <span>Patients</span>
            <strong>{summary.patients}</strong>
          </article>
          <article className="kpi-card">
            <span>Appointments</span>
            <strong>{summary.appointments}</strong>
          </article>
          <article className="kpi-card">
            <span>Knowledge Items</span>
            <strong>{summary.articles}</strong>
          </article>
        </div>
      )}

      <div className="workspace-grid">
        <article className="panel workspace-panel">
          <h3>Create News</h3>
          <p className="muted">Role-focused content endpoint: <code>/api/v1/knowledge/articles</code></p>
          <form className="form-grid" onSubmit={onCreateNews}>
            <label>
              News Title
              <input
                value={form.title}
                onChange={(e) => setForm((value) => ({ ...value, title: e.target.value }))}
                required
              />
            </label>
            <label>
              Slug
              <input
                value={form.slug}
                onChange={(e) => setForm((value) => ({ ...value, slug: e.target.value }))}
                placeholder="Auto generated from title if empty"
              />
            </label>
            <label>
              Summary
              <textarea
                value={form.summary}
                onChange={(e) => setForm((value) => ({ ...value, summary: e.target.value }))}
              />
            </label>
            <label>
              Content
              <textarea
                value={form.content}
                onChange={(e) => setForm((value) => ({ ...value, content: e.target.value }))}
                required
              />
            </label>
            <label>
              Tags
              <input
                value={form.tags}
                onChange={(e) => setForm((value) => ({ ...value, tags: e.target.value }))}
              />
            </label>
            <label className="checkbox-row">
              <input
                type="checkbox"
                checked={form.published}
                onChange={(e) => setForm((value) => ({ ...value, published: e.target.checked }))}
              />
              Publish immediately
            </label>
            <button type="submit" disabled={saving}>
              {saving ? "Saving..." : "Create News"}
            </button>
          </form>
        </article>

        <article className="panel workspace-panel">
          <h3>Admin Shortcuts</h3>
          <ul className="list">
            <li>
              <strong>Clinic structure</strong>
              <p className="muted">Update clinics, departments and services.</p>
              <Link to="/clinics">Open Clinics</Link>
            </li>
            <li>
              <strong>Users and roles</strong>
              <p className="muted">Review doctors and patient accounts.</p>
              <Link to="/users">Open Users</Link>
            </li>
            <li>
              <strong>Audit trail</strong>
              <p className="muted">Inspect historical operations and changes.</p>
              <Link to="/audit">Open Audit</Link>
            </li>
          </ul>
        </article>

        <article className="panel workspace-panel workspace-panel-wide">
          <h3>Published and Draft News</h3>
          <ul className="list">
            {newsArticles.map((article) => (
              <li key={article.id}>
                <strong>{article.title}</strong>
                <p className="muted">{article.summary || "No summary"}</p>
                <div className="inline-actions">
                  <button type="button" className="ghost-btn" onClick={() => onSelectNews(article)}>
                    Edit
                  </button>
                  <button type="button" className="ghost-btn" onClick={() => onTogglePublish(article)} disabled={saving}>
                    {article.published ? "Unpublish" : "Publish"}
                  </button>
                  <span className={`status-chip ${article.published ? "status-chip-completed" : "status-chip-cancelled"}`}>
                    {article.published ? "PUBLISHED" : "DRAFT"}
                  </span>
                </div>
              </li>
            ))}
            {!newsArticles.length && <li className="muted">No news entries yet.</li>}
          </ul>
        </article>

        <article className="panel workspace-panel workspace-panel-wide">
          <h3>Edit Selected News</h3>
          {!selectedNewsId && <p className="muted">Select a news item above to edit fields.</p>}
          {selectedNewsId && (
            <form className="form-grid" onSubmit={onUpdateNews}>
              <label>
                Title
                <input
                  value={editForm.title}
                  onChange={(e) => setEditForm((value) => ({ ...value, title: e.target.value }))}
                  required
                />
              </label>
              <label>
                Summary
                <textarea
                  value={editForm.summary}
                  onChange={(e) => setEditForm((value) => ({ ...value, summary: e.target.value }))}
                />
              </label>
              <label>
                Content
                <textarea
                  value={editForm.content}
                  onChange={(e) => setEditForm((value) => ({ ...value, content: e.target.value }))}
                  required
                />
              </label>
              <label>
                Tags
                <input
                  value={editForm.tags}
                  onChange={(e) => setEditForm((value) => ({ ...value, tags: e.target.value }))}
                />
              </label>
              <label className="checkbox-row">
                <input
                  type="checkbox"
                  checked={editForm.published}
                  onChange={(e) => setEditForm((value) => ({ ...value, published: e.target.checked }))}
                />
                Published
              </label>
              <button type="submit" disabled={saving}>
                {saving ? "Updating..." : "Save News Changes"}
              </button>
            </form>
          )}
        </article>
      </div>
    </section>
  );
}
