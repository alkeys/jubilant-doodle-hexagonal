import type { ProductoImagenResponse } from "./ProductoImagenResponse";
import type { TiendaInventarioResponse } from "./TiendaInventarioResponse";

export type { TiendaInventarioResponse };

export interface ProductoResponse {
    id: string;
    categoriaId: string;
    nombre: string;
    descripcion: string;
    talla?: string;
    precio: number;
    precioMin?: number;
    precioMax?: number;
    stock: number;
    stockTotal?: number;
    activo: boolean;
    imagenes: ProductoImagenResponse[];
    tiendas?: TiendaInventarioResponse[];
    creadoEn: string;
    actualizadoEn: string;
}
