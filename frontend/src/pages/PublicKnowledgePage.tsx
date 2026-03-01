import { FormEvent, useEffect, useMemo, useState } from "react";
import { useLocation, useNavigate, useSearchParams } from "react-router-dom";
import { apiRequest, toErrorMessage } from "../api";
import { LibraryCard } from "../components/knowledge/LibraryCard";
import type { Article, KnowledgeCategory, PageResponse } from "../types";
import { splitTags } from "../utils/knowledge";

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

type SortOption = "newest" | "relevant";

function formatCategory(category: string): string {
  return category.replace(/_/g, " ");
}

function getRelevanceScore(article: Article, query: string): number {
  if (!query.trim()) return 0;
  const tokens = query.toLowerCase().split(/\s+/).filter(Boolean);
  if (!tokens.length) return 0;

  const title = article.title.toLowerCase();
  const summary = (article.summary || "").toLowerCase();
  const tags = (article.tags || "").toLowerCase();

  let score = 0;
  for (const token of tokens) {
    if (title.includes(token)) score += 4;
    if (summary.includes(token)) score += 2;
    if (tags.includes(token)) score += 1;
  }
  return score;
}

function LibrarySkeleton() {
  return (
    <div className="library-card-grid">
      {Array.from({ length: 6 }).map((_, index) => (
        <article key={`library-skeleton-${index}`} className="library-card library-card-skeleton">
          <div className="library-skeleton-line library-skeleton-line-short" />
          <div className="library-skeleton-title" />
          <div className="library-skeleton-line" />
          <div className="library-skeleton-line library-skeleton-line-wide" />
          <div className="library-skeleton-chip-row">
            <span className="library-skeleton-chip" />
            <span className="library-skeleton-chip" />
          </div>
        </article>
      ))}
    </div>
  );
}

export function PublicKnowledgePage() {
  const [searchParams, setSearchParams] = useSearchParams();
  const location = useLocation();
  const navigate = useNavigate();

  const query = searchParams.get("q") ?? "";
  const category = (searchParams.get("category") ?? "") as KnowledgeCategory | "";
  const tag = searchParams.get("tag") ?? "";
  const sort = (searchParams.get("sort") ?? "newest") as SortOption;
  const page = Math.max(0, Number(searchParams.get("page") ?? 0) || 0);

  const [queryInput, setQueryInput] = useState(query);
  const [result, setResult] = useState<PageResponse<Article> | null>(null);
  const [tagOptions, setTagOptions] = useState<string[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    setQueryInput(query);
  }, [query]);

  useEffect(() => {
    const restoreScrollY = (location.state as { restoreScrollY?: number } | null)?.restoreScrollY;
    if (typeof restoreScrollY !== "number") return;
    requestAnimationFrame(() => {
      window.scrollTo({ top: restoreScrollY, behavior: "auto" });
      navigate(`${location.pathname}${location.search}`, { replace: true, state: null });
    });
  }, [location.pathname, location.search, location.state, navigate]);

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
            size: 12
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
    async function loadTags() {
      try {
        const tagsSource = await apiRequest<PageResponse<Article>>(
          "/public/knowledge/articles",
          { method: "GET" },
          undefined,
          { page: 0, size: 100 }
        );
        if (cancelled) return;
        const uniqueTags = new Set<string>();
        tagsSource.content.forEach((article) => {
          splitTags(article.tags).forEach((value) => uniqueTags.add(value));
        });
        setTagOptions(Array.from(uniqueTags).sort((a, b) => a.localeCompare(b)));
      } catch {
        if (!cancelled) setTagOptions([]);
      }
    }
    loadTags();
    return () => {
      cancelled = true;
    };
  }, []);

  function updateSearchParams(updates: Record<string, string | number | null>) {
    const next = new URLSearchParams(searchParams);
    Object.entries(updates).forEach(([key, value]) => {
      if (value === null || value === "" || value === 0) {
        next.delete(key);
      } else {
        next.set(key, String(value));
      }
    });
    setSearchParams(next);
  }

  function onSearchSubmit(event: FormEvent) {
    event.preventDefault();
    updateSearchParams({ q: queryInput.trim(), page: 0 });
  }

  const visibleArticles = useMemo(() => {
    const source = result?.content ?? [];

    const tagFiltered = tag
      ? source.filter((article) =>
          splitTags(article.tags)
            .map((value) => value.toLowerCase())
            .includes(tag.toLowerCase())
        )
      : source;

    const sorted = [...tagFiltered];
    if (sort === "relevant") {
      sorted.sort((a, b) => {
        const scoreDiff = getRelevanceScore(b, query) - getRelevanceScore(a, query);
        if (scoreDiff !== 0) return scoreDiff;
        const bTime = new Date(b.publishedAt || b.createdAt).getTime();
        const aTime = new Date(a.publishedAt || a.createdAt).getTime();
        return bTime - aTime;
      });
      return sorted;
    }

    sorted.sort((a, b) => {
      const bTime = new Date(b.publishedAt || b.createdAt).getTime();
      const aTime = new Date(a.publishedAt || a.createdAt).getTime();
      return bTime - aTime;
    });
    return sorted;
  }, [result?.content, tag, sort, query]);

  return (
    <section className="panel library-page">
      <header className="library-header">
        <p className="eyebrow">Bering Knowledge Base</p>
        <h2>Health Library</h2>
        <p className="muted">
          Structured, readable clinical reference articles with category filters and textbook-style navigation.
        </p>
      </header>

      <form className="library-controls" onSubmit={onSearchSubmit}>
        <label>
          Search
          <input
            value={queryInput}
            onChange={(event) => setQueryInput(event.target.value)}
            placeholder="Search conditions, diagnostics, treatment protocols"
          />
        </label>

        <label>
          Category
          <select
            value={category}
            onChange={(event) =>
              updateSearchParams({
                category: event.target.value,
                page: 0
              })
            }
          >
            <option value="">All categories</option>
            {CATEGORIES.map((value) => (
              <option key={value} value={value}>
                {formatCategory(value)}
              </option>
            ))}
          </select>
        </label>

        <label>
          Tag
          <select
            value={tag}
            onChange={(event) =>
              updateSearchParams({
                tag: event.target.value,
                page: 0
              })
            }
          >
            <option value="">All tags</option>
            {tagOptions.map((value) => (
              <option key={value} value={value}>
                {value}
              </option>
            ))}
          </select>
        </label>

        <label>
          Sort
          <select
            value={sort}
            onChange={(event) =>
              updateSearchParams({
                sort: event.target.value as SortOption
              })
            }
          >
            <option value="newest">Newest</option>
            <option value="relevant">Most relevant</option>
          </select>
        </label>

        <div className="library-control-actions">
          <button type="submit">Apply</button>
          <button
            type="button"
            className="library-inline-btn"
            onClick={() => {
              setQueryInput("");
              setSearchParams(new URLSearchParams());
            }}
          >
            Reset
          </button>
        </div>
      </form>

      <p className="muted">
        Public endpoint: <code>/api/v1/public/knowledge/articles</code>
      </p>

      {loading && <LibrarySkeleton />}
      {error && <p className="error">{error}</p>}

      {!loading && !error && (
        <>
          {!visibleArticles.length && (
            <div className="library-empty">
              <h3>No articles found</h3>
              <p className="muted">Try broadening your query or removing one of the filters.</p>
            </div>
          )}

          <div className="library-card-grid">
            {visibleArticles.map((article) => (
              <LibraryCard key={article.id} article={article} from={location.pathname + location.search} />
            ))}
          </div>

          {result && (
            <div className="pagination-bar">
              <button
                type="button"
                onClick={() => updateSearchParams({ page: Math.max(0, page - 1) })}
                disabled={result.first}
              >
                Previous
              </button>
              <span>
                Page {result.number + 1} / {Math.max(1, result.totalPages)}
              </span>
              <button
                type="button"
                onClick={() => updateSearchParams({ page: page + 1 })}
                disabled={result.last || result.totalPages === 0}
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
