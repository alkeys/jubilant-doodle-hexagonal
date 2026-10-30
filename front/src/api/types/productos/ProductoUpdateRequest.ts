/**
 * ProductoUpdateRequest
Collapse allobject

Solicitud para actualizar un producto existente

    tiendaId
    Expand allstringuuid
    categoriaId
    Expand allstringuuid
    nombre
    Expand allstring
    descripcion
    Expand allstring
    precio
    Expand allnumber≥ 0
    stock
    Expand allinteger≥ 0int32
    activo
    Expand allboolean
 */
export interface ProductoUpdateRequest {
    tiendaId: string;
    categoriaId: string;
    nombre: string;
    descripcion: string;
    precio: number;
    stock: number;
    activo: boolean;
}
