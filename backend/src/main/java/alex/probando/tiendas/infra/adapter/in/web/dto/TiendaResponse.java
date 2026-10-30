package alex.probando.tiendas.infra.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(name = "TiendaResponse", description = "Representación de una sucursal de la cadena comercial")
@Builder
public record TiendaResponse(
    @Schema(description = "Identificador único (UUID)", example = "123e4567-e89b-12d3-a456-426614174000")
    String id,

    @Schema(description = "Nombre de la sucursal", example = "Sucursal Central")
    String nombre,

    @Schema(description = "Descripción o servicios de la sucursal", example = "Panadería, pastelería y cafetería")
    String descripcion,

    @Schema(description = "Dirección de la sucursal", example = "Av. Principal #123, San Salvador")
    String direccion,

    @Schema(description = "Estado operativo de la sucursal", example = "true")
    boolean activo,

    @Schema(description = "Fecha de registro ISO-8601", example = "2026-10-28T20:00:00Z")
    String creadoEn
) {
}
