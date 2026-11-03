import { apiClient } from "../../lib/ApiClient";
import type { ProductoMasVendidoResponse } from "../../types/ventas/ProductoMasVendidoResponse";
import type { VentaRequest } from "../../types/ventas/VentaRequest";
import type { VentaResponse } from "../../types/ventas/VentaResponse";

export interface ListVentasParams {
    clienteId?: string;
    productoId?: string;
}

export class VentasApi {
    private basePath = "/ventas";

    /**
     * GET /api/v1/ventas
     * Listar historial de ventas con filtros opcionales (?clienteId=...&productoId=...)
     */
    async listVentas(params?: ListVentasParams): Promise<VentaResponse[]> {
        const response = await apiClient.get<VentaResponse[]>(this.basePath, { params });
        return response.data;
    }

    /**
     * POST /api/v1/ventas
     * Registrar una nueva venta
     */
    async registrarVenta(request: VentaRequest): Promise<VentaResponse> {
        const response = await apiClient.post<VentaResponse>(this.basePath, request);
        return response.data;
    }

    /**
     * GET /api/v1/ventas/{id}
     * Obtener detalle de una venta por ID
     */
    async getVentaById(id: string): Promise<VentaResponse> {
        const response = await apiClient.get<VentaResponse>(`${this.basePath}/${id}`);
        return response.data;
    }

    /**
     * GET /api/v1/ventas/top-productos
     * Consultar productos más vendidos (Top ventas)
     */
    async getProductosMasVendidos(
        limite: number = 10
    ): Promise<ProductoMasVendidoResponse[]> {
        const response = await apiClient.get<ProductoMasVendidoResponse[]>(
            `${this.basePath}/top-productos`,
            { params: { limite } }
        );
        return response.data;
    }
}

