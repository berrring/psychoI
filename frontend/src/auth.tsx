import { createContext, useContext, useEffect, useMemo, useState } from "react";
import { apiRequest } from "./api";
import type { AuthResponse, UserRole } from "./types";

const SESSION_STORAGE_KEY = "psycho.session.v1";

type Session = AuthResponse;

interface AuthContextValue {
  session: Session | null;
  token: string | undefined;
  isAuthenticated: boolean;
  login: (email: string, password: string) => Promise<void>;
  register: (name: string, email: string, password: string, phone?: string) => Promise<void>;
  logout: () => void;
  hasRole: (...roles: UserRole[]) => boolean;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

function readSession(): Session | null {
  const raw = localStorage.getItem(SESSION_STORAGE_KEY);
  if (!raw) return null;
  try {
    return JSON.parse(raw) as Session;
  } catch {
    return null;
  }
}

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [session, setSession] = useState<Session | null>(null);

  useEffect(() => {
    setSession(readSession());
  }, []);

  const value = useMemo<AuthContextValue>(
    () => ({
      session,
      token: session?.token,
      isAuthenticated: !!session?.token,
      async login(email: string, password: string) {
        const data = await apiRequest<AuthResponse>("/auth/login", {
          method: "POST",
          body: JSON.stringify({ email, password })
        });
        localStorage.setItem(SESSION_STORAGE_KEY, JSON.stringify(data));
        setSession(data);
      },
      async register(name: string, email: string, password: string, phone?: string) {
        const data = await apiRequest<AuthResponse>("/auth/register", {
          method: "POST",
          body: JSON.stringify({ name, email, password, phone })
        });
        localStorage.setItem(SESSION_STORAGE_KEY, JSON.stringify(data));
        setSession(data);
      },
      logout() {
        localStorage.removeItem(SESSION_STORAGE_KEY);
        setSession(null);
      },
      hasRole(...roles: UserRole[]) {
        if (!session) return false;
        return roles.includes(session.role);
      }
    }),
    [session]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) throw new Error("useAuth must be used within AuthProvider");
  return context;
}
