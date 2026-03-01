import { Link, useLocation } from "react-router-dom";
import type { Article } from "../../types";
import { estimateReadingTime, splitTags } from "../../utils/knowledge";

interface RelatedArticlesProps {
  items: Article[];
}

export function RelatedArticles({ items }: RelatedArticlesProps) {
  const location = useLocation();

  if (!items.length) return null;

  return (
    <section className="library-related">
      <h3>Related articles</h3>
      <div className="library-related-grid">
        {items.map((item) => (
          <article key={item.id} className="library-related-card">
            <p className="library-related-meta">
              <span>{item.category.replace(/_/g, " ")}</span>
              <span>{estimateReadingTime(item.content)} min read</span>
            </p>
            <h4>
              <Link to={`/knowledge/${item.slug}`} state={{ from: location.pathname + location.search }}>
                {item.title}
              </Link>
            </h4>
            <p>{item.summary || item.content.slice(0, 150)}</p>
            <div className="library-tag-row">
              {splitTags(item.tags)
                .slice(0, 3)
                .map((tag) => (
                  <span key={`${item.id}-${tag}`} className="library-tag-chip">
                    {tag}
                  </span>
                ))}
            </div>
          </article>
        ))}
      </div>
    </section>
  );
}
