import type { ApiError } from "./types";

const isLocalHost =
  typeof window !== "undefined" &&
  (window.location.hostname === "localhost" ||
    window.location.hostname === "127.0.0.1");

const DEFAULT_API_BASE_URL = isLocalHost
  ? "http://localhost:8080/api/v1"
  : "https://psycho-dwq2.onrender.com/api/v1";

const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL ?? DEFAULT_API_BASE_URL;

export class HttpError extends Error {
  status: number;
  payload?: ApiError;

  constructor(message: string, status: number, payload?: ApiError) {
    super(message);
    this.name = "HttpError";
    this.status = status;
    this.payload = payload;
  }
}

function sleep(ms: number) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

function buildUrl(path: string, query?: Record<string, string | number | undefined | null>) {
  const normalizedPath = path.startsWith("/") ? path : `/${path}`;
  const url = new URL(`${API_BASE_URL}${normalizedPath}`);
  if (query) {
    Object.entries(query).forEach(([key, value]) => {
      if (value !== undefined && value !== null && value !== "") {
        url.searchParams.set(key, String(value));
      }
    });
  }
  return url.toString();
}

async function parseBody(response: Response): Promise<unknown> {
  const text = await response.text();
  if (!text) return null;
  try {
    return JSON.parse(text) as unknown;
  } catch {
    return text;
  }
}

export async function apiRequest<T>(
  path: string,
  init: RequestInit = {},
  token?: string,
  query?: Record<string, string | number | undefined | null>
): Promise<T> {
  const method = (init.method ?? "GET").toUpperCase();
  const maxRetries = method === "GET" ? 1 : 0;
  const headers = new Headers(init.headers ?? {});
  if (!headers.has("Content-Type") && init.body) {
    headers.set("Content-Type", "application/json");
  }
  if (token) {
    headers.set("Authorization", `Bearer ${token}`);
  }

  for (let attempt = 0; attempt <= maxRetries; attempt += 1) {
    try {
      const response = await fetch(buildUrl(path, query), {
        ...init,
        headers
      });

      const body = await parseBody(response);

      if (!response.ok) {
        const apiError = body && typeof body === "object" ? (body as ApiError) : undefined;
        throw new HttpError(
          apiError?.message ?? `Request failed with status ${response.status}`,
          response.status,
          apiError
        );
      }

      return body as T;
    } catch (error) {
      const retryable =
        attempt < maxRetries &&
        ((error instanceof HttpError && error.status >= 500) || error instanceof TypeError);

      if (retryable) {
        await sleep(700);
        continue;
      }
      throw error;
    }
  }

  throw new Error("Unexpected request state");
}

export function toErrorMessage(error: unknown): string {
  if (error instanceof HttpError) {
    if (error.payload?.validationErrors) {
      const details = Object.entries(error.payload.validationErrors)
        .map(([field, message]) => `${field}: ${message}`)
        .join("; ");
      return `${error.message}. ${details}`;
    }
    return error.message;
  }
  if (error instanceof Error) return error.message;
  return "Unexpected error";
}
