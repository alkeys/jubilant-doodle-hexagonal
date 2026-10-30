package alex.probando.productos.application.port.in;

import java.util.UUID;

import alex.probando.productos.domain.Producto;

public interface UpdateProductoUseCase {
    Producto updateProducto(UUID id, Producto producto);
}
