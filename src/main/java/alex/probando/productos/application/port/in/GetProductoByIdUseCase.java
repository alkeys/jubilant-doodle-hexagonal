package alex.probando.productos.application.port.in;

import java.util.UUID;

import alex.probando.productos.domain.Producto;

public interface GetProductoByIdUseCase {
    Producto getProductoById(UUID id);
}
