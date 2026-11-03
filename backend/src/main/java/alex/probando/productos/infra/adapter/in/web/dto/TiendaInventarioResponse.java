package alex.probando.productos.infra.adapter.in.web.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(name = "TiendaInventarioResponse", description = "Disponibilidad, precio y stock de un producto en una sucursal específica")
@Builder
public record TiendaInventarioResponse(
    @Schema(description = "ID único del registro de inventario", example = "123e4567-e89b-12d3-a456-426614174000")
    String id,

    @Schema(description = "ID de la sucursal", example = "33333333-3333-3333-3333-333333333331")
    String tiendaId,

    @Schema(description = "Nombre de la sucursal", example = "Panadería Central")
    String tiendaNombre,

    @Schema(description = "Precio en esta sucursal", example = "2.50")
    BigDecimal precio,

    @Schema(description = "Stock disponible en esta sucursal", example = "45")
    int stock,

    @Schema(description = "Disponibilidad activa en esta sucursal", example = "true")
    boolean activo
) {
}
