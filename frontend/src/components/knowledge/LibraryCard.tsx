import { Link } from "react-router-dom";
import type { Article } from "../../types";
import { estimateReadingTime, splitTags } from "../../utils/knowledge";

interface LibraryCardProps {
  article: Article;
  from: string;
}

function toExcerpt(article: Article): string {
  if (article.summary?.trim()) return article.summary.trim();
  return article.content.replace(/\s+/g, " ").trim().slice(0, 180);
}

export function LibraryCard({ article, from }: LibraryCardProps) {
  const tags = splitTags(article.tags);

  return (
    <article className="library-card">
      <div className="library-card-meta">
        <span>{article.category.replace(/_/g, " ")}</span>
        <span>{estimateReadingTime(article.content)} min read</span>
      </div>

      <h3>
        <Link
          to={`/knowledge/${article.slug}`}
          state={{ from, scrollY: window.scrollY }}
          onClick={() => sessionStorage.setItem("knowledge.library.scrollY", String(window.scrollY))}
        >
          {article.title}
        </Link>
      </h3>

      <p>{toExcerpt(article)}</p>

      <div className="library-tag-row">
        {tags.slice(0, 4).map((tag) => (
          <span key={`${article.id}-${tag}`} className="library-tag-chip">
            {tag}
          </span>
        ))}
      </div>
    </article>
  );
}
