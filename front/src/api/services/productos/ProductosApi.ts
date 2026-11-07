import { apiClient, resolveImageUrl } from "../../lib/ApiClient";
import type { ProductoImagenResponse } from "../../types/productos/ProductoImagenResponse";
import type { ProductoRequest } from "../../types/productos/ProductoRequest";
import type { ProductoResponse } from "../../types/productos/ProductoResponse";
import type { ProductoUpdateRequest } from "../../types/productos/ProductoUpdateRequest";
import type { TiendaInventarioRequest } from "../../types/productos/TiendaInventarioRequest";
import type { TiendaInventarioResponse } from "../../types/productos/TiendaInventarioResponse";

export interface ListProductosParams {
    tiendaId?: string;
    categoriaId?: string;
    buscar?: string;
    incluirInactivos?: boolean;
}

function normalizeImagen(img: ProductoImagenResponse): ProductoImagenResponse {
    return {
        ...img,
        url: resolveImageUrl(img.url),
    };
}

function normalizeProducto(prod: ProductoResponse): ProductoResponse {
    return {
        ...prod,
        imagenes: Array.isArray(prod.imagenes)
            ? prod.imagenes.map(normalizeImagen)
            : [],
    };
}

export class ProductosApi {
    private basePath = "/productos";

    /**
     * GET /api/v1/productos
     * Listar catálogo de productos con filtros opcionales
     */
    async listProductos(params?: ListProductosParams): Promise<ProductoResponse[]> {
        const response = await apiClient.get<ProductoResponse[]>(this.basePath, { params });
        return (response.data ?? []).map(normalizeProducto);
    }

    /**
     * GET /api/v1/productos/principales?pagina=0&limite=10
     * Obtener productos principales del catálogo (Portada)
     */
    async getPrincipalesProductos(
        pagina: number = 0,
        limite: number = 10
    ): Promise<ProductoResponse[]> {
        const response = await apiClient.get<ProductoResponse[]>(
            `${this.basePath}/principales`,
            { params: { pagina, limite } }
        );
        return (response.data ?? []).map(normalizeProducto);
    }

    /**
     * GET /api/v1/productos/{id}
     * Obtener producto por ID
     */
    async getProductoById(id: string): Promise<ProductoResponse> {
        const response = await apiClient.get<ProductoResponse>(`${this.basePath}/${id}`);
        return normalizeProducto(response.data);
    }

    /**
     * POST /api/v1/productos
     * Crear nuevo producto (Solo ADMIN)
     */
    async createProducto(request: ProductoRequest): Promise<ProductoResponse> {
        const response = await apiClient.post<ProductoResponse>(this.basePath, request);
        return normalizeProducto(response.data);
    }

    /**
     * PUT /api/v1/productos/{id}
     * Actualizar datos generales del producto (Solo ADMIN)
     */
    async updateProducto(
        id: string,
        request: ProductoUpdateRequest
    ): Promise<ProductoResponse> {
        const response = await apiClient.put<ProductoResponse>(
            `${this.basePath}/${id}`,
            request
        );
        return normalizeProducto(response.data);
    }

    /**
     * DELETE /api/v1/productos/{id}
     * Eliminar producto (Solo ADMIN)
     */
    async deleteProducto(id: string): Promise<void> {
        await apiClient.delete(`${this.basePath}/${id}`);
    }

    /**
     * POST /api/v1/productos/{id}/tiendas
     * Asignar o actualizar precio y stock en una sucursal (Solo ADMIN)
     */
    async asignarInventario(
        id: string,
        request: TiendaInventarioRequest
    ): Promise<TiendaInventarioResponse> {
        const response = await apiClient.post<TiendaInventarioResponse>(
            `${this.basePath}/${id}/tiendas`,
            request
        );
        return response.data;
    }

    /**
     * DELETE /api/v1/productos/{id}/tiendas/{tiendaId}
     * Remover producto de una sucursal (Solo ADMIN)
     */
    async eliminarInventario(id: string, tiendaId: string): Promise<void> {
        await apiClient.delete(`${this.basePath}/${id}/tiendas/${tiendaId}`);
    }

    /**
     * POST /api/v1/productos/{id}/imagenes
     * Subir imágenes para un producto (Solo ADMIN) - multipart/form-data
     */
    async uploadImagenes(
        id: string,
        imagenes: File[]
    ): Promise<ProductoImagenResponse[]> {
        const formData = new FormData();
        imagenes.forEach((file) => formData.append("imagenes", file));

        const response = await apiClient.post<ProductoImagenResponse[]>(
            `${this.basePath}/${id}/imagenes`,
            formData,
            {
                headers: { "Content-Type": "multipart/form-data" },
            }
        );
        return (response.data ?? []).map(normalizeImagen);
    }

    /**
     * DELETE /api/v1/productos/{id}/imagenes/{imagenId}
     * Eliminar una imagen de un producto (Solo ADMIN)
     */
    async deleteImagen(id: string, imagenId: string): Promise<void> {
        await apiClient.delete(`${this.basePath}/${id}/imagenes/${imagenId}`);
    }

    /**
     * PUT /api/v1/productos/{id}/imagenes/{imagenId}/principal
     * Marcar una imagen como principal (Solo ADMIN)
     */
    async setPrincipalImagen(
        id: string,
        imagenId: string
    ): Promise<ProductoResponse> {
        const response = await apiClient.put<ProductoResponse>(
            `${this.basePath}/${id}/imagenes/${imagenId}/principal`
        );
        return normalizeProducto(response.data);
    }
}
