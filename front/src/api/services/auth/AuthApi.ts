import { apiClient } from "../../lib/ApiClient";
import type { AuthResponse } from "../../types/auth/AuthResponse";
import type { LoginRequest } from "../../types/auth/LoginRequest";

export class AuthApi {
    private basePath = "/auth";

    /**
     * POST /api/v1/auth/login
     * Iniciar sesión y establecer cookie HttpOnly 'access_token'
     */
    async login(request: LoginRequest): Promise<AuthResponse> {
        const response = await apiClient.post<AuthResponse>(
            `${this.basePath}/login`,
            request
        );
        return response.data;
    }

    /**
     * POST /api/v1/auth/logout
     * Cerrar sesión e invalidar cookie HttpOnly
     */
    async logout(): Promise<AuthResponse> {
        const response = await apiClient.post<AuthResponse>(`${this.basePath}/logout`);
        return response.data;
    }

    /**
     * GET /api/v1/auth/me
     * Consultar sesión actual del usuario autenticado
     */
    async me(): Promise<AuthResponse> {
        const response = await apiClient.get<AuthResponse>(`${this.basePath}/me`);
        return response.data;
    }
}

