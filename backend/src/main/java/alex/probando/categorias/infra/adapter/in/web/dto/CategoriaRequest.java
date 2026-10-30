package alex.probando.categorias.infra.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Solicitud para registrar o actualizar una categoría de productos")
public class CategoriaRequest {

    @Schema(description = "Nombre único de la categoría", example = "Panadería Artesanal")
    @NotBlank(message = "El nombre de la categoría es obligatorio")
    private String nombre;

    @Schema(description = "Descripción de la categoría de productos", example = "Pan dulce, baguette, pan campesino y masa madre")
    private String descripcion;

    @Schema(description = "Estado de visibilidad en el catálogo (opcional, true por defecto)", example = "true")
    private Boolean activo;
}
