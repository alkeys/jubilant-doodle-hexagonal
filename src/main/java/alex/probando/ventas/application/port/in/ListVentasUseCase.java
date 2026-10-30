package alex.probando.ventas.application.port.in;

import java.util.List;
import java.util.UUID;

import alex.probando.ventas.domain.Venta;

public interface ListVentasUseCase {
    List<Venta> listVentas(UUID clienteId, UUID productoId);
}
