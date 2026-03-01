import type { TocItem } from "../../utils/knowledge";

interface TableOfContentsProps {
  items: TocItem[];
  activeId: string;
}

export function TableOfContents({ items, activeId }: TableOfContentsProps) {
  if (!items.length) return null;

  return (
    <nav className="library-toc" aria-label="Table of contents">
      <h3>Contents</h3>
      <ul>
        {items.map((item) => (
          <li key={item.id} className={item.level === 3 ? "library-toc-item-level-3" : ""}>
            <a
              href={`#${item.id}`}
              className={activeId === item.id ? "library-toc-link library-toc-link-active" : "library-toc-link"}
            >
              {item.text}
            </a>
          </li>
        ))}
      </ul>
    </nav>
  );
}
