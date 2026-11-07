import axios from "axios";

/** Origen del servidor Backend (para archivos estáticos como /uploads/**) */
export const BACKEND_ORIGIN = "http://localhost:8080";

/** URL base de la API REST */
const apiUrl = `${BACKEND_ORIGIN}/api/v1/`;

/**
 * Normaliza la URL de una imagen devuelta por el backend.
 * Si es una ruta relativa como `/uploads/productos/uuid.jpg`, le antepone `http://localhost:8080`.
 */
export function resolveImageUrl(url?: string | null): string {
    if (!url) return "";
    const trimmed = url.trim();
    if (!trimmed) return "";
    if (trimmed.startsWith("http://") || trimmed.startsWith("https://") || trimmed.startsWith("data:")) {
        return trimmed;
    }
    if (trimmed.startsWith("/")) {
        return `${BACKEND_ORIGIN}${trimmed}`;
    }
    return `${BACKEND_ORIGIN}/${trimmed}`;
}

/** Instancia preconfigurada de Axios */
export const apiClient = axios.create({
    baseURL: apiUrl,
    headers: { "Content-Type": "application/json" },
    withCredentials: true,
});

// Interceptor en el cliente para adjuntar también el header Authorization: Bearer <token>
// como respaldo de la cookie HttpOnly en peticiones PUT / DELETE / PATCH cross-origin.
apiClient.interceptors.request.use((config) => {
    if (typeof window !== "undefined") {
        try {
            let token = localStorage.getItem("auth_token");
            if (!token) {
                const rawUser = localStorage.getItem("auth_user");
                if (rawUser) {
                    const parsed = JSON.parse(rawUser);
                    if (parsed?.token) token = parsed.token;
                }
            }
            if (token && config.headers) {
                config.headers.Authorization = `Bearer ${token}`;
            }
        } catch {
            // Ignorar si localStorage no está disponible
        }
    }
    return config;
});
