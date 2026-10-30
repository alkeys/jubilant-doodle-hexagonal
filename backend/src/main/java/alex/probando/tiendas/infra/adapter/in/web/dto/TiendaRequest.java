package alex.probando.tiendas.infra.adapter.in.web.dto;

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
@Schema(description = "Solicitud para registrar o actualizar una tienda")
public class TiendaRequest {

    @Schema(description = "Nombre de la tienda", example = "Panadería El Rosario")
    @NotBlank(message = "El nombre de la tienda es obligatorio")
    private String nombre;

    @Schema(description = "Descripción de la tienda o catálogo", example = "Especialistas en pan artesanal y repostería")
    private String descripcion;

    @Schema(description = "Dirección física de la tienda", example = "Av. Principal #123, San Salvador")
    private String direccion;
}
