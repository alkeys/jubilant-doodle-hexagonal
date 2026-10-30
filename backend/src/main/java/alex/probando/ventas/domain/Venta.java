package alex.probando.ventas.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record Venta(
        UUID id,
        UUID clienteId,
        UUID productoId,
        int cantidad,
        BigDecimal precioUnitario,
        BigDecimal total,
        Instant createdAt) {

    public Venta {
        if (clienteId == null) {
            throw new IllegalArgumentException("El cliente no puede ser nulo");
        }
        if (productoId == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
        if (precioUnitario == null || precioUnitario.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio unitario no puede ser negativo o nulo");
        }
        if (total == null || total.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El total no puede ser negativo o nulo");
        }
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public Venta(UUID clienteId, UUID productoId, int cantidad, BigDecimal precioUnitario) {
        this(null, clienteId, productoId, cantidad, precioUnitario, precioUnitario.multiply(BigDecimal.valueOf(cantidad)), Instant.now());
    }
}
