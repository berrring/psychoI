import { FormEvent, useEffect, useState } from "react";
import { apiRequest, toErrorMessage } from "../api";
import { useAuth } from "../auth";
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

export function KnowledgeAdminPage() {
  const { token } = useAuth();
  const [articles, setArticles] = useState<Article[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [selectedId, setSelectedId] = useState<number | null>(null);

  const [form, setForm] = useState({
    slug: "",
    title: "",
    summary: "",
    content: "",
    category: "PREVENTION" as KnowledgeCategory,
    tags: "",
    published: true
  });

  async function loadArticles() {
    if (!token) return;
    const data = await apiRequest<PageResponse<Article>>(
      "/knowledge/articles",
      { method: "GET" },
      token,
      { page: 0, size: 100 }
    );
    setArticles(data.content);
  }

  useEffect(() => {
    let cancelled = false;
    async function init() {
      if (!token) return;
      setLoading(true);
      setError(null);
      try {
        await loadArticles();
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

  async function createArticle(event: FormEvent) {
    event.preventDefault();
    if (!token) return;
    setError(null);
    try {
      await apiRequest<Article>(
        "/knowledge/articles",
        { method: "POST", body: JSON.stringify(form) },
        token
      );
      await loadArticles();
      setForm({
        slug: "",
        title: "",
        summary: "",
        content: "",
        category: "PREVENTION",
        tags: "",
        published: true
      });
    } catch (e) {
      setError(toErrorMessage(e));
    }
  }

  async function togglePublished(article: Article) {
    if (!token) return;
    setSelectedId(article.id);
    setError(null);
    try {
      await apiRequest<Article>(
        `/knowledge/articles/${article.id}`,
        {
          method: "PATCH",
          body: JSON.stringify({
            published: !article.published
          })
        },
        token
      );
      await loadArticles();
    } catch (e) {
      setError(toErrorMessage(e));
    } finally {
      setSelectedId(null);
    }
  }

  return (
    <section className="panel">
      <h2>Knowledge Management</h2>
      <p className="muted">Staff endpoints: <code>/api/v1/knowledge/articles</code></p>

      {loading && <p className="muted">Loading articles...</p>}
      {error && <p className="error">{error}</p>}

      <div className="split-grid">
        <div className="panel-sub">
          <h3>Create Article</h3>
          <form className="form-grid" onSubmit={createArticle}>
            <input
              placeholder="Slug"
              value={form.slug}
              onChange={(e) => setForm((v) => ({ ...v, slug: e.target.value }))}
              required
            />
            <input
              placeholder="Title"
              value={form.title}
              onChange={(e) => setForm((v) => ({ ...v, title: e.target.value }))}
              required
            />
            <textarea
              placeholder="Summary"
              value={form.summary}
              onChange={(e) => setForm((v) => ({ ...v, summary: e.target.value }))}
            />
            <textarea
              placeholder="Content"
              value={form.content}
              onChange={(e) => setForm((v) => ({ ...v, content: e.target.value }))}
              required
            />
            <select
              value={form.category}
              onChange={(e) =>
                setForm((v) => ({ ...v, category: e.target.value as KnowledgeCategory }))
              }
            >
              {CATEGORIES.map((cat) => (
                <option key={cat} value={cat}>
                  {cat}
                </option>
              ))}
            </select>
            <input
              placeholder="Tags"
              value={form.tags}
              onChange={(e) => setForm((v) => ({ ...v, tags: e.target.value }))}
            />
            <label className="checkbox-row">
              <input
                type="checkbox"
                checked={form.published}
                onChange={(e) => setForm((v) => ({ ...v, published: e.target.checked }))}
              />
              Publish immediately
            </label>
            <button type="submit">Create Article</button>
          </form>
        </div>

        <div className="panel-sub">
          <h3>Existing Articles</h3>
          <ul className="list">
            {articles.map((article) => (
              <li key={article.id}>
                <strong>{article.title}</strong> ({article.category})<br />
                <span>{article.slug}</span>
                <div className="inline-actions">
                  <button
                    type="button"
                    onClick={() => togglePublished(article)}
                    disabled={selectedId === article.id}
                  >
                    {article.published ? "Unpublish" : "Publish"}
                  </button>
                </div>
              </li>
            ))}
          </ul>
        </div>
      </div>
    </section>
  );
}
