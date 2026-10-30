/**
 * ProductoRequest
Collapse allobject

Solicitud para registrar un nuevo producto

    tiendaId
    Expand allstringuuid
    categoriaId
    Expand allstringuuid
    nombre
    Expand allstring≥ 1 characters
    descripcion
    Expand allstring
    precio
    Expand allnumber≥ 0
    stock
    Expand allinteger≥ 0int32
    activo
    Expand allboolean
 */
export interface ProductoRequest {
    tiendaId: string;
    categoriaId: string;
    nombre: string;
    descripcion: string;
    precio: number;
    stock: number;
    activo: boolean;
}
