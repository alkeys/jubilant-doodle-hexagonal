export interface TiendaInventarioRequest {
    tiendaId: string;
    precio: number;
    stock: number;
    activo?: boolean;
}

