import { apiClient } from "../../lib/ApiClient";
import type { TiendaRequest } from "../../types/tiendas/TiendaRequest";
import type { TiendaResponse } from "../../types/tiendas/TiendaResponse";

export class TiendasApi {
    private basePath = "/tiendas";

    /**
     * GET /api/v1/tiendas
     * Listar sucursales activas de la cadena
     */
    async listTiendasActivas(): Promise<TiendaResponse[]> {
        const response = await apiClient.get<TiendaResponse[]>(this.basePath);
        return response.data;
    }

    /**
     * GET /api/v1/tiendas/{id}
     * Obtener sucursal por ID
     */
    async getTiendaById(id: string): Promise<TiendaResponse> {
        const response = await apiClient.get<TiendaResponse>(`${this.basePath}/${id}`);
        return response.data;
    }

    /**
     * POST /api/v1/tiendas
     * Crear nueva sucursal (Solo ADMIN)
     */
    async createTienda(request: TiendaRequest): Promise<TiendaResponse> {
        const response = await apiClient.post<TiendaResponse>(this.basePath, request);
        return response.data;
    }

    /**
     * PUT /api/v1/tiendas/{id}
     * Actualizar datos de sucursal (Solo ADMIN)
     */
    async updateTienda(
        id: string,
        request: TiendaRequest
    ): Promise<TiendaResponse> {
        const response = await apiClient.put<TiendaResponse>(
            `${this.basePath}/${id}`,
            request
        );
        return response.data;
    }

    /**
     * DELETE /api/v1/tiendas/{id}
     * Dar de baja sucursal (Solo ADMIN)
     */
    async deleteTienda(id: string): Promise<void> {
        await apiClient.delete(`${this.basePath}/${id}`);
    }
}

