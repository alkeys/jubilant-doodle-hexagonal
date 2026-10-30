package alex.probando.productos.application.port.in;

import java.util.UUID;

public interface DeleteProductoUseCase {
    void deleteProducto(UUID id);
}
