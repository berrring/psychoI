import type { UserRole } from "./types";

export function isDoctorRole(role: UserRole | undefined): boolean {
  return role === "DOCTOR" || role === "PSYCHOLOGIST";
}

export function isClientRole(role: UserRole | undefined): boolean {
  return role === "PATIENT" || role === "CLIENT";
}

export function getWorkspacePath(role: UserRole | undefined): string {
  if (role === "ADMIN") return "/admin-app";
  if (isDoctorRole(role)) return "/doctor-app";
  return "/client-app";
}
