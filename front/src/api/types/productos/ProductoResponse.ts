/**
 * ProductoResponse
Collapse allobject

Información completa de un producto del catálogo

    id
    Expand allstring
    tiendaId
    Expand allstring
    categoriaId
    Expand allstring
    nombre
    Expand allstring
    descripcion
    Expand allstring
    precio
    Expand allnumber
    stock
    Expand allintegerint32
    activo
    Expand allboolean
    imagenes
    Expand allarray<object>
    creadoEn
    Expand allstring
    actualizadoEn
    Expand allstring
 */

import type { ProductoImagenResponse } from "./ProductoImagenResponse";

export interface ProductoResponse {
    id: string;
    tiendaId: string;
    categoriaId: string;
    nombre: string;
    descripcion: string;
    precio: number;
    stock: number;
    activo: boolean;
    imagenes: ProductoImagenResponse[];
    creadoEn: string;
    actualizadoEn: string;
}
