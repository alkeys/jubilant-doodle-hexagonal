import { apiClient } from "../../lib/ApiClient";
import type { AssignRolesRequest } from "../../types/usuarios/AssignRolesRequest";
import type { UsuarioRequest } from "../../types/usuarios/UsuarioRequest";
import type { UsuarioResponse } from "../../types/usuarios/UsuarioResponse";
import type { UsuarioUpdateRequest } from "../../types/usuarios/UsuarioUpdateRequest";

export class UsuariosApi {
    private basePath = "/usuarios";

    /**
     * POST /api/v1/usuarios
     * Registrar nuevo usuario
     */
    async createUser(request: UsuarioRequest): Promise<UsuarioResponse> {
        const response = await apiClient.post<UsuarioResponse>(this.basePath, request);
        return response.data;
    }

    /**
     * GET /api/v1/usuarios
     * Listar usuarios con filtro opcional por rol (Solo ADMIN y RH)
     */
    async getUsuariosFiltradosPorRol(rol?: string): Promise<UsuarioResponse[]> {
        const response = await apiClient.get<UsuarioResponse[]>(this.basePath, {
            params: rol ? { rol } : undefined,
        });
        return response.data;
    }

    /**
     * GET /api/v1/usuarios/perfil
     * Obtener mi perfil del usuario autenticado
     */
    async getMiPerfil(): Promise<UsuarioResponse> {
        const response = await apiClient.get<UsuarioResponse>(
            `${this.basePath}/perfil`
        );
        return response.data;
    }

    /**
     * GET /api/v1/usuarios/{id}
     * Obtener usuario por ID (Solo ADMIN y RH)
     */
    async getUserById(id: string): Promise<UsuarioResponse> {
        const response = await apiClient.get<UsuarioResponse>(
            `${this.basePath}/${id}`
        );
        return response.data;
    }

    /**
     * PUT /api/v1/usuarios/{id}
     * Actualizar usuario (Solo ADMIN)
     */
    async updateUser(
        id: string,
        request: UsuarioUpdateRequest
    ): Promise<UsuarioResponse> {
        const response = await apiClient.put<UsuarioResponse>(
            `${this.basePath}/${id}`,
            request
        );
        return response.data;
    }

    /**
     * DELETE /api/v1/usuarios/{id}
     * Eliminar usuario (Solo ADMIN)
     */
    async deleteUser(id: string): Promise<void> {
        await apiClient.delete(`${this.basePath}/${id}`);
    }

    /**
     * PATCH /api/v1/usuarios/{id}/roles
     * Asignar roles a usuario (Solo ADMIN)
     */
    async assignRoles(
        id: string,
        request: AssignRolesRequest
    ): Promise<UsuarioResponse> {
        const response = await apiClient.patch<UsuarioResponse>(
            `${this.basePath}/${id}/roles`,
            request
        );
        return response.data;
    }
}

