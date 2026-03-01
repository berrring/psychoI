import { useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import type { Article } from "../../types";
import type { ArticleSection, TocItem } from "../../utils/knowledge";
import { splitTags } from "../../utils/knowledge";
import { ArticleContent } from "./ArticleContent";
import { RelatedArticles } from "./RelatedArticles";
import { TableOfContents } from "./TableOfContents";

interface ArticleLayoutProps {
  article: Article;
  lead: string;
  sections: ArticleSection[];
  toc: TocItem[];
  related: Article[];
  backHref: string;
  backScrollY?: number;
}

function formatDate(value: string | undefined): string | null {
  if (!value) return null;
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return null;
  return date.toLocaleDateString(undefined, {
    year: "numeric",
    month: "long",
    day: "numeric"
  });
}

export function ArticleLayout({
  article,
  lead,
  sections,
  toc,
  related,
  backHref,
  backScrollY
}: ArticleLayoutProps) {
  const [activeTocId, setActiveTocId] = useState(toc[0]?.id ?? "");
  const [tocOpenMobile, setTocOpenMobile] = useState(false);
  const [showToTop, setShowToTop] = useState(false);
  const [copyStatus, setCopyStatus] = useState<"idle" | "done" | "error">("idle");

  const articleDate = formatDate(article.publishedAt || article.createdAt);
  const articleTags = useMemo(() => splitTags(article.tags), [article.tags]);

  useEffect(() => {
    if (!toc.length) return;
    const headingElements = toc
      .map((item) => document.getElementById(item.id))
      .filter((element): element is HTMLElement => !!element);
    if (!headingElements.length) return;

    const observer = new IntersectionObserver(
      (entries) => {
        const visible = entries
          .filter((entry) => entry.isIntersecting)
          .sort((a, b) => b.intersectionRatio - a.intersectionRatio);
        if (visible[0]?.target.id) {
          setActiveTocId(visible[0].target.id);
        }
      },
      {
        rootMargin: "-120px 0px -55% 0px",
        threshold: [0.1, 0.3, 0.7]
      }
    );

    headingElements.forEach((element) => observer.observe(element));
    return () => observer.disconnect();
  }, [toc]);

  useEffect(() => {
    const onScroll = () => {
      setShowToTop(window.scrollY > 460);
    };
    window.addEventListener("scroll", onScroll, { passive: true });
    onScroll();
    return () => window.removeEventListener("scroll", onScroll);
  }, []);

  async function onCopyLink() {
    try {
      await navigator.clipboard.writeText(window.location.href);
      setCopyStatus("done");
      setTimeout(() => setCopyStatus("idle"), 1400);
    } catch {
      setCopyStatus("error");
      setTimeout(() => setCopyStatus("idle"), 1800);
    }
  }

  return (
    <section className="panel library-article-page">
      <nav className="library-breadcrumbs" aria-label="Breadcrumb">
        <Link to="/">Home</Link>
        <span>→</span>
        <Link to="/health-library">Health Library</Link>
        <span>→</span>
        <span>{article.category.replace(/_/g, " ")}</span>
        <span>→</span>
        <span>{article.title}</span>
      </nav>

      <header className="library-article-header">
        <div className="library-article-meta">
          <span className="library-category-pill">{article.category.replace(/_/g, " ")}</span>
          {article.authorName && <span>By {article.authorName}</span>}
          {articleDate && <span>{articleDate}</span>}
        </div>
        <h1>{article.title}</h1>
        {lead && <p className="library-article-lead">{lead}</p>}
        <div className="library-article-actions">
          <Link to={backHref} state={backScrollY ? { restoreScrollY: backScrollY } : undefined}>
            Back to library
          </Link>
          <button type="button" className="library-inline-btn" onClick={onCopyLink}>
            {copyStatus === "done" ? "Link copied" : copyStatus === "error" ? "Copy failed" : "Copy link"}
          </button>
        </div>
      </header>

      <div className="library-article-grid">
        <aside className="library-article-aside">
          <button
            type="button"
            className="library-toc-toggle"
            onClick={() => setTocOpenMobile((value) => !value)}
          >
            Contents {tocOpenMobile ? "−" : "+"}
          </button>
          <div className={tocOpenMobile ? "library-toc-shell library-toc-shell-open" : "library-toc-shell"}>
            <TableOfContents items={toc} activeId={activeTocId} />
          </div>
        </aside>

        <article className="library-article-main">
          <ArticleContent sections={sections} />

          {!!articleTags.length && (
            <footer className="library-article-tags">
              <h3>Tags</h3>
              <div className="library-tag-row">
                {articleTags.map((tag) => (
                  <span key={`${article.id}-${tag}`} className="library-tag-chip">
                    {tag}
                  </span>
                ))}
              </div>
            </footer>
          )}

          <RelatedArticles items={related} />
        </article>
      </div>

      {showToTop && (
        <button
          type="button"
          className="library-to-top"
          onClick={() => window.scrollTo({ top: 0, behavior: "smooth" })}
        >
          To top
        </button>
      )}
    </section>
  );
}
