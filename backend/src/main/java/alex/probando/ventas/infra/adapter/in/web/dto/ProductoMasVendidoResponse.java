package alex.probando.ventas.infra.adapter.in.web.dto;

import java.math.BigDecimal;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(name = "ProductoMasVendidoResponse", description = "Reporte de producto con mayor cantidad de ventas")
@Builder
public record ProductoMasVendidoResponse(
        @Schema(description = "ID único del producto", example = "55555555-5555-5555-5555-555555555551")
        UUID productoId,

        @Schema(description = "Nombre comercial del producto", example = "Baguette Artesanal")
        String nombreProducto,

        @Schema(description = "Total de unidades vendidas", example = "42")
        long totalCantidadVendida,

        @Schema(description = "Monto monetario acumulado", example = "105.00")
        BigDecimal totalRecaudado
) {
}
