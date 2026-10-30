package alex.probando.productos.infra.adapter.in.web.dto;

import java.math.BigDecimal;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(name = "ProductoResponse", description = "Información completa de un producto del catálogo")
@Builder
public record ProductoResponse(
    @Schema(description = "Identificador único (UUID)", example = "123e4567-e89b-12d3-a456-426614174000")
    String id,

    @Schema(description = "ID de la sucursal/tienda donde se vende", example = "a1b2c3d4-e89b-12d3-a456-426614174000")
    String tiendaId,

    @Schema(description = "ID de la categoría a la que pertenece", example = "b2c3d4e5-e89b-12d3-a456-426614174000")
    String categoriaId,

    @Schema(description = "Nombre del producto", example = "Croissant de Mantequilla")
    String nombre,

    @Schema(description = "Descripción detallada del producto", example = "Croissant francés horneado a diario con mantequilla de primera calidad")
    String descripcion,

    @Schema(description = "Precio unitario", example = "2.50")
    BigDecimal precio,

    @Schema(description = "Cantidad disponible en stock", example = "45")
    int stock,

    @Schema(description = "Estado de disponibilidad para clientes", example = "true")
    boolean activo,

    @Schema(description = "Lista de imágenes asociadas a la publicación (mínimo recomendado: 3)", example = "[]")
    List<ProductoImagenResponse> imagenes,

    @Schema(description = "Fecha de creación ISO-8601", example = "2026-10-28T20:00:00Z")
    String creadoEn,

    @Schema(description = "Fecha de última modificación ISO-8601", example = "2026-10-28T22:00:00Z")
    String actualizadoEn
) {
}
