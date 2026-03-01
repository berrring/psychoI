export interface TocItem {
  id: string;
  text: string;
  level: 2 | 3;
}

export type ArticleCalloutTone = "note" | "tip" | "warning";

export type ArticleBlock =
  | { type: "paragraph"; text: string }
  | { type: "list"; ordered: boolean; items: string[] }
  | { type: "callout"; tone: ArticleCalloutTone; text: string };

export interface ArticleSection {
  id: string;
  title: string;
  level: 2 | 3;
  blocks: ArticleBlock[];
}

const HEADING_MARKDOWN_PATTERN = /^(#{2,3})\s+(.+)$/;
const LIST_UNORDERED_PATTERN = /^[-*]\s+(.+)$/;
const LIST_ORDERED_PATTERN = /^\d+[.)]\s+(.+)$/;
const CALLOUT_PATTERN = /^(note|tip|warning)\s*:\s*(.+)$/i;
const WORD_PATTERN = /[A-Za-z0-9]+(?:['-][A-Za-z0-9]+)*/g;

export function slugifyHeading(input: string): string {
  return input
    .toLowerCase()
    .trim()
    .replace(/[^a-z0-9\s-]/g, "")
    .replace(/\s+/g, "-")
    .replace(/-+/g, "-")
    .replace(/^-|-$/g, "");
}

function stripHtml(input: string): string {
  return input.replace(/<[^>]*>/g, "").trim();
}

function pushUniqueTocItem(
  target: TocItem[],
  rawText: string,
  level: 2 | 3,
  usedIds: Map<string, number>
) {
  const text = stripHtml(rawText);
  if (!text) return;
  const baseId = slugifyHeading(text) || "section";
  const index = usedIds.get(baseId) ?? 0;
  usedIds.set(baseId, index + 1);
  const id = index === 0 ? baseId : `${baseId}-${index + 1}`;
  target.push({ id, text, level });
}

export function buildTocFromHeadings(source: string): TocItem[] {
  const toc: TocItem[] = [];
  const usedIds = new Map<string, number>();
  if (!source.trim()) return toc;

  const htmlHeadingPattern = /<h([23])[^>]*>(.*?)<\/h\1>/gims;
  let htmlMatch: RegExpExecArray | null = htmlHeadingPattern.exec(source);
  while (htmlMatch) {
    const level = Number(htmlMatch[1]) as 2 | 3;
    pushUniqueTocItem(toc, htmlMatch[2], level, usedIds);
    htmlMatch = htmlHeadingPattern.exec(source);
  }

  if (toc.length > 0) return toc;

  const lines = source.replace(/\r\n/g, "\n").split("\n");
  for (const line of lines) {
    const heading = line.match(HEADING_MARKDOWN_PATTERN);
    if (!heading) continue;
    const level = heading[1].length as 2 | 3;
    pushUniqueTocItem(toc, heading[2], level, usedIds);
  }

  return toc;
}

function flushParagraph(buffer: string[], blocks: ArticleBlock[]) {
  if (buffer.length === 0) return;
  blocks.push({
    type: "paragraph",
    text: buffer.join(" ").replace(/\s+/g, " ").trim()
  });
  buffer.length = 0;
}

function flushList(
  ordered: boolean | null,
  items: string[],
  blocks: ArticleBlock[]
): { ordered: boolean | null; items: string[] } {
  if (ordered !== null && items.length > 0) {
    blocks.push({ type: "list", ordered, items: [...items] });
  }
  return { ordered: null, items: [] };
}

export function parseArticleSections(content: string): ArticleSection[] {
  const normalized = content.replace(/\r\n/g, "\n").trim();
  if (!normalized) {
    return [
      {
        id: "overview",
        title: "Overview",
        level: 2,
        blocks: []
      }
    ];
  }

  const sections: ArticleSection[] = [
    {
      id: "overview",
      title: "Overview",
      level: 2,
      blocks: []
    }
  ];

  const usedIds = new Map<string, number>([["overview", 1]]);
  const lines = normalized.split("\n");

  let currentSection = sections[0];
  let paragraphBuffer: string[] = [];
  let listOrdered: boolean | null = null;
  let listItems: string[] = [];

  const flushCurrentBuffers = () => {
    flushParagraph(paragraphBuffer, currentSection.blocks);
    const flushed = flushList(listOrdered, listItems, currentSection.blocks);
    listOrdered = flushed.ordered;
    listItems = flushed.items;
  };

  for (const rawLine of lines) {
    const line = rawLine.trim();

    const heading = line.match(HEADING_MARKDOWN_PATTERN);
    if (heading) {
      flushCurrentBuffers();
      const level = heading[1].length as 2 | 3;
      const title = heading[2].trim();
      const baseId = slugifyHeading(title) || "section";
      const currentIndex = usedIds.get(baseId) ?? 0;
      usedIds.set(baseId, currentIndex + 1);
      const id = currentIndex === 0 ? baseId : `${baseId}-${currentIndex + 1}`;
      currentSection = { id, title, level, blocks: [] };
      sections.push(currentSection);
      continue;
    }

    if (!line) {
      flushCurrentBuffers();
      continue;
    }

    const callout = line.match(CALLOUT_PATTERN);
    if (callout) {
      flushCurrentBuffers();
      currentSection.blocks.push({
        type: "callout",
        tone: callout[1].toLowerCase() as ArticleCalloutTone,
        text: callout[2].trim()
      });
      continue;
    }

    const unorderedItem = line.match(LIST_UNORDERED_PATTERN);
    if (unorderedItem) {
      flushParagraph(paragraphBuffer, currentSection.blocks);
      if (listOrdered === null) {
        listOrdered = false;
      }
      if (listOrdered === false) {
        listItems.push(unorderedItem[1].trim());
        continue;
      }
      flushList(listOrdered, listItems, currentSection.blocks);
      listOrdered = false;
      listItems = [unorderedItem[1].trim()];
      continue;
    }

    const orderedItem = line.match(LIST_ORDERED_PATTERN);
    if (orderedItem) {
      flushParagraph(paragraphBuffer, currentSection.blocks);
      if (listOrdered === null) {
        listOrdered = true;
      }
      if (listOrdered === true) {
        listItems.push(orderedItem[1].trim());
        continue;
      }
      flushList(listOrdered, listItems, currentSection.blocks);
      listOrdered = true;
      listItems = [orderedItem[1].trim()];
      continue;
    }

    const flushed = flushList(listOrdered, listItems, currentSection.blocks);
    listOrdered = flushed.ordered;
    listItems = flushed.items;
    paragraphBuffer.push(line);
  }

  flushCurrentBuffers();

  const hasHeadingSection = sections.length > 1;
  if (!hasHeadingSection && sections[0].blocks.length === 0) {
    return [
      {
        id: "overview",
        title: "Overview",
        level: 2,
        blocks: [{ type: "paragraph", text: normalized }]
      }
    ];
  }

  return sections.filter((section, index) => index === 0 || section.blocks.length > 0);
}

export function estimateReadingTime(text: string, wordsPerMinute = 200): number {
  const words = text.match(WORD_PATTERN)?.length ?? 0;
  return Math.max(1, Math.ceil(words / wordsPerMinute));
}

export function extractLead(summary: string | undefined, content: string): string {
  if (summary && summary.trim()) return summary.trim();
  const sentences = content
    .replace(/\r\n/g, " ")
    .split(/(?<=[.!?])\s+/)
    .map((sentence) => sentence.trim())
    .filter(Boolean);
  return sentences.slice(0, 2).join(" ").trim();
}

export function splitTags(tags: string | undefined): string[] {
  if (!tags) return [];
  return tags
    .split(",")
    .map((item) => item.trim())
    .filter(Boolean);
}
