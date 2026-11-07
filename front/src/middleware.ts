// src/middleware.ts
import { defineMiddleware } from "astro:middleware";
import { hasAnyRole } from "./lib/authGuard";
import { BACKEND_ORIGIN } from "./api/lib/ApiClient";

const API_AUTH_ME = `${BACKEND_ORIGIN}/api/v1/auth/me`;

export const onRequest = defineMiddleware(async (context, next) => {
  const { pathname } = context.url;

  // Si algún elemento solicita /uploads/** directamente al servidor Astro (4321),
  // redirigimos automáticamente al servidor de archivos estáticos del Backend (8080).
  if (pathname.startsWith("/uploads/")) {
    return context.redirect(`${BACKEND_ORIGIN}${pathname}`, 302);
  }

  const isAdminRoute = pathname.startsWith("/admin");

  if (!isAdminRoute) {
    return next();
  }

  const cookieHeader = context.request.headers.get("cookie") || "";

  // Si la cookie access_token está presente en la petición SSR, validamos rol en servidor
  if (cookieHeader.includes("access_token=")) {
    try {
      const res = await fetch(API_AUTH_ME, {
        method: "GET",
        headers: {
          Cookie: cookieHeader,
          Accept: "application/json",
        },
      });

      if (res.ok) {
        const user = (await res.json()) as {
          username?: string;
          roles?: string[];
        };
        (context.locals as any).user = user;

        if (!hasAnyRole(user.roles, ["ADMIN", "RH"])) {
          return context.redirect("/user/account?denied=admin");
        }
      }
    } catch {
      // Si falla la conexión SSR local, AdminLayout valida en el cliente con Axios
    }
  }

  return next();
});
