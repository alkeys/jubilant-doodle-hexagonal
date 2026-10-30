package alex.probando.ventas.application.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import alex.probando.ventas.domain.ProductoMasVendido;
import alex.probando.ventas.domain.Venta;

public interface VentaRepositoryPort {
    Venta save(Venta venta);
    Optional<Venta> findById(UUID id);
    List<Venta> findAll();
    List<Venta> findByClienteId(UUID clienteId);
    List<Venta> findByProductoId(UUID productoId);
    List<ProductoMasVendido> findTopProductosVendidos(int limite);
}
