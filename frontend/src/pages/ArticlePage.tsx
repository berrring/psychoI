import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { apiRequest, toErrorMessage } from "../api";
import type { Article } from "../types";

export function ArticlePage() {
  const { slug } = useParams<{ slug: string }>();
  const [article, setArticle] = useState<Article | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!slug) return;
    let cancelled = false;
    async function load() {
      setLoading(true);
      setError(null);
      try {
        const data = await apiRequest<Article>(`/public/knowledge/articles/${slug}`);
        if (!cancelled) setArticle(data);
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
  }, [slug]);

  return (
    <section className="panel">
      <p>
        <Link to="/">Back to encyclopedia</Link>
      </p>
      {loading && <p className="muted">Loading article...</p>}
      {error && <p className="error">{error}</p>}
      {article && (
        <>
          <h2>{article.title}</h2>
          <p className="muted">
            Category: {article.category} | Author: {article.authorName ?? "Unknown"}
          </p>
          {article.summary && <p className="lead">{article.summary}</p>}
          <div className="article-body">{article.content}</div>
          <p className="muted">Tags: {article.tags || "No tags"}</p>
        </>
      )}
    </section>
  );
}
