package alex.probando.categorias.infra.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(name = "CategoriaResponse", description = "Representación de una categoría del catálogo de la tienda")
@Builder
public record CategoriaResponse(
    @Schema(description = "Identificador único (UUID)", example = "123e4567-e89b-12d3-a456-426614174000")
    String id,

    @Schema(description = "Nombre de la categoría", example = "Panadería Artesanal")
    String nombre,

    @Schema(description = "Descripción de la categoría", example = "Pan dulce, baguette, pan campesino y masa madre")
    String descripcion,

    @Schema(description = "Estado activo en el catálogo", example = "true")
    boolean activo
) {
}
