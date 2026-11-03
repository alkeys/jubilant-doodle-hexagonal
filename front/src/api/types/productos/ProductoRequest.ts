export interface ProductoRequest {
    tiendaId: string;
    categoriaId: string;
    nombre: string;
    descripcion: string;
    talla?: string;
    precio: number;
    stock: number;
    activo?: boolean;
}
