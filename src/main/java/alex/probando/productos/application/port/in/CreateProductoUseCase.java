package alex.probando.productos.application.port.in;

import alex.probando.productos.domain.Producto;

public interface CreateProductoUseCase {
    Producto createProducto(Producto producto);
}
