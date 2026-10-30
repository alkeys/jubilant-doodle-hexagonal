package alex.probando.ventas.application.port.in;

import java.util.List;

import alex.probando.ventas.domain.ProductoMasVendido;

public interface GetProductosMasVendidosUseCase {
    List<ProductoMasVendido> getProductosMasVendidos(int limite);
}
