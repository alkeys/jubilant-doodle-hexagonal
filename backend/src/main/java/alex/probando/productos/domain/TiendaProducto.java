package alex.probando.productos.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TiendaProducto(
        UUID id,
        UUID tiendaId,
        String tiendaNombre,
        BigDecimal precio,
        int stock,
        boolean activo,
        Instant creadoEn,
        Instant actualizadoEn) {

    public TiendaProducto {
        if (tiendaId == null) {
            throw new IllegalArgumentException("El ID de la tienda no puede ser nulo");
        }
        if (precio == null || precio.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio en la tienda debe ser mayor o igual a 0");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
        if (creadoEn == null) {
            creadoEn = Instant.now();
        }
        if (actualizadoEn == null) {
            actualizadoEn = Instant.now();
        }
    }

    public TiendaProducto(UUID tiendaId, String tiendaNombre, BigDecimal precio, int stock) {
        this(null, tiendaId, tiendaNombre, precio, stock, true, Instant.now(), Instant.now());
    }

    public TiendaProducto withStock(int nuevoStock) {
        return new TiendaProducto(
                this.id,
                this.tiendaId,
                this.tiendaNombre,
                this.precio,
                nuevoStock,
                this.activo,
                this.creadoEn,
                Instant.now());
    }

    public TiendaProducto withPrecio(BigDecimal nuevoPrecio) {
        return new TiendaProducto(
                this.id,
                this.tiendaId,
                this.tiendaNombre,
                nuevoPrecio,
                this.stock,
                this.activo,
                this.creadoEn,
                Instant.now());
    }
}
