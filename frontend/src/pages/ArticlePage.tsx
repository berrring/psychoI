import { useEffect, useMemo, useState } from "react";
import { Link, useLocation, useParams } from "react-router-dom";
import { HttpError, apiRequest, toErrorMessage } from "../api";
import { ArticleLayout } from "../components/knowledge/ArticleLayout";
import type { Article, PageResponse } from "../types";
import {
  buildTocFromHeadings,
  extractLead,
  parseArticleSections,
  splitTags,
  type TocItem
} from "../utils/knowledge";

interface ArticleNavigationState {
  from?: string;
  scrollY?: number;
}

function buildRelatedArticles(article: Article, candidates: Article[]): Article[] {
  const baseTags = new Set(splitTags(article.tags).map((tag) => tag.toLowerCase()));

  return candidates
    .filter((candidate) => candidate.slug !== article.slug)
    .map((candidate) => {
      const candidateTags = splitTags(candidate.tags).map((tag) => tag.toLowerCase());
      const tagOverlap = candidateTags.filter((tag) => baseTags.has(tag)).length;
      const categoryBonus = candidate.category === article.category ? 3 : 0;
      const score = categoryBonus + tagOverlap * 2;
      return { candidate, score };
    })
    .filter((item) => item.score > 0 || item.candidate.category === article.category)
    .sort((a, b) => {
      if (b.score !== a.score) return b.score - a.score;
      const bTime = new Date(b.candidate.publishedAt || b.candidate.createdAt).getTime();
      const aTime = new Date(a.candidate.publishedAt || a.candidate.createdAt).getTime();
      return bTime - aTime;
    })
    .slice(0, 4)
    .map((item) => item.candidate);
}

function buildToc(content: string): TocItem[] {
  const markdownToc = buildTocFromHeadings(content);
  if (markdownToc.length > 0) return markdownToc;
  return [{ id: "overview", text: "Overview", level: 2 }];
}

function ArticleSkeleton() {
  return (
    <section className="panel library-article-page">
      <div className="library-skeleton-line library-skeleton-line-short" />
      <div className="library-skeleton-title" />
      <div className="library-skeleton-line" />
      <div className="library-skeleton-line library-skeleton-line-wide" />
      <div className="library-skeleton-block" />
      <div className="library-skeleton-block" />
      <div className="library-skeleton-block" />
    </section>
  );
}

export function ArticlePage() {
  const { slug } = useParams<{ slug: string }>();
  const location = useLocation();
  const navState = location.state as ArticleNavigationState | null;

  const [article, setArticle] = useState<Article | null>(null);
  const [related, setRelated] = useState<Article[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [notFound, setNotFound] = useState(false);

  useEffect(() => {
    if (!slug) return;
    window.scrollTo({ top: 0, behavior: "auto" });
    let cancelled = false;

    async function load() {
      setLoading(true);
      setError(null);
      setNotFound(false);
      try {
        const [articleData, articlePage] = await Promise.all([
          apiRequest<Article>(`/public/knowledge/articles/${slug}`),
          apiRequest<PageResponse<Article>>("/public/knowledge/articles", { method: "GET" }, undefined, {
            page: 0,
            size: 100
          })
        ]);

        if (cancelled) return;
        setArticle(articleData);
        setRelated(buildRelatedArticles(articleData, articlePage.content));
      } catch (e) {
        if (cancelled) return;
        if (e instanceof HttpError && e.status === 404) {
          setNotFound(true);
          setArticle(null);
          return;
        }
        setError(toErrorMessage(e));
      } finally {
        if (!cancelled) setLoading(false);
      }
    }

    load();
    return () => {
      cancelled = true;
    };
  }, [slug]);

  const sections = useMemo(() => parseArticleSections(article?.content ?? ""), [article?.content]);
  const toc = useMemo(() => buildToc(article?.content ?? ""), [article?.content]);
  const lead = useMemo(() => extractLead(article?.summary, article?.content ?? ""), [article?.summary, article?.content]);

  const backHref = navState?.from || "/health-library";
  const storedScrollY = Number(sessionStorage.getItem("knowledge.library.scrollY") ?? NaN);
  const backScrollY = navState?.scrollY ?? (Number.isFinite(storedScrollY) ? storedScrollY : undefined);

  if (loading) return <ArticleSkeleton />;

  if (notFound) {
    return (
      <section className="panel panel-narrow">
        <h2>Article not found</h2>
        <p className="muted">The requested article slug does not exist or has not been published.</p>
        <p>
          <Link to="/health-library">Back to library</Link>
        </p>
      </section>
    );
  }

  if (error) {
    return (
      <section className="panel panel-narrow">
        <h2>Unable to load article</h2>
        <p className="error">{error}</p>
        <p>
          <Link to="/health-library">Back to library</Link>
        </p>
      </section>
    );
  }

  if (!article) return null;

  return (
    <ArticleLayout
      article={article}
      lead={lead}
      sections={sections}
      toc={toc}
      related={related}
      backHref={backHref}
      backScrollY={backScrollY}
    />
  );
}
