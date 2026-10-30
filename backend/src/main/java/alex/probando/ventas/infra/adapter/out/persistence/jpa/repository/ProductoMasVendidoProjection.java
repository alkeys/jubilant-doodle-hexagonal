package alex.probando.ventas.infra.adapter.out.persistence.jpa.repository;

import java.math.BigDecimal;
import java.util.UUID;

public interface ProductoMasVendidoProjection {
    UUID getProductoId();
    Long getTotalCantidad();
    BigDecimal getTotalRecaudado();
}
