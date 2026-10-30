package alex.probando.productos.infra.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(name = "ProductoImagenResponse", description = "Imagen asociada a la publicación de un producto")
@Builder
public record ProductoImagenResponse(
    @Schema(description = "Identificador único de la imagen", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    String id,

    @Schema(description = "URL accesible de la imagen", example = "/uploads/productos/3fa85f64.jpg")
    String url,

    @Schema(description = "Indica si es la imagen de portada o principal", example = "true")
    boolean esPrincipal,

    @Schema(description = "Posición u orden de visualización", example = "0")
    int orden,

    @Schema(description = "Fecha de subida ISO-8601", example = "2026-10-28T22:00:00Z")
    String creadoEn
) {
}
