import type { ReactNode } from "react";
import type { ArticleSection } from "../../utils/knowledge";

interface ArticleContentProps {
  sections: ArticleSection[];
}

function linkifyText(input: string): ReactNode[] {
  const urlPattern = /(https?:\/\/[^\s]+)/g;
  const chunks = input.split(urlPattern);
  return chunks.map((chunk, index) => {
    if (/^https?:\/\//.test(chunk)) {
      return (
        <a key={`${chunk}-${index}`} href={chunk} target="_blank" rel="noreferrer">
          {chunk}
        </a>
      );
    }
    return <span key={`${chunk}-${index}`}>{chunk}</span>;
  });
}

function calloutLabel(tone: "note" | "tip" | "warning"): string {
  if (tone === "tip") return "Tip";
  if (tone === "warning") return "Warning";
  return "Note";
}

export function ArticleContent({ sections }: ArticleContentProps) {
  return (
    <div className="library-article-content">
      {sections.map((section) => {
        const HeadingTag = section.level === 3 ? "h3" : "h2";
        return (
          <section key={section.id} id={section.id} data-toc-id={section.id} className="library-article-section">
            <HeadingTag>{section.title}</HeadingTag>

            {section.blocks.map((block, index) => {
              if (block.type === "paragraph") {
                return <p key={`${section.id}-paragraph-${index}`}>{linkifyText(block.text)}</p>;
              }

              if (block.type === "list") {
                if (block.ordered) {
                  return (
                    <ol key={`${section.id}-list-${index}`}>
                      {block.items.map((item, itemIndex) => (
                        <li key={`${section.id}-ol-${index}-${itemIndex}`}>{item}</li>
                      ))}
                    </ol>
                  );
                }
                return (
                  <ul key={`${section.id}-list-${index}`}>
                    {block.items.map((item, itemIndex) => (
                      <li key={`${section.id}-ul-${index}-${itemIndex}`}>{item}</li>
                    ))}
                  </ul>
                );
              }

              return (
                <aside
                  key={`${section.id}-callout-${index}`}
                  className={`library-callout library-callout-${block.tone}`}
                  role="note"
                >
                  <strong>{calloutLabel(block.tone)}</strong>
                  <p>{block.text}</p>
                </aside>
              );
            })}
          </section>
        );
      })}
    </div>
  );
}
