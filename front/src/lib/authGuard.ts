// src/lib/authGuard.ts
import { AuthApi } from "../api/services/auth/AuthApi";
import type { AuthResponse } from "../api/types/auth/AuthResponse";

export interface SessionUser {
  username: string;
  roles: string[];
}

/**
 * Normaliza un rol quitando el prefijo opcional "ROLE_"
 * Ej: "ROLE_ADMIN" -> "ADMIN", "CLIENTE" -> "CLIENTE"
 */
export function normalizeRole(role: string): string {
  return role.replace(/^ROLE_/i, "").trim().toUpperCase();
}

/**
 * Verifica si una lista de roles incluye alguno de los roles requeridos
 */
export function hasAnyRole(
  userRoles: string[] | undefined,
  requiredRoles: string[]
): boolean {
  if (!userRoles || userRoles.length === 0) return false;
  const normalizedUser = userRoles.map(normalizeRole);
  return requiredRoles.some((req) =>
    normalizedUser.includes(normalizeRole(req))
  );
}

/**
 * Determina si el usuario tiene permisos administrativos (ADMIN o RH)
 */
export function isAdminOrRh(roles: string[] | undefined): boolean {
  return hasAnyRole(roles, ["ADMIN", "RH"]);
}

/**
 * Determina si el usuario es estrictamente ADMIN
 */
export function isStrictAdmin(roles: string[] | undefined): boolean {
  return hasAnyRole(roles, ["ADMIN"]);
}

/**
 * Obtiene la sesión guardada en localStorage (rápido para UI inicial)
 */
export function getStoredSession(): SessionUser | null {
  try {
    const raw = localStorage.getItem("auth_user");
    if (!raw) return null;
    const parsed = JSON.parse(raw);
    if (!parsed?.username) return null;
    return {
      username: parsed.username,
      roles: Array.isArray(parsed.roles) ? parsed.roles : [],
    };
  } catch {
    return null;
  }
}

/**
 * Verifica la sesión real contra GET /api/v1/auth/me y sincroniza localStorage
 */
export async function verifySessionWithApi(): Promise<AuthResponse | null> {
  const authApi = new AuthApi();
  try {
    const me = await authApi.me();
    if (me && me.username) {
      localStorage.setItem("auth_user", JSON.stringify(me));
      return me;
    }
    localStorage.removeItem("auth_user");
    return null;
  } catch {
    localStorage.removeItem("auth_user");
    return null;
  }
}

