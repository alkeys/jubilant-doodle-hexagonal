package alex.probando.ventas.domain;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductoMasVendido(
        UUID productoId,
        String nombreProducto,
        long totalCantidadVendida,
        BigDecimal totalRecaudado) {
}
