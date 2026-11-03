import { apiClient } from "../../lib/ApiClient";
import type { CategoriaRequest } from "../../types/categorias/CategoriaRequest";
import type { CategoriaResponse } from "../../types/categorias/CategoriaResponse";

export class CategoriasApi {
    private basePath = "/categorias";

    /**
     * GET /api/v1/categorias
     * Listar categorías de productos (?todas=true para incluir inactivas)
     */
    async listCategorias(todas: boolean = false): Promise<CategoriaResponse[]> {
        const response = await apiClient.get<CategoriaResponse[]>(this.basePath, {
            params: { todas },
        });
        return response.data;
    }

    /**
     * GET /api/v1/categorias/{id}
     * Obtener categoría por ID
     */
    async getCategoriaById(id: string): Promise<CategoriaResponse> {
        const response = await apiClient.get<CategoriaResponse>(`${this.basePath}/${id}`);
        return response.data;
    }

    /**
     * POST /api/v1/categorias
     * Crear nueva categoría (Solo ADMIN)
     */
    async createCategoria(request: CategoriaRequest): Promise<CategoriaResponse> {
        const response = await apiClient.post<CategoriaResponse>(this.basePath, request);
        return response.data;
    }

    /**
     * PUT /api/v1/categorias/{id}
     * Actualizar categoría (Solo ADMIN)
     */
    async updateCategoria(
        id: string,
        request: CategoriaRequest
    ): Promise<CategoriaResponse> {
        const response = await apiClient.put<CategoriaResponse>(
            `${this.basePath}/${id}`,
            request
        );
        return response.data;
    }

    /**
     * DELETE /api/v1/categorias/{id}
     * Eliminar categoría (Solo ADMIN)
     */
    async deleteCategoria(id: string): Promise<void> {
        await apiClient.delete(`${this.basePath}/${id}`);
    }
}

