package alex.probando.ventas.infra.adapter.in.web.dto;

import java.math.BigDecimal;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(name = "VentaResponse", description = "Información detallada de una venta registrada")
@Builder
public record VentaResponse(
        @Schema(description = "ID único de la venta", example = "77777777-7777-7777-7777-777777777771")
        UUID id,

        @Schema(description = "ID del cliente comprador", example = "22222222-2222-2222-2222-222222222222")
        UUID clienteId,

        @Schema(description = "ID del producto adquirido", example = "55555555-5555-5555-5555-555555555551")
        UUID productoId,

        @Schema(description = "Cantidad vendida", example = "2")
        int cantidad,

        @Schema(description = "Precio unitario cobrado", example = "2.50")
        BigDecimal precioUnitario,

        @Schema(description = "Total de la venta", example = "5.00")
        BigDecimal total,

        @Schema(description = "Fecha y hora de registro", example = "2026-10-30T10:00:00Z")
        String createdAt
) {
}
